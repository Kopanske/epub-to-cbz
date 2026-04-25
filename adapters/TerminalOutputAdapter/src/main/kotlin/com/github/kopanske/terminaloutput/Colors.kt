package com.github.kopanske.terminaloutput

enum class Colors(
    val value: String,
) {
    RESET("\u001B[0m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    ;

    override fun toString(): String = value
}
