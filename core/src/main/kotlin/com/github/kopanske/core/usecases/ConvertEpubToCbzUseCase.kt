package com.github.kopanske.core.usecases

import arrow.core.getOrElse
import com.github.kopanske.core.ports.ArchiveCreatorPort
import com.github.kopanske.core.ports.ConvertEpubToCbzUseCasePort
import com.github.kopanske.core.ports.EpubPort
import com.github.kopanske.core.ports.FileAccessPort
import com.github.kopanske.core.ports.UserOutputPort

class ConvertEpubToCbzUseCase(
    private val fileAccess: FileAccessPort,
    private val epubProcessor: EpubPort,
    private val userOutput: UserOutputPort,
    private val archiveCreator: ArchiveCreatorPort,
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
            userOutput.displayMessageNl("◯ No ebooks found to convert! ◯")
            return
        }

        fileAccess.createMissingDirectories(ePubs)

        ePubs.forEach { ePub ->
            userOutput.displayMessage("📖 ${ePub.name} 📤")
            val comic =
                epubProcessor
                    .extractImages(
                        epubPath = ePub.inputPath,
                        outputCbzPath = ePub.outputPath,
                    ).getOrElse {
                        userOutput.displayMessageNl(" ❌ ${it.message}")
                        return@forEach
                    }
            userOutput.displayMessage(" 📄 (${comic.pictures.size + 1})")
            userOutput.displayMessage(" 📦")
            archiveCreator.writeArchive(comic).getOrElse {
                userOutput.displayMessageNl(" ❌ ${it.message}")
                return@forEach
            }
            userOutput.displayMessageNl(" ✅")
        }
    }
}
