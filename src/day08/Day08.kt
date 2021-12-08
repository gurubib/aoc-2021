package day08

import day08.SevenSegmentDigit.*
import readInputForDay

fun main() {
    fun part1(input: List<String>): Int {
        val notes = Notes.parse(input)
        return notes.entries
            .flatMap { it.outputs }
            .count { SevenSegmentDigit.uniques().map { sseg -> sseg.length }.contains(it.segments.length) }
    }

    fun part2(input: List<String>): Int {
        val notes = Notes.parse(input)

        notes.entries.forEach { e ->
            e.wiring.putAll(determineWiring(e.patterns))
        }

        return notes.entries.sumOf { e ->
            val realDigits = e.outputs.map { e.decode(it) }
            SevenSegmentDigit.toInt(realDigits)
        }
    }

    val testInput = readInputForDay(8, true)
    check(part1(testInput) == 26)
    check(part2(testInput) == 61229)

    val input = readInputForDay(8)
    println(part1(input))
    println(part2(input))
}

data class Digit(
    val segments: String
)

class Entry(
    val patterns: List<Digit>,
    val outputs: List<Digit>,
    val wiring: MutableMap<Char, Char> = mutableMapOf(),
) {
    companion object {
        fun parse(input: String): Entry {
            val parts = input.split("|").map { it.trim() }
            val patterns = parts.first().split(" ").map { Digit(it) }
            val output = parts.last().split(" ").map { Digit(it) }
            return Entry(patterns, output)
        }
    }

    fun decode(input: Digit): Digit {
        return input.segments.toList().mapNotNull { wiring[it] }.let { Digit(it.sorted().joinToString("")) }
    }
}

class Notes(
    val entries: List<Entry>,
) {
    companion object {
        fun parse(input: List<String>): Notes {
            val entries = input.map { Entry.parse(it) }
            return Notes(entries)
        }
    }
}

enum class SevenSegmentDigit(
    val num: Int,
    val segments: String,
    val unique: Boolean = false,
) {
    ZERO(0, "abcefg"),
    ONE(1, "cf", true),
    TWO(2, "acdeg"),
    THREE(3, "acdfg"),
    FOUR(4, "bcdf", true),
    FIVE(5, "abdfg"),
    SIX(6, "abdefg"),
    SEVEN(7, "acf", true),
    EIGHT(8, "abcdefg", true),
    NINE(9, "abcdfg"),
    ;

    val length get() = segments.length

    companion object {
        fun uniques(): Set<SevenSegmentDigit> = values().filter { it.unique }.toSet()
        private fun segmentFor(input: String): SevenSegmentDigit = values().first { it.segments == input }
        fun toInt(input: List<Digit>): Int {
            return input.map { segmentFor(it.segments).num }.joinToString("").toInt()
        }
    }
}

fun determineWiring(patterns: List<Digit>): Map<Char, Char> {
    val known = mutableMapOf(
        ONE to patterns.first { it.segments.length == ONE.length },
        FOUR to patterns.first { it.segments.length == FOUR.length },
        SEVEN to patterns.first { it.segments.length == SEVEN.length },
        EIGHT to patterns.first { it.segments.length == EIGHT.length },
    )

    val wiringForA = known[SEVEN]?.segments?.toSet()?.subtract(
        (known[ONE]?.segments?.toSet() ?: setOf()).toSet()
    )?.first() ?: 'a'

    known[TWO] = patterns
        .filter { it.segments.length == 5 }
        .first { (it.segments.toSet().subtract((known[FOUR]?.segments?.toSet() ?: setOf()).toSet())).size == 3 }

    val wiringForF = known[ONE]?.segments?.toSet()?.subtract(
        (known[TWO]?.segments?.toSet() ?: setOf()).toSet()
    )?.first() ?: 'f'

    val wiringForC = known[SEVEN]?.segments?.toSet()?.subtract(setOf(wiringForA, wiringForF))?.first() ?: 'c'

    val wiringForB = known[FOUR]?.segments?.toSet()
        ?.subtract((known[TWO]?.segments?.toSet() ?: setOf()).toSet())
        ?.subtract(setOf(wiringForF))
        ?.first() ?: 'b'

    val wiringForD = known[FOUR]?.segments?.toSet()?.subtract(setOf(wiringForB, wiringForC, wiringForF))?.first() ?: 'd'

    val wiringForG = patterns
        .filter { it.segments.length == 6 }
        .map { it.segments.toSet().subtract(setOf(wiringForA, wiringForB, wiringForC, wiringForD, wiringForF)) }
        .first { it.size == 1 }
        .first()

    val wiringForE = known[EIGHT]?.segments?.toSet()
        ?.subtract(setOf(wiringForA, wiringForB, wiringForC, wiringForD, wiringForF, wiringForG))
        ?.first() ?: 'e'

    return mapOf(
        wiringForA to 'a',
        wiringForB to 'b',
        wiringForC to 'c',
        wiringForD to 'd',
        wiringForE to 'e',
        wiringForF to 'f',
        wiringForG to 'g',
    )
}


