package day07

import readInputForDay
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.floor

fun main() {
    fun part1(input: List<String>): Int {
        val positions = input.first().split(",").map { it.toInt() }
        return distanceSum(positions, medianElement(positions))
    }

    fun part2(input: List<String>): Int {
        val positions = input.first().split(",").map { it.toInt() }
        val average = positions.average()
        val sumFromAverageFloor = accumulatingDistanceSum(positions, floor(average).toInt())
        val sumFromAverageCeil = accumulatingDistanceSum(positions, ceil(average).toInt())
        return listOf(sumFromAverageFloor, sumFromAverageCeil).minOf { it }
    }

    val testInput = readInputForDay(7, true)
    check(part1(testInput) == 37)
    check(part2(testInput) == 168)

    val input = readInputForDay(7)
    println(part1(input))
    println(part2(input))
}

private fun distanceSum(elements: List<Int>, from: Int): Int {
    return elements.fold(0) { acc, e -> acc + (from - e).absoluteValue }
}

private fun medianElement(elements: List<Int>): Int {
    val sorted = elements.sorted()
    val middleIndex = ceil(elements.size / 2.0).toInt()
    return sorted[middleIndex]
}

private fun accumulatingDistanceSum(elements: List<Int>, from: Int): Int {
    return elements.fold(0) { acc, e -> acc + sumOfNaturalNumbers((from - e).absoluteValue) }
}

private fun sumOfNaturalNumbers(n: Int) = (n * (n + 1)) / 2