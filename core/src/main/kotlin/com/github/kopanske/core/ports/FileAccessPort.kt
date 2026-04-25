package com.github.kopanske.core.ports

fun interface FileAccessPort {
    fun findPathsForExtension(
        startPath: String,
        extension: String,
    ): List<String>
}
