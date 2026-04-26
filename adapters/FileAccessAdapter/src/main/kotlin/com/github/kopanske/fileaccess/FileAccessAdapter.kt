package com.github.kopanske.fileaccess

import com.github.kopanske.core.model.EBookDescriptor
import com.github.kopanske.core.ports.FileAccessPort
import java.nio.file.Files
import java.nio.file.Paths

class FileAccessAdapter : FileAccessPort {
    override fun getEbooks(
        startPath: String,
        inputExtension: String,
        outputPath: String,
        outputExtension: String,
    ): List<EBookDescriptor> {
        val cleanedInputExtension = inputExtension.trimStart('.')
        val cleanedOutputExtension = outputExtension.trimStart('.')
        val absoluteStartPath = Paths.get(startPath).toAbsolutePath().normalize()
        val absoluteOutputPath = Paths.get(outputPath).toAbsolutePath().normalize()

        return absoluteStartPath
            .toFile()
            .walkTopDown()
            .filter { it.isFile && it.extension.equals(cleanedInputExtension, ignoreCase = true) }
            .map { file ->
                val filePath = file.toPath().toAbsolutePath().normalize()
                val relativePath = absoluteStartPath.relativize(filePath)

                val convertedFileName = "${file.nameWithoutExtension}.$cleanedOutputExtension"
                val targetRelativePath =
                    relativePath.parent?.resolve(convertedFileName) ?: Paths.get(convertedFileName)

                EBookDescriptor(
                    inputPath = file.absolutePath,
                    outputPath = absoluteOutputPath.resolve(targetRelativePath).toString(),
                    name = file.name,
                )
            }.toList()
    }

    override fun createMissingDirectories(filePaths: List<EBookDescriptor>) {
        filePaths
            .map { it.outputPath }
            .asSequence()
            .mapNotNull {
                Paths
                    .get(it)
                    .toAbsolutePath()
                    .normalize()
                    .parent
            }.distinct()
            .forEach { Files.createDirectories(it) }
    }
}
