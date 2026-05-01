package com.github.kopanske.epup

import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import com.github.kopanske.core.ports.EpubPort
import com.github.kopanske.core.ports.EpubPort.EbookError
import com.github.kopanske.core.ports.ProgressPort
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import javax.xml.parsers.DocumentBuilderFactory

class EpubAdapter(
    private val progressReport: ProgressPort,
) : EpubPort {
    override fun extractImagesToCbz(
        epubPath: String,
        outputCbzPath: String,
    ): Either<EbookError, Unit> =
        either {
            val images = listImagesInReadingOrder(epubPath).bind()

            catch(
                block = {
                    ZipFile(epubPath).use { inputZip ->
                        ZipOutputStream(File(outputCbzPath).outputStream()).use { outputZip ->

                            images.forEachIndexed { index, imagePath ->
                                val entry = inputZip.getEntry(imagePath)
                                ensureNotNull(entry) { EbookError.FileNotfoundError("Could not fine image $imagePath") }

                                val ext = imagePath.substringAfterLast(".")
                                val newName = "%04d.%s".format(index + 1, ext)

                                val newEntry = ZipEntry(newName)
                                outputZip.putNextEntry(newEntry)

                                inputZip.getInputStream(entry).use { input ->
                                    input.copyTo(outputZip)
                                }

                                outputZip.closeEntry()

                                progressReport.reportProgress(index + 1, images.size)
                            }
                        }
                    }
                },
                catch = { error ->
                    raise(EbookError.FileAccessError("Error: ${error.message}", error))
                },
            )
        }

    private fun listImagesInReadingOrder(epubPath: String): Either<EbookError, List<String>> =
        either {
            catch(
                block = {
                    ZipFile(epubPath).use { zip ->
                        val containerEntry =
                            ensureNotNull(zip.getEntry("META-INF/container.xml")) {
                                EbookError.FileNotfoundError("Could not find META-INF/container.xml")
                            }

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

                        val opfEntry =
                            ensureNotNull(zip.getEntry(opfPath)) {
                                EbookError.FileNotfoundError("Could not find OPF file $opfPath")
                            }

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

                        // Extract cover picture from metadata
                        val coverImagePath =
                            (0 until opfDoc.getElementsByTagName("meta").length)
                                .firstNotNullOfOrNull { i ->
                                    val meta = opfDoc.getElementsByTagName("meta").item(i)
                                    if (meta.attributes.getNamedItem("name")?.nodeValue == "cover") {
                                        manifest[meta.attributes.getNamedItem("content")?.nodeValue]
                                    } else {
                                        null
                                    }
                                }?.let { normalizePathForZip(opfDir, "", it) }
                                ?.takeIf { zip.getEntry(it) != null }

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

                        val spineImages =
                            spine
                                .flatMap { xhtml ->
                                    val xhtmlPath = normalizePathForZip(opfDir, "", xhtml)
                                    val entry = zip.getEntry(xhtmlPath) ?: return@flatMap emptyList()

                                    val content =
                                        zip
                                            .getInputStream(entry)
                                            .bufferedReader()
                                            .use { it.readText() }

                                    imgRegex
                                        .findAll(content)
                                        .map { it.groupValues[1] }
                                        .map { it.substringBefore('#').substringBefore('?') }
                                        .filter { it.isNotBlank() }
                                        .filterNot { it.startsWith("data:") || it.contains("://") }
                                        .map { src -> normalizePathForZip(opfDir, xhtml, src) }
                                        .filter { imagePath -> zip.getEntry(imagePath) != null }
                                        .distinct()
                                        .toList()
                                }.distinct()

                        listOfNotNull(coverImagePath) + spineImages
                    }
                },
                catch = { error ->
                    raise(EbookError.FileAccessError(error.message ?: "Could not access $epubPath", error))
                },
            )
        }

    // Returns the real zip-path of the picture file
    private fun normalizePathForZip(
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
            .split("/")
            .fold(mutableListOf<String>()) { acc, segment ->
                when (segment) {
                    ".." -> {
                        if (acc.isNotEmpty()) acc.removeLast()
                    }

                    ".", "" -> { // skip
                    }

                    else -> {
                        acc.add(segment)
                    }
                }
                acc
            }.joinToString("/")
    }
}
