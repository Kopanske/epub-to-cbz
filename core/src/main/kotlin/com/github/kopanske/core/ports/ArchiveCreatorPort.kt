package com.github.kopanske.core.ports

import arrow.core.Either
import com.github.kopanske.core.model.Comic

interface ArchiveCreatorPort {
    sealed class ArchiveError(
        open val message: String,
        open val cause: Throwable? = null,
    ) {
        data class FileAccessError(
            override val message: String,
            override val cause: Throwable?,
        ) : ArchiveError(message, cause)
    }

    fun writeArchive(comic: Comic): Either<ArchiveError, Unit>
}
