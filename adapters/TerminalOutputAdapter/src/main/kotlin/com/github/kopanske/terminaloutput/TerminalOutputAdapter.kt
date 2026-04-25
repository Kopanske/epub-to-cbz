package com.github.kopanske.terminaloutput

import com.github.kopanske.core.ports.ProgressPort
import com.github.kopanske.core.ports.UserOutputPort

class TerminalOutputAdapter :
    UserOutputPort,
    ProgressPort {
    override fun displayMessage(message: String) {
        println(message)
    }

    override fun reportProgress(
        current: Int,
        total: Int,
    ) {
        val percent = (current * 100) / total
        val filled = (percent * BAR_LENGTH) / 100
        val bar = "${Colors.BLUE}█".repeat(filled) + "${Colors.BLUE}░".repeat(BAR_LENGTH - filled)

        print(
            "\r${Colors.YELLOW}[$bar${Colors.YELLOW}]${Colors.GREEN} $percent% ($current / $total)${Colors.RESET}",
        )
    }

    companion object {
        private const val BAR_LENGTH = 40
    }
}
