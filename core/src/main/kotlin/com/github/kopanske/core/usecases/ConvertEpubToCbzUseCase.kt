package com.github.kopanske.core.usecases

import com.github.kopanske.core.ports.ConvertEpubToCbzUseCasePort
import com.github.kopanske.core.ports.EpubPort
import com.github.kopanske.core.ports.FileAccessPort
import com.github.kopanske.core.ports.UserOutputPort

class ConvertEpubToCbzUseCase(
    private val fileAccess: FileAccessPort,
    private val epubProcessor: EpubPort,
    private val userOutput: UserOutputPort,
) : ConvertEpubToCbzUseCasePort {
    override fun process(
        inputPath: String,
        outputPath: String,
    ) {
        val ePubs =
            fileAccess.getEbooks(
                startPath = inputPath,
                inputExtension = "epub",
                outputPath = outputPath,
                outputExtension = "cbz",
            )

        if (ePubs.isEmpty()) {
            userOutput.displayMessage("◯ No ebooks found to convert! ◯")
            return
        }

        fileAccess.createMissingDirectories(ePubs)

        ePubs.forEach { ePub ->
            userOutput.displayMessage("📖: ${ePub.name}")
            epubProcessor
                .extractImagesToCbz(
                    epubPath = ePub.inputPath,
                    outputCbzPath = ePub.outputPath,
                ).fold(
                    ifLeft = { userOutput.displayMessage(" ❌ ${it.message}") },
                    ifRight = { userOutput.displayMessage(" ✅") },
                )
        }
    }
}
