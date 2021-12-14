package day13

import day13.Axis.*
import readInputForDay

fun main() {
    fun part1(input: List<String>): Int {
        val manual = Manual.parse(input)
        val foldedOnce = foldByNextInstruction(manual)
        return foldedOnce.dots.size
    }

    fun part2(input: List<String>) {
        val manual = Manual.parse(input)
        val fullyFolded = foldByInstructions(manual)
        printManual(fullyFolded)
    }

    val testInput = readInputForDay(13, true)
    check(part1(testInput) == 17)
    part2(testInput)

    val input = readInputForDay(13)
    println(part1(input))
    part2(input)
}

data class Dot(
    val x: Int,
    val y: Int,
)

enum class Axis(val symbol: String) {
    HORIZONTAL("y"), VERTICAL("x");

    companion object {
        fun fromSymbol(s: String): Axis {
            return values().first { it.symbol == s }
        }
    }
}

data class Instruction(
    val axis: Axis,
    val along: Int,
)

data class Manual(
    val dots: Set<Dot>,
    val instructions: List<Instruction>,
) {
    companion object {
        fun parse(input: List<String>): Manual {
            val separatorIndex = input.indexOfFirst { it.isBlank() }
            val dots =  input.slice(0 until separatorIndex)
                .map { coords ->
                    coords.split(",").let { Dot(it.first().toInt(), it.last().toInt()) }
                }.toSet()

            val instructions = input.slice(separatorIndex + 1 until input.size)
                .map { instruction ->
                    instruction.split(" ").last().let { axis ->
                        axis.split("=").let { Instruction(Axis.fromSymbol(it.first()), it.last().toInt()) }
                    }
                }

            return Manual(dots, instructions)
        }
    }
}

fun foldByInstructions(manual: Manual): Manual {
    val manualVersions = mutableSetOf(manual)
    repeat(manual.instructions.size) {
        manualVersions.add(foldByNextInstruction(manualVersions.last()))
    }

    return manualVersions.last()
}

fun foldByNextInstruction(manual: Manual): Manual {
    val nextInstruction = manual.instructions.first()
    val foldedDots = foldBy(nextInstruction, manual.dots)
    val remainingInstructions = manual.instructions.drop(1)
    return Manual(foldedDots, remainingInstructions)
}

fun foldBy(instruction: Instruction, dots: Set<Dot>): Set<Dot> =
    dots
        .map { transformDot(instruction, it) }
        .filterNot {
            it.x < 0 || it.y < 0
        }
        .toSet()

fun transformDot(instruction: Instruction, dot: Dot): Dot {
    return when (instruction.axis) {
        HORIZONTAL -> if (dot.y < instruction.along) { dot } else { Dot(dot.x, 2 * instruction.along - dot.y) }
        VERTICAL -> if(dot.x < instruction.along) { dot } else { Dot(2 * instruction.along - dot.x, dot.y) }
    }
}

fun printManual(manual: Manual) {
    val maxX = manual.dots.maxOf { it.x }
    val maxY = manual.dots.maxOf { it.y }
    (0..maxY).forEach { y ->
        (0..maxX).forEach { x -> if (manual.dots.contains(Dot(x, y))) { print("#") } else { print(".") } }
        println()
    }
    println()
}