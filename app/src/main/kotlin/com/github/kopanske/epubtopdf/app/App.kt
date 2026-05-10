package com.github.kopanske.epubtopdf.app

import com.github.kopanske.cli.CliAdapter
import com.github.kopanske.core.usecases.ConvertEpubToCbzUseCase
import com.github.kopanske.epup.EpubAdapter
import com.github.kopanske.fileaccess.FileAccessAdapter
import com.github.kopanske.terminaloutput.TerminalOutputAdapter
import com.github.kopanske.zip.ZipWriter

fun main(args: Array<String>) {
    // Instantiating adapters (wiring)
    val userOutput = TerminalOutputAdapter()
    val useCase =
        ConvertEpubToCbzUseCase(
            fileAccess = FileAccessAdapter(),
            epubProcessor = EpubAdapter(),
            userOutput = userOutput,
            archiveCreator = ZipWriter(),
        )
    val userInput = CliAdapter(userOutput, useCase)

    // Starting the application
    userInput.checkInput(args)
}
