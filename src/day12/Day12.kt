package day12

import readInputForDay

fun main() {
    fun part1(input: List<String>): Int {
        val caveSystem = CaveSystem.parse(input)
        return caveSystem.findRoutes(CaveSystem::wasSmallCaveVisited)
    }

    fun part2(input: List<String>): Int {
        val caveSystem = CaveSystem.parse(input)
        return caveSystem.findRoutes(CaveSystem::wasOneSmallCaveVisitedAtMostOnce)
    }

    val testInput = readInputForDay(12, true)
    check(part1(testInput) == 226)
    check(part2(testInput) == 3509)

    val input = readInputForDay(12)
    println(part1(input))
    println(part2(input))
}

class CaveSystem(
    private val paths: Map<String, List<String>>,
) {
    companion object {
        fun parse(input: List<String>): CaveSystem {
            val oneWay = input.map { it.substringBefore("-") to it.substringAfter("-") }.toSet()
            val otherWay = input.map { it.substringAfter("-") to it.substringBefore("-") }.toSet()
            val paths = (oneWay union otherWay).groupBy({ it.first }) { it.second }
            return CaveSystem(paths)
        }

        fun wasSmallCaveVisited(name: String, route: String): Boolean =
            if (name.lowercase() == name) route.contains(",$name,").not() else true

        fun wasOneSmallCaveVisitedAtMostOnce(name: String, route: String): Boolean {
            return if (setOf("start", "end").contains(name)) {
                wasSmallCaveVisited(name, route)
            } else if (name.lowercase() == name) {
                if (hasVisitedSmallCaveTwiceInRoute(route)) {
                    wasSmallCaveVisited(name, route)
                } else {
                    route.windowed(name.length + 2).count { it == ",$name," } < 2
                }
            } else {
                true
            }
        }

        private fun hasVisitedSmallCaveTwiceInRoute(route: String): Boolean {
            return route.split(",")
                .filter { it.lowercase() == it }
                .groupingBy { it }
                .eachCount()
                .any { it.value > 1 }
        }
    }

    fun findRoutes(visited: (name: String, route: String) -> Boolean): Int {
        val completeRoutes = mutableSetOf<String>()
        val routes = mutableSetOf(",start")

        while(routes.any { it.endsWith("end").not() }) {
            val notCompleteRoutes = routes.filterNot { it.endsWith("end") }
            val longerRoutes = notCompleteRoutes.flatMap { r ->
                val lastNode = r.substringAfterLast(",")
                adjacentNodes(lastNode)
                    .filter { visited(it, r) }
                    .map { adj -> "$r,$adj" }
            }.filterNot { completeRoutes.contains(it) }

            if (longerRoutes.size > 1) {
                routes.clear()
                routes.addAll(longerRoutes)
                completeRoutes.addAll(routes.filter { it.endsWith("end") })
            } else {
                routes.clear()
            }
        }

        return completeRoutes.size
    }

    private fun adjacentNodes(name: String): List<String> {
        return paths[name] ?: listOf()
    }
}
