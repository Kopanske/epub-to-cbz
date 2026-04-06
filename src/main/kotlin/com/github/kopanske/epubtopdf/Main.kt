package com.github.kopanske.epubtopdf

import com.github.kopanske.epubtopdf.epub.extractImagesToCbz

fun main(args: Array<String>) {
    if (args.size != 2) {
        println("Usage: epub2cbz <input.epub> <output.cbz>")
        return
    }

    val input = args[0]
    val output = args[1]

    println("Reading EPUB: $input")
    extractImagesToCbz(input, output)
    println("Done! CBZ created: $output")
}
