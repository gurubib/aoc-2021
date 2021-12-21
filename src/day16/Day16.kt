package day16

import readInputForDay

fun main() {
    fun part1(input: List<String>): Int {
        val bits = BITS.parse(input)
        val packets = bits.interpret()
        return packets.sumOf { it.version }
    }

    fun part2(input: List<String>): Long {
        val bits = BITS.parse(input)
        val packets = bits.interpret()
        return bits.eval(packets)
    }

    val testInput = readInputForDay(16, true)
//    check(part1(testInput) == 31)
    check(part2(testInput) == 1L)

    val input = readInputForDay(16)
    println(part1(input))
    println(part2(input))
}

enum class PacketType {
    LITERAL, OPERATOR;

    companion object {
        fun fromTypeId(id: Int): PacketType = if (id == 4) LITERAL else OPERATOR
    }
}

interface Packet{
    val version: Int
    val type: PacketType
    val typeId: Int
    val depth: Int
}

data class LiteralPacket(
    override val version: Int,
    override val depth: Int,
    val literalValue: Long,
) : Packet {
    override val type get() = PacketType.LITERAL
    override val typeId get() = 4
}

data class OperatorPacket(
    override val version: Int,
    override val typeId: Int,
    override val depth: Int,
    val lengthTypeId: Int,
    val argNum: Int,
) : Packet {
    override val type get() = PacketType.OPERATOR
}

class BinaryStream(
    private var stream: String,
) {
    val isEmpty get() = stream.isEmpty() || stream.all { it.digitToInt(2) == 0 }

    fun take(n: Int): String {
        check(!isEmpty) { "Binary stream is empty!" }

        val streamBeforeTake = stream
        val taken = stream.take(n)
        stream = streamBeforeTake.drop(n)
        return taken
    }

    fun takeWhile(chunk: Int, predicate: (taken: String) -> Boolean): String {
        var content = ""

        do {
            val part = take(chunk)
            content += part
        } while (predicate(part))

        return content
    }

    fun takeAll(): String {
       return take(stream.length)
    }

    fun copy(n: Int): BinaryStream = BinaryStream(stream.take(n))
}

class BITS(
    val hexMessage: String,
    val stream: BinaryStream,
) {
    companion object {
        fun parse(input: List<String>): BITS {
            val hexMessage = input.first()
            val binaryMessage = hexMessage.toList().joinToString("") { it.digitToInt(16).toString(2).padStart(4, '0') }
            return BITS(hexMessage, BinaryStream(binaryMessage))
        }
    }

    fun interpret() = interpret(stream, listOf(), 0)

    private fun interpret(bs: BinaryStream, packets: List<Packet>, depth: Int): List<Packet> {
        if (bs.isEmpty) {
            return packets
        }

        val version = bs.take(3).toInt(2)
        val typeId = bs.take(3).toInt(2)

        return if (PacketType.fromTypeId(typeId) == PacketType.LITERAL) {
            val content = bs.takeWhile(5) { it.first().digitToInt(2) == 1 }
            val value = content.chunked(5).filter { it.length == 5 }
                .joinToString("") { it.drop(1) }.toLong(2)
            packets + LiteralPacket(version, depth, value)
        } else {
            val lengthTypeId = bs.take(1).toInt(2)
            if (lengthTypeId == 0) {
                val total = bs.take(15).toInt(2)
                val subPacketsStream = bs.copy(total)
                val subPackets = mutableListOf<Packet>()
                while (subPacketsStream.isEmpty.not()) {
                    subPackets += interpret(subPacketsStream, listOf(), depth + 1)
                }
                val numOfSubPackets = subPackets.count { it.depth == depth + 1 }
                val packet = OperatorPacket(version, typeId, depth, lengthTypeId, numOfSubPackets)
                bs.take(total)
                interpret(bs, packets + packet + subPackets, depth)
            } else {
                val numOfSubPackets = bs.take(11).toInt(2)
                val subPackets = (0 until numOfSubPackets).flatMap { interpret(bs, listOf(), depth + 1) }
                val packet = OperatorPacket(version, typeId, depth, lengthTypeId, numOfSubPackets)
                interpret(bs, packets + packet + subPackets, depth)
            }
        }
    }

    fun eval(packets: List<Packet>): Long {
        val stack = ArrayDeque<Long>()
        val maxDepth = packets.last().depth
        packets.reversed().forEachIndexed { i, p ->
            when (p) {
                is LiteralPacket -> {
                    stack.addLast(p.literalValue)
                }
                is OperatorPacket -> {
                    val args = mutableListOf<Long>()
                    repeat(p.argNum) { args.add(stack.removeLast()) }
                    when (p.typeId) {
                        0 -> stack.addLast(args.fold(0) { acc, value -> acc + value })
                        1 -> stack.addLast(args.fold(1) { acc, value -> acc * value })
                        2 -> stack.addLast(args.minOf { it })
                        3 -> stack.addLast(args.maxOf { it })
                        5 -> if (args[0] > args[1]) stack.addLast(1) else stack.addLast(0)
                        6 -> if (args[0]< args[1]) stack.addLast(1) else stack.addLast(0)
                        7 -> if (args[0]== args[1]) stack.addLast(1) else stack.addLast(0)
                    }
                }
            }
        }

        return stack.last()
    }
}