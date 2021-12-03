package day03

import readInput

fun main() {
    fun part1(input: List<String>): Int {
        val measurements = parse(input)
        val gamma = gamma(transpose(measurements))
        val epsilon = epsilon(gamma, measurements.first().size)
        return gamma * epsilon
    }


    fun part2(input: List<String>): Int {
        val measurements = parse(input)
        val oxygen = oxygen(measurements)
        val co2 = co2(measurements)
        return oxygen * co2
    }

    val testInput = readInput("Day03_test", "day03")
    check(part1(testInput) == 198)
    check(part2(testInput) == 230)

    val input = readInput("Day03", "day03")
    println(part1(input))
    println(part2(input))
}

private fun parse(input: List<String>): List<List<Int>> = input.map { it.toList().mapNotNull(Char::digitToIntOrNull) }

private fun transpose(matrix: List<List<Int>>): List<List<Int>> =
    matrix.first().indices.map { i -> matrix.map { it[i] } }

private fun gamma(matrix: List<List<Int>>): Int {
    val threshold = matrix.first().size.toDouble() / 2
    return matrix.map { if(it.sum() > threshold) 1 else 0 }.joinToString("").toInt(radix = 2)
}

private fun epsilon(gamma: Int, numOfMeasurements: Int): Int {
    val binary = gamma.toUInt().toString(radix = 2).padStart(numOfMeasurements, '0')
    return binary.toList().map { if (it.digitToIntOrNull() == 1) 0 else 1 }.joinToString("").toInt(radix = 2)
}

private fun oxygen(measurements: List<List<Int>>): Int {
    return measurements.first().indices.fold(measurements.toList()) { acc, i ->
        if (acc.size > 1) {
            val transposed = transpose(acc)
            val mostCommon = if (transposed[i].sum() >= transposed.first().size.toDouble() / 2) 1 else 0
            acc.filter { it[i] == mostCommon }
        } else {
            acc
        }
    }.first().joinToString("").toInt(radix = 2)
}
private fun co2(measurements: List<List<Int>>): Int {
    return measurements.first().indices.fold(measurements.toList()) { acc, i ->
        if (acc.size > 1) {
            val transposed = transpose(acc)
            val leastCommon = if (transposed[i].sum() >= transposed.first().size.toDouble() / 2) 0 else 1
            acc.filter { it[i] == leastCommon }
        } else {
            acc
        }
    }.first().joinToString("").toInt(radix = 2)
}
