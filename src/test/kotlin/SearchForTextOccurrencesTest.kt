package org.example

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchForTextOccurrencesTest {

    @Test
    fun `finds single occurrence in one file`() = runTest {
        val dir = Files.createTempDirectory("searchTest")
        val file = dir.resolve("file1.txt")
        Files.writeString(file, "Hello world")

        val results = searchForTextOccurrences("world", dir).toList()

        assertEquals(1, results.size)
        val occ = results.first()
        assertEquals(file, occ.file)
        assertEquals(1, occ.line)
        assertEquals(6, occ.offset)
    }

    @Test
    fun `finds multiple occurrences across lines`() = runTest {
        val dir = Files.createTempDirectory("searchTest")
        val file = dir.resolve("multi.txt")
        Files.writeString(file, """
            foo bar
            bar foo bar
            test
        """.trimIndent())

        val results = searchForTextOccurrences("bar", dir).toList()
        assertEquals(3, results.size)

        val grouped = results.groupBy { it.line }
        assertEquals(listOf(4), grouped[1]?.map { it.offset })
        assertEquals(listOf(0, 8), grouped[2]?.map { it.offset })
    }

    @Test
    fun `returns empty flow when string not found`() = runTest {
        val dir = Files.createTempDirectory("searchTest")
        val file = dir.resolve("nohits.txt")
        Files.writeString(file, "Nothing here")

        val results = searchForTextOccurrences("missing", dir).toList()
        assertTrue(results.isEmpty())
    }

    @Test
    fun `throws error if not a directory`() {
        val file = Files.createTempFile("searchTestFile", ".txt")

        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                searchForTextOccurrences("x", file).toList()
            }
        }
        assertTrue(exception.message!!.contains("Path must be a directory"))
    }
    @Test
    fun `throws error on empty search string`() {
        val dir = Files.createTempDirectory("searchTest")

        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                searchForTextOccurrences("", dir).toList()
            }
        }
        assertTrue(exception.message!!.contains("Search string cannot be empty"))
    }
}