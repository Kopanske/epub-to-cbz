package com.github.kopanske.epubtopdf.fileaccess

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

fun retrieveFilePathsForExtension(
    startDirectory: String,
    fileExtension: String,
): List<File> {
    val cleanedExtensions = fileExtension.removePrefix(".")
    return Paths
        .get(startDirectory)
        .toAbsolutePath()
        .toFile()
        .walk()
        .filter {
            it.isFile && it.name.endsWith(".$cleanedExtensions", true)
        }.toList()
}
