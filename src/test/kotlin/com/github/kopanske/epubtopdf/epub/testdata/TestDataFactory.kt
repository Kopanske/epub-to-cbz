package com.github.kopanske.epubtopdf.epub.testdata

import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.outputStream

fun createTestEpubWithSpine(
    xhtmlFiles: Map<String, String>, // filename -> content
    images: Map<String, ByteArray>, // filename -> bytes
): File {
    val tmp = kotlin.io.path.createTempFile(suffix = ".epub")

    ZipOutputStream(tmp.outputStream()).use { zip ->

        // Required mimetype
        zip.putNextEntry(ZipEntry("mimetype"))
        zip.write("application/epub+zip".toByteArray())
        zip.closeEntry()

        // container.xml
        zip.putNextEntry(ZipEntry("META-INF/container.xml"))
        zip.write(
            """
            <?xml version="1.0"?>
            <container version="1.0" xmlns="urn:oasis:names:tc:opendocument:xmlns:container">
              <rootfiles>
                <rootfile full-path="content.opf" media-type="application/oebps-package+xml"/>
              </rootfiles>
            </container>
            """.trimIndent().toByteArray(),
        )
        zip.closeEntry()

        // content.opf
        val manifestItems =
            buildString {
                xhtmlFiles.keys.forEach { name ->
                    append("""<item id="$name" href="$name" media-type="application/xhtml+xml"/>""")
                }
                images.keys.forEach { name ->
                    append("""<item id="$name" href="$name" media-type="image/png"/>""")
                }
            }

        val spineItems = xhtmlFiles.keys.joinToString("") { """<itemref idref="$it"/>""" }

        zip.putNextEntry(ZipEntry("content.opf"))
        zip.write(
            """
            <package xmlns="http://www.idpf.org/2007/opf" version="3.0">
              <manifest>$manifestItems</manifest>
              <spine>$spineItems</spine>
            </package>
            """.trimIndent().toByteArray(),
        )
        zip.closeEntry()

        // XHTML files
        xhtmlFiles.forEach { (name, content) ->
            zip.putNextEntry(ZipEntry(name))
            zip.write(content.toByteArray())
            zip.closeEntry()
        }

        // Images
        images.forEach { (name, bytes) ->
            zip.putNextEntry(ZipEntry(name))
            zip.write(bytes)
            zip.closeEntry()
        }
    }

    return tmp.toFile()
}
