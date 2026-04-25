package com.github.kopanske.core.ports

fun interface ConvertEpubToCbzUseCasePort {
    fun process(
        inputPath: String,
        outputPath: String,
    )
}
