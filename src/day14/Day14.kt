package day14

import readInputForDay

fun main() {
    fun part1(input: List<String>): Long {
        val polymerizer = Polymerizer.parse(input)
        polymerizer.polymerize(10)
        val frequency = polymerizer.frequency()
        return frequency.maxOf { it.value } - frequency.minOf { it.value }
    }

    fun part2(input: List<String>): Long {
        val polymerizer = Polymerizer.parse(input)
        polymerizer.polymerize(40)
        val frequency = polymerizer.frequency()
        return frequency.maxOf { it.value } - frequency.minOf { it.value }
    }

    val testInput = readInputForDay(14, true)
    check(part1(testInput) == 1588L)
    check(part2(testInput) == 2188189693529L)

    val input = readInputForDay(14)
    println(part1(input))
    println(part2(input))
}

class Polymerizer(
   private val pairInsertions: Map<String, String>,
   private val pairs: MutableMap<String, Long>,
   private var firstPair: String,
   private var lastPair: String,
) {
    companion object {
        fun parse(input: List<String>): Polymerizer {
            val indexOfSeparator = input.indexOfFirst { it.isBlank() }
            val template = input.first()
            val pairInsertions = input.slice(indexOfSeparator + 1 until input.size)
                .associate { insertion -> insertion.split(" ").let { it.first() to it.last() } }
            val pairs = pairInsertions.keys.associateWith { 0L }.toMutableMap()
            template.windowed(2).forEach { pairs[it] = pairs.getValue(it) + 1 }
            val firstPair = template.take(2)
            val lastPair = template.takeLast(2)

            return Polymerizer(pairInsertions, pairs, firstPair, lastPair)
        }
    }

    fun polymerize(steps: Int) {
        repeat (steps) { step() }
    }

    private fun step() {
        var firstPairSet = false
        var lastPairSet = false
        val steppedPairs = pairs.keys.associateWith { 0L }.toMutableMap()
        pairs.filter { it.value > 0 }.keys.forEach { pair ->
            val toInsert = pairInsertions.getValue(pair)
            val newPairs = listOf(pair.first() + toInsert,  toInsert + pair.last())

            if (!firstPairSet && pair == firstPair) {
                firstPair = newPairs.first()
                firstPairSet = true
            }
            if (!lastPairSet && pair == lastPair) {
                lastPair = newPairs.last()
                lastPairSet = true
            }

            newPairs.forEach { steppedPairs[it] = steppedPairs.getValue(it) + pairs.getValue(pair) }
        }

        pairs.clear()
        pairs.putAll(steppedPairs)
    }

    fun frequency(): Map<Char, Long> {
        val frequencies = mutableMapOf<Char, Long>()
        pairs.forEach {
            frequencies[it.key.first()] = frequencies.getOrDefault(it.key.first(), 0) + it.value
            frequencies[it.key.last()] = frequencies.getOrDefault(it.key.last(), 0) + it.value
        }

        val halvedFrequencies = frequencies.map { it.key to it.value / 2 }.toMap().toMutableMap()
        halvedFrequencies[firstPair.first()] = halvedFrequencies.getValue(firstPair.first()) + 1
        halvedFrequencies[lastPair.last()] = halvedFrequencies.getValue(lastPair.last()) + 1

        return halvedFrequencies
    }
}
