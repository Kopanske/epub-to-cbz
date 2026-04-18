package com.github.kopanske.epubtopdf.epub

sealed class EbookError(
    open val message: String,
    open val cause: Throwable?,
) {
    data class FileAccessError(
        override val message: String,
        override val cause: Throwable?,
    ) : EbookError(message, cause)

    data class FileNotfoundError(
        override val message: String,
        override val cause: Throwable?,
    ) : EbookError(message, cause)
}
