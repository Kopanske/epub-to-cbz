package com.github.kopanske.epubtopdf

import com.github.kopanske.epubtopdf.epub.extractImagesToCbz
import com.github.kopanske.epubtopdf.fileaccess.retrieveFilePathsForExtension

fun main(args: Array<String>) {
    if (args.size != 2) {
        println("Usage: epub2cbz <input directory> <output directory>")
        return
    }

    val inputDir = args[0]
    val outputDir = args[1]

    val epubFiles = retrieveFilePathsForExtension(inputDir, "epub")
    if (epubFiles.isEmpty()) {
        println("No ebooks found to convert!")
    }
    epubFiles.forEach { epubFile ->
        val outputFileName = outputDir + "\\" + epubFile.name.substringBeforeLast(".") + ".cbz"
        println("📖: ${Colors.YELLOW}${epubFile.name}${Colors.RESET}")
        extractImagesToCbz(
            epubPath = epubFile.absolutePath,
            outputCbzPath = outputFileName,
            onProgress = { current, total ->
                printProgress(current, total)
            },
        ).fold(
            ifLeft = { println(" ❌ ${it.message}") },
            ifRight = { println(" ✅") },
        )
    }
}

fun printProgress(
    done: Int,
    total: Int,
) {
    val percent = (done * 100) / total
    val filled = (percent * BAR_LENGTH) / 100
    val bar = "${Colors.BLUE}█".repeat(filled) + "${Colors.BLUE}░".repeat(BAR_LENGTH - filled)

    print("\r${Colors.YELLOW}[$bar${Colors.YELLOW}]${Colors.GREEN} $percent% ($done / $total)${Colors.RESET}")
}

const val BAR_LENGTH = 40

enum class Colors(
    val value: String,
) {
    RESET("\u001B[0m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
}
