package com.github.kopanske.core.ports

import com.github.kopanske.core.model.EBookDescriptor

interface FileAccessPort {
    fun getEbooks(
        startPath: String,
        inputExtension: String,
        outputPath: String,
        outputExtension: String,
    ): List<EBookDescriptor>

    fun createMissingDirectories(filePaths: List<EBookDescriptor>)
}
