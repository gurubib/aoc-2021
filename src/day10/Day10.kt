package day10

import day10.Interpreter.State.*
import readInputForDay
import java.util.*

fun main() {
    fun part1(input: List<String>): Int {
        return input.flatMap { Interpreter().interpret(it) }.sum()
    }

    fun part2(input: List<String>): Long {
        val autocompleteScores = input.map { Interpreter().autoComplete(it).fold(0L) { acc, e -> acc * 5L + e } }
            .filter { it != 0L }
            .sorted()

        return autocompleteScores[autocompleteScores.size / 2]
    }

    val testInput = readInputForDay(10, true)
    check(part1(testInput) == 26397)
    check(part2(testInput) == 288957L)

    val input = readInputForDay(10)
    println(part1(input))
    println(part2(input))
}

class Interpreter(
    private var current: State = INITIAL,
    private val previousStates: Deque<State> = ArrayDeque()
) {

    fun autoComplete(line: String): List<Int> {
        var valid = true
        line.toList().forEach {
            valid = valid && interpretChar(it)
        }

        return if (valid) {
            (listOf(current) + ArrayDeque(previousStates).toMutableList()).map { it.autoCompleteScore }.dropLast(1)
        } else {
            listOf()
        }
    }

    fun interpret(line: String): List<Int> {
        line.toList().forEach {
            val valid = interpretChar(it)
            if (valid.not()) {
                return listOf(State.scoreFor(it))
            }
        }

        return listOf()
    }

    private fun interpretChar(char: Char): Boolean {
        return when (current) {
            INITIAL -> stepStateFromInitial(char)
            else -> stepStateFromOpened(char)
        }
    }

    private fun stepStateFromInitial(char: Char): Boolean {
        if (openingChars().contains(char).not()) { return false }

        return when (char) {
            '(' -> {
                previousStates.push(current)
                current = PAREN_OPENED
                true
            }
            '[' -> {
                previousStates.push(current)
                current = BRACKET_OPENED
                true
            }
            '{' -> {
                previousStates.push(current)
                current = CURLY_OPENED
                true
            }
            '<' -> {
                previousStates.push(current)
                current = ANGLE_OPENED
                true
            }
            else -> false
        }
    }

    private fun stepStateFromOpened(char: Char): Boolean {
        if (openingChars().union(setOf(current.closing)).contains(char).not()) { return false }

        return when (char) {
            '(' -> {
                previousStates.push(current)
                current = PAREN_OPENED
                true
            }
            '[' -> {
                previousStates.push(current)
                current = BRACKET_OPENED
                true
            }
            '{' -> {
                previousStates.push(current)
                current = CURLY_OPENED
                true
            }
            '<' -> {
                previousStates.push(current)
                current = ANGLE_OPENED
                true
            }
            current.closing -> {
                current = previousStates.pop()
                true
            }
            else -> false
        }
    }

    private fun openingChars(): Set<Char> = setOf('(', '[', '{', '<')

    enum class State(
        val closing: Char,
        val score: Int,
        val autoCompleteScore: Int,
    ) {
        INITIAL(' ', 0, 0),
        PAREN_OPENED(')', 3, 1),
        BRACKET_OPENED(']', 57, 2),
        CURLY_OPENED('}', 1197, 3),
        ANGLE_OPENED('>', 25137, 4),
        ;

        companion object {
            fun scoreFor(char: Char): Int = values().first { char == it.closing }.score
        }
    }
}
