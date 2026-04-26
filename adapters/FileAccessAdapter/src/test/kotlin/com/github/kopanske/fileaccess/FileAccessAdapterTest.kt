package com.github.kopanske.fileaccess

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.shouldBe
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeText

class FileAccessAdapterTest :
    FunSpec({

        val sut = FileAccessAdapter()

        lateinit var tempDir: java.nio.file.Path

        beforeTest {
            tempDir = createTempDirectory("fileaccess-test")
        }

        afterTest {
            tempDir.toFile().deleteRecursively()
        }

        test("finds files recursively and generates correct output paths") {
            val sourceRoot = tempDir.resolve("input").createDirectories()
            val nested = sourceRoot.resolve("sub").createDirectories()

            val fileA = sourceRoot.resolve("A.EPUB")
            val fileB = nested.resolve("B.epub")
            val ignored = sourceRoot.resolve("C.txt")

            fileA.writeText("a")
            fileB.writeText("b")
            ignored.writeText("c")

            val startPath = sourceRoot.toAbsolutePath().toString()
            val outputRoot = tempDir.resolve("output").toAbsolutePath().toString()

            val result =
                sut.getEbooks(
                    startPath = startPath,
                    inputExtension = ".epub",
                    outputPath = outputRoot,
                    outputExtension = ".pdf",
                )

            result shouldHaveSize 2

            val byName = result.associateBy { it.name }
            byName shouldContainKey "A.EPUB"
            byName shouldContainKey "B.epub"

            val expectedOutputA =
                outputRoot +
                    fileA
                        .toFile()
                        .absolutePath
                        .removePrefix(startPath)
                        .substringBeforeLast(".") + ".pdf"
            val expectedOutputB =
                outputRoot +
                    fileB
                        .toFile()
                        .absolutePath
                        .removePrefix(startPath)
                        .substringBeforeLast(".") + ".pdf"

            byName.getValue("A.EPUB").inputPath shouldBe fileA.toFile().absolutePath
            byName.getValue("B.epub").inputPath shouldBe fileB.toFile().absolutePath
            byName.getValue("A.EPUB").outputPath shouldBe expectedOutputA
            byName.getValue("B.epub").outputPath shouldBe expectedOutputB
        }

        test("returns an empty list if no file matches the extension") {
            val sourceRoot = tempDir.resolve("input").createDirectories()
            sourceRoot.resolve("A.txt").writeText("x")

            val result =
                sut.getEbooks(
                    startPath = sourceRoot.toAbsolutePath().toString(),
                    inputExtension = "epub",
                    outputPath = tempDir.resolve("output").toAbsolutePath().toString(),
                    outputExtension = "pdf",
                )

            result.shouldBeEmpty()
        }
    })
