package com.github.kopanske.fileaccess

import com.github.kopanske.core.ports.FileAccessPort
import java.nio.file.Paths

class FileAccessAdapter : FileAccessPort {
    override fun findPathsForExtension(
        startPath: String,
        extension: String,
    ): List<String> {
        val cleanedExtensions = extension.removePrefix(".")
        return Paths
            .get(startPath)
            .toAbsolutePath()
            .toFile()
            .walk()
            .filter {
                it.isFile && it.name.endsWith(".$cleanedExtensions", true)
            }.toList()
            .map { it.absolutePath }
    }
}
