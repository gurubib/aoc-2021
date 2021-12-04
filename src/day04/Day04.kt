package day04

import readInputForDay

fun main() {
    fun part1(input: List<String>): Int {
        val bingo = Bingo.from(input)
        bingo.play()
        return bingo.winningScore()
    }


    fun part2(input: List<String>): Int {
        val bingo = Bingo.from(input)
        bingo.play()
        return bingo.losingScore()
    }

    val testInput = readInputForDay(4, true)
    check(part1(testInput) == 4512)
    check(part2(testInput) == 1924)

    val input = readInputForDay(4)
    println(part1(input))
    println(part2(input))
}

class Table(
    private val table: Map<Int, List<Pair<Int, Int>>>,
    private val marked: MutableList<Int> = mutableListOf(),
    private val markedInRows: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0),
    private val markedInColumns: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0),
) {
    companion object {
        fun from(rows: List<String>): Table {
            val table = mutableMapOf<Int, List<Pair<Int, Int>>>()
            rows.forEachIndexed { y, row ->
                row.split(" ").filterNot { it == "" }.forEachIndexed { x, value ->
                    val fieldsForNum = table[value.toInt()]
                    if (fieldsForNum == null) {
                        table[value.toInt()] = listOf(Pair(x, y))
                    } else {
                        table.replace(value.toInt(), listOf(*fieldsForNum.toTypedArray(), Pair(x, y)))
                    }
                }
            }

            return Table(table)
        }
    }

    fun unmarkedSum(): Int = (table.keys subtract marked.toSet()).sum()

    fun mark(value: Int): Boolean = marked.add(value).let { table[value]?.map { markField(it) }?.any { it } ?: false }

    private fun markField(field: Pair<Int, Int>): Boolean {
        val (column, row) = field
        markedInRows[row] += 1
        markedInColumns[column] += 1

        return checkRowBingo() || checkColumnBing()
    }

    private fun checkRowBingo(): Boolean = markedInRows.any { it == 5 }

    private fun checkColumnBing(): Boolean = markedInColumns.any { it == 5 }
}

class Bingo(
    private val tables: List<Table>,
    private val marks: List<Int>,
    private val bingos: MutableList<Int> = mutableListOf(),
    private val won: MutableSet<Int> = mutableSetOf(),
) {
    companion object {
        fun from(input: List<String>): Bingo {
            val marks = input[0].split(",").map { it.toInt() }
            val tables = input.drop(1).chunked(6) { it.drop(1) }.map { Table.from(it) }
            return Bingo(tables, marks)
        }
    }

    fun play() {
        marks.forEach { value ->
            tables.forEachIndexed { i, table ->
                if (won.contains(i).not())  {
                    val bingo = table.mark(value)
                    if (bingo) {
                        bingos.add(value * table.unmarkedSum())
                        won.add(i)
                    }
                }
            }
        }
    }

    fun winningScore() = bingos.first()

    fun losingScore() = bingos.last()
}
