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

    val manifest = mutableMapOf<String, String>()
    val manifestItems = opfDoc.getElementsByTagName("item")
    for (i in 0 until manifestItems.length) {
        val item = manifestItems.item(i)
        val id = item.attributes.getNamedItem("id").nodeValue
        val href = item.attributes.getNamedItem("href").nodeValue
        manifest[id] = href
    }

    val spine = mutableListOf<String>()
    val spineItems = opfDoc.getElementsByTagName("itemref")
    for (i in 0 until spineItems.length) {
        val idref =
            spineItems
                .item(i)
                .attributes
                .getNamedItem("idref")
                .nodeValue
        manifest[idref]?.let { spine.add(it) }
    }

    val imagePaths = mutableListOf<String>()
    val imgRegex = Regex("""<img[^>]+src=["']([^"']+)["']""")

    for (xhtml in spine) {
        val fullPath = if (opfDir.isEmpty()) xhtml else "$opfDir/$xhtml"
        val entry = zip.getEntry(fullPath) ?: continue
        val content = zip.getInputStream(entry).bufferedReader().readText()

        // per page only unique src-values
        val pageImages = mutableSetOf<String>()

        imgRegex.findAll(content).forEach { match ->
            val src = match.groupValues[1]
            if (pageImages.add(src)) {
                val normalized = normalizePathForZip(opfDir, xhtml, src)
                imagePaths.add(normalized)
            }
        }
    }

    return imagePaths
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
}

fun extractImagesToCbz(
    epubPath: String,
    outputCbzPath: String,
) {
    val zip = ZipFile(epubPath)
    val imagesWithDuplicates = listImagesInReadingOrder(epubPath)

    // Remove duplicates, keep the order
    val images = imagesWithDuplicates.distinct()

    val total = images.size
    var processed = 0

    ZipOutputStream(File(outputCbzPath).outputStream()).use { outZip ->

        images.forEachIndexed { index, imagePath ->
            val entry = zip.getEntry(imagePath)
            if (entry == null) {
                println("WARN: Bild nicht gefunden: $imagePath")
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
            printProgress(processed, total)
        }
    }

    println("\nDone! CBZ created: $outputCbzPath")
}

fun printProgress(
    done: Int,
    total: Int,
) {
    val percent = (done * 100) / total
    val barLength = 40
    val filled = (percent * barLength) / 100
    val bar = "█".repeat(filled) + " ".repeat(barLength - filled)

    print("\r[$bar] $percent% ($done / $total)")
}
