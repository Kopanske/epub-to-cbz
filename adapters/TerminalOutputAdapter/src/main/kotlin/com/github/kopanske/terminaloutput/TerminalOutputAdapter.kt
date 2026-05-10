package com.github.kopanske.terminaloutput

import com.github.kopanske.core.ports.UserOutputPort

class TerminalOutputAdapter : UserOutputPort {
    override fun displayMessageNl(message: String) {
        println(message)
    }

    override fun displayMessage(message: String) {
        print(message)
    }
}
