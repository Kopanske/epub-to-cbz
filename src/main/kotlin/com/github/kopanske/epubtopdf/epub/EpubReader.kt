package com.github.kopanske.epubtopdf.epub

import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import javax.xml.parsers.DocumentBuilderFactory

fun listImagesInReadingOrder(epubPath: String): List<String> {
    val zip = ZipFile(epubPath)

    val containerEntry = zip.getEntry("META-INF/container.xml")
    val containerDoc =
        DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
            .parse(zip.getInputStream(containerEntry))

    val opfPath =
        containerDoc
            .getElementsByTagName("rootfile")
            .item(0)
            .attributes
            .getNamedItem("full-path")
            .nodeValue

    val opfDir = opfPath.substringBeforeLast("/", "")

    val opfEntry = zip.getEntry(opfPath)
    val opfDoc =
        DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
            .parse(zip.getInputStream(opfEntry))

    val manifestItems = opfDoc.getElementsByTagName("item")
    val manifest =
        (0 until manifestItems.length).associate { i ->
            val item = manifestItems.item(i)
            val id = item.attributes.getNamedItem("id").nodeValue
            val href = item.attributes.getNamedItem("href").nodeValue
            id to href
        }

    val spineItems = opfDoc.getElementsByTagName("itemref")
    val spine =
        (0 until spineItems.length).mapNotNull { i ->
            val idref =
                spineItems
                    .item(i)
                    .attributes
                    .getNamedItem("idref")
                    .nodeValue

            manifest[idref]
        }

    val imgRegex = Regex("""<img[^>]+src=["']([^"']+)["']""")

    val imagePaths =
        spine.flatMap { xhtml ->
            val fullPath = if (opfDir.isEmpty()) xhtml else "$opfDir/$xhtml"
            val entry = zip.getEntry(fullPath) ?: return@flatMap emptyList()

            val content =
                zip
                    .getInputStream(entry)
                    .bufferedReader()
                    .readText()

            imgRegex
                .findAll(content)
                .map { it.groupValues[1] } // extract src
                .distinct() // per page one unique src
                .map { src -> normalizePathForZip(opfDir, xhtml, src) }
                .toList()
        }
    zip.close()
    return imagePaths.distinct()
}

// Returns the real zip-path of the picture file
fun normalizePathForZip(
    opfDir: String,
    xhtmlPath: String,
    imgSrc: String,
): String {
    // Ignore absolute URLs
    if (imgSrc.contains("://")) return imgSrc

    // EPUB-internal absolute paths
    if (imgSrc.startsWith("/")) return imgSrc.removePrefix("/")

    // Root directory of the XHTML document
    val xhtmlDir = xhtmlPath.substringBeforeLast("/", "")

    val prefix =
        listOf(opfDir, xhtmlDir)
            .filter { it.isNotEmpty() }
            .joinToString("/")

    return "$prefix/$imgSrc"
        .replace("//", "/")
        .removePrefix("/")
}

fun extractImagesToCbz(
    epubPath: String,
    outputCbzPath: String,
    onProgress: (current: Int, total: Int) -> Unit = { _, _ -> },
) {
    val zip = ZipFile(epubPath)

    val images = listImagesInReadingOrder(epubPath)

    val total = images.size
    var processed = 0

    ZipOutputStream(File(outputCbzPath).outputStream()).use { outZip ->

        images.forEachIndexed { index, imagePath ->
            val entry = zip.getEntry(imagePath)
            if (entry == null) {
                println("WARN: Picture not found: $imagePath")
                return@forEachIndexed
            }

            val ext = imagePath.substringAfterLast(".", "bin")
            val newName = "%04d.%s".format(index + 1, ext)

            val newEntry = ZipEntry(newName)
            outZip.putNextEntry(newEntry)

            zip.getInputStream(entry).use { input ->
                input.copyTo(outZip)
            }

            outZip.closeEntry()

            processed++
            onProgress(processed, total)
        }
    }
    zip.close()
}
