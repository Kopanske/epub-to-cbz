package com.github.kopanske.core.ports

import arrow.core.Either
import com.github.kopanske.core.model.Comic

interface EpubPort {
    sealed class EbookError(
        open val message: String,
        open val cause: Throwable? = null,
    ) {
        data class FileAccessError(
            override val message: String,
            override val cause: Throwable?,
        ) : EbookError(message, cause)

        data class FileNotfoundError(
            override val message: String,
            override val cause: Throwable? = null,
        ) : EbookError(message, cause)
    }

    fun extractImages(
        epubPath: String,
        outputCbzPath: String,
    ): Either<EbookError, Comic>
}
