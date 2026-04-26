package com.github.kopanske.terminaloutput

import com.github.kopanske.core.ports.ProgressPort
import com.github.kopanske.core.ports.UserOutputPort
import com.github.kopanske.terminaloutput.Colors.BLUE
import com.github.kopanske.terminaloutput.Colors.GREEN
import com.github.kopanske.terminaloutput.Colors.RESET
import com.github.kopanske.terminaloutput.Colors.YELLOW

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
        val bar = "$BLUE█".repeat(filled) + "$BLUE░".repeat(BAR_LENGTH - filled)

        print(
            "\r$YELLOW[$bar$YELLOW]$GREEN ${percent.padded(3)}% (${current.padded(4)} / ${total.padded(4)})$RESET",
        )
    }

    companion object {
        private const val BAR_LENGTH = 40

        private fun Int.padded(chars: Int) = toString().padStart(chars, ' ')
    }
}
