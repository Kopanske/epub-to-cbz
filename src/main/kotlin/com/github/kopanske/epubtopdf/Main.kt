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
        println("Reading EPUB: $epubFile")
        extractImagesToCbz(
            epubPath = epubFile.absolutePath,
            outputCbzPath = outputFileName,
            onProgress = { current, total ->
                printProgress(current, total)
            },
        ).fold(
            ifLeft = { println(it.message) },
            ifRight = { println(" Done!") },
        )
    }
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
