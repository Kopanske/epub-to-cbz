package com.github.kopanske.core.ports

interface UserOutputPort {
    fun displayMessageNl(message: String)

    fun displayMessage(message: String)
}
