package com.github.kopanske.epup

import com.github.kopanske.core.ports.ProgressPort
import com.github.kopanske.epup.testdata.createTestEpubWithSpine
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.io.File
import java.util.zip.ZipFile
import kotlin.io.path.createTempFile

class ExtractImagesToCbzTest :
    StringSpec(
        {

            class NoOutputAdapter : ProgressPort {
                override fun reportProgress(
                    current: Int,
                    total: Int,
                ) {
                }
            }

            val adapter = EpubAdapter(NoOutputAdapter())

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
                    createTempFile(suffix = ".cbz")
                        .toFile()

                adapter.extractImagesToCbz(epub.path, output.path)

                ZipFile(output).use { zip ->
                    val entry = zip.getEntry("0001.png")
                    entry shouldNotBe null

                    zip.getInputStream(entry).readBytes().size shouldBe 5
                }
                File(epub.absolutePath).deleteRecursively()
                File(output.absolutePath).deleteRecursively()
            }
        },
    )
