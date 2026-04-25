package com.github.kopanske.core.usecases

import com.github.kopanske.core.ports.ConvertEpubToCbzUseCasePort
import com.github.kopanske.core.ports.EpubPort
import com.github.kopanske.core.ports.FileAccessPort
import com.github.kopanske.core.ports.UserOutputPort

class ConvertEpubToCbzUseCase(
    private val fileAccess: FileAccessPort,
    private val epub: EpubPort,
    private val userOutput: UserOutputPort,
) : ConvertEpubToCbzUseCasePort {
    override fun process(
        inputPath: String,
        outputPath: String,
    ) {
        val ePubs = fileAccess.findPathsForExtension(inputPath, "epub")

        if (ePubs.isEmpty()) {
            userOutput.displayMessage("No ebooks found to convert!")
            return
        }

        ePubs.forEach { epubFile ->
            val outputFileName = outputPath + epubFile.removePrefix(inputPath).substringBeforeLast(".") + ".cbz"
            val ePubDisplayName = epubFile.substringAfterLast("\\")
            userOutput.displayMessage("📖: $ePubDisplayName")
            epub
                .extractImagesToCbz(
                    epubPath = epubFile,
                    outputCbzPath = outputFileName,
                ).fold(
                    ifLeft = { userOutput.displayMessage(" ❌ ${it.message}") },
                    ifRight = { userOutput.displayMessage(" ✅") },
                )
        }
    }
}
