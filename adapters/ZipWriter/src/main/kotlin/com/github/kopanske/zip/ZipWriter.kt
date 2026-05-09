package com.github.kopanske.zip

import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import com.github.kopanske.core.model.Comic
import com.github.kopanske.core.ports.ArchiveCreatorPort
import com.github.kopanske.core.ports.ArchiveCreatorPort.ArchiveError
import java.io.BufferedOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ZipWriter : ArchiveCreatorPort {
    override fun writeArchive(comic: Comic): Either<ArchiveError, Unit> =
        either {
            catch(
                block = {
                    ZipOutputStream(BufferedOutputStream(File(comic.outputPath).outputStream())).use { outputZip ->
                        comic.pictures.forEach { picture ->
                            outputZip.putNextEntry(ZipEntry(picture.name))
                            outputZip.write(picture.data)
                            outputZip.closeEntry()
                        }
                    }
                },
                catch = { e -> raise(ArchiveError.FileAccessError("Error: ${e.message}", e)) },
            )
        }
}
