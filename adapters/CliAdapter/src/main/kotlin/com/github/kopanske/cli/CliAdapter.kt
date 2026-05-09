package com.github.kopanske.cli

import com.github.kopanske.core.ports.ConvertEpubToCbzUseCasePort
import com.github.kopanske.core.ports.UserInputPort
import com.github.kopanske.core.ports.UserOutputPort

class CliAdapter(
    private val output: UserOutputPort,
    private val useCase: ConvertEpubToCbzUseCasePort,
) : UserInputPort {
    override fun checkInput(args: Array<String>) {
        if (args.size != 2) {
            output.displayMessageNl("Usage: epub2cbz <input directory> <output directory>")
            return
        }

        val inputDir = args[0]
        val outputDir = args[1]

        useCase.process(inputDir, outputDir)
    }
}
