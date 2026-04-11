package com.github.kopanske.epubtopdf.epub

import com.github.kopanske.epubtopdf.epub.testdata.createTestEpubWithSpine
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.zip.ZipFile

class ExtractImagesToCbzTest :
    StringSpec({

        "extractImagesToCbz creates a correct CBZ" {
            val epub =
                createTestEpubWithSpine(
                    xhtmlFiles =
                        mapOf(
                            "page.xhtml" to """<img src="pic.png"/>""",
                        ),
                    images =
                        mapOf(
                            "pic.png" to ByteArray(5) { 7 },
                        ),
                )

            val output =
                kotlin.io.path
                    .createTempFile(suffix = ".cbz")
                    .toFile()

            extractImagesToCbz(epub.path, output.path)

            ZipFile(output).use { zip ->
                val entry = zip.getEntry("0001.png")
                entry shouldNotBe null

                zip.getInputStream(entry).readBytes().size shouldBe 5
            }
        }
    })
