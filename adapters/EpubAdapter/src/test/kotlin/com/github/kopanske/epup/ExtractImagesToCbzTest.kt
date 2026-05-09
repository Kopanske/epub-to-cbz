package com.github.kopanske.epup

import com.github.kopanske.epup.testdata.createTestEpubWithSpine
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.io.File
import kotlin.io.path.createTempFile

class ExtractImagesToCbzTest :
    StringSpec(
        {
            val adapter = EpubAdapter()

            "extractImagesToCbz creates a correct CBZ" {
                val expectedSize = 5
                val epub =
                    createTestEpubWithSpine(
                        xhtmlFiles =
                            mapOf(
                                "page.xhtml" to """<img src="pic.png"/>""",
                            ),
                        images =
                            mapOf(
                                "pic.png" to ByteArray(expectedSize) { 7 },
                            ),
                    )

                val output = createTempFile(suffix = ".cbz").toFile()

                val comic = adapter.extractImages(epub.path, output.path)
                with(comic.getOrNull()) {
                    shouldNotBeNull()
                    pictures shouldHaveSize 1
                    with(pictures.first()) {
                        name shouldBe "0001.png"
                        data shouldHaveSize expectedSize
                    }
                }

                File(epub.absolutePath).deleteRecursively()
            }
        },
    )
