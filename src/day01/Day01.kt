package day01

import readInput

fun main() {
    fun part1(input: List<String>): Int {
        val measurements = input.map { it.toInt() }
        return measurements
            .windowed(2)
            .count { isDescending(it.first(), it.last()) }
    }


    fun part2(input: List<String>): Int {
        val measurements = input.map { it.toInt() }
        return measurements
            .windowed(3)
            .windowed(2)
            .count { areDescending(it.first(), it.last()) }
    }

    val testInput = readInput("Day01_test", "day01")
    check(part1(testInput) == 7)
    check(part2(testInput) == 5)

    val input = readInput("Day01", "day01")
    println(part1(input))
    println(part2(input))
}


private fun isDescending(prev: Int, curr: Int): Boolean = prev < curr

private fun areDescending(prev: List<Int>, curr: List<Int>): Boolean =
    prev.sum() < curr.sum()
