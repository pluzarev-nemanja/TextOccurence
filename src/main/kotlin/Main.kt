package org.example

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.nio.file.Files
import java.nio.file.Path

interface Occurrence {
    val file: Path
    val line: Int
    val offset: Int
}

data class OccurrenceImpl(
    override val file: Path,
    override val line: Int,
    override val offset: Int
) : Occurrence

fun searchForTextOccurrences(
    stringToSearch: String,
    directory: Path
): Flow<Occurrence> = channelFlow {
    require(Files.isDirectory(directory)) { "Path must be a directory: $directory" }
    require(stringToSearch.isNotEmpty()) { "Search string cannot be empty" }
    Files.walk(directory).use { paths ->
        paths.filter { Files.isRegularFile(it) && Files.isReadable(it) }.forEach { file ->
            launch(Dispatchers.IO) {
                Files.newBufferedReader(file).useLines { lines ->
                    lines.forEachIndexed { lineIndex, line ->
                        var searchIndex = line.indexOf(stringToSearch)
                        while (searchIndex >= 0) {
                            send(OccurrenceImpl(file, lineIndex + 1, searchIndex))
                            searchIndex = line.indexOf(stringToSearch, searchIndex + 1)
                        }
                    }
                }
            }
        }
    }
}.flowOn(Dispatchers.IO)