package com.github.kopanske.core.ports

fun interface ProgressPort {
    fun reportProgress(
        current: Int,
        total: Int,
    )
}
