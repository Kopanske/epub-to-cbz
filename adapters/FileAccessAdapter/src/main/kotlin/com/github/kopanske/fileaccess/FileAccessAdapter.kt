package com.github.kopanske.fileaccess

import com.github.kopanske.core.model.EBookDescriptor
import com.github.kopanske.core.ports.FileAccessPort
import java.nio.file.Paths

class FileAccessAdapter : FileAccessPort {
    override fun getEbooks(
        startPath: String,
        inputExtension: String,
        outputPath: String,
        outputExtension: String,
    ): List<EBookDescriptor> {
        val cleanedInputExtension = inputExtension.removePrefix(".")
        val cleanedOutputExtension = outputExtension.removePrefix(".")
        return Paths
            .get(startPath)
            .toAbsolutePath()
            .toFile()
            .walk()
            .filter {
                it.isFile && it.name.endsWith(".$cleanedInputExtension", true)
            }.toList()
            .map {
                EBookDescriptor(
                    inputPath = it.absolutePath,
                    outputPath =
                        outputPath +
                            it.absolutePath
                                .removePrefix(startPath)
                                .substringBeforeLast(".") +
                            ".$cleanedOutputExtension",
                    name = it.name,
                )
            }
    }
}
