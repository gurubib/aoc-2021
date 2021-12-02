package day02

import readInput

fun main() {
    fun part1(input: List<String>): Int {
        val submarine = BasicSubmarine()
        submarine.drive(input)
        return submarine.calculatePosition()
    }


    fun part2(input: List<String>): Int {
        val submarine = AimedSubmarine()
        submarine.drive(input)
        return submarine.calculatePosition()
    }

    val testInput = readInput("Day02_test", "day02")
    check(part1(testInput) == 150)
    check(part2(testInput) == 900)

    val input = readInput("Day02", "day02")
    println(part1(input)) // 1815044
    println(part2(input)) // 1739283308
}


private data class Position(
    val horizontal: Int = 0,
    val depth: Int = 0,
)

private fun Position.changed(horizontal: Int, depth: Int) = Position(
    this.horizontal + horizontal,
    this.depth + depth
)

private abstract class Controller(
    var position: Position,
) {

    fun control(command: String) {
        val (direction, amount) = parseCommand(command)
        control(direction, amount)
    }

    protected abstract fun control(direction: Direction, amount: Int)

    private fun parseCommand(command: String): Pair<Direction, Int> {
        val parts = command.split(" ")
        return Pair(Direction.valueOf(parts.first()), parts.last().toInt())
    }
}

private enum class Direction {
    forward, down, up
}

private class BasicController(
    position: Position,
) : Controller(position) {
    override fun control(direction: Direction, amount: Int) {
        position = when (direction) {
            Direction.forward -> position.changed(amount, 0)
            Direction.down -> position.changed(0, amount)
            Direction.up -> position.changed(0, -amount)
        }
    }
}

private abstract class Submarine {
    protected abstract val controller: Controller

    fun drive(commands: List<String>) = commands.forEach { command -> controller.control(command) }
    fun calculatePosition() = controller.position.horizontal * controller.position.depth
}

private class BasicSubmarine: Submarine() {
    override val controller = BasicController(Position())
}

private class AimedController(
    position: Position,
    private var aim: Int = 0,
) : Controller(position) {
    override fun control(direction: Direction, amount: Int) {
       when (direction) {
           Direction.down -> aim += amount
           Direction.up -> aim -= amount
           Direction.forward -> position = position.changed(amount, aim * amount)
       }
    }
}

private class AimedSubmarine : Submarine() {
    override val controller = AimedController(Position())
}