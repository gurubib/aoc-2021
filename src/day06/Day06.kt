package day06

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import readInputForDay

fun main() = runBlocking {
    suspend fun part1(input: List<String>): Long {
        val school = School.parse(input.first())
        val result = async { school.simulate(80) }
        return result.await()
    }

    suspend fun part2(input: List<String>): Long {
        val school = School.parse(input.first())
        val result = async { school.simulate(256) }
        return result.await()
    }

    val testInput = readInputForDay(6, true)
    check(part1(testInput) == 5934L)
    check(part2(testInput) == 26984457539)

    val input = readInputForDay(6)
    println(part1(input))
    println(part2(input))
}

class School(
    private val currentCycles: MutableMap<Int, Long> = cycleCounter(),
    private val nextCycles: MutableMap<Int, Long> = cycleCounter(),
) {
    companion object {
        fun parse(input: String): School {
            val school = School()
            input.split(",").map { it.toInt() }.forEach { cycle ->
                val current = school.currentCycles[cycle] ?: 0
                school.currentCycles[cycle] = current + 1
            }
            return school
        }
    }

    fun simulate(days: Int): Long {
        repeat(days) { tick() }
        return currentCycles.values.sum()
    }

    private fun tick() {
        (0..8).forEach { cycle ->
            val currCount = currentCycles[cycle] ?: 0L
            nextCycles[cycle - 1] = currCount
        }

        val nextSixCount = nextCycles[6] ?: 0
        val newCount = nextCycles[-1] ?: 0
        nextCycles[-1] = 0
        nextCycles[6] = nextSixCount + newCount
        nextCycles[8] = newCount

        currentCycles.clear()
        currentCycles.putAll(nextCycles)
    }
}

private fun cycleCounter(): MutableMap<Int, Long> = mutableMapOf(
    -1 to 0L,
    0 to 0L,
    1 to 0L,
    2 to 0L,
    3 to 0L,
    4 to 0L,
    5 to 0L,
    6 to 0L,
    7 to 0L,
    8 to 0L,
)
