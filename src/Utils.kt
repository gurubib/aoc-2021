import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String, day: String) = File("src/$day", "$name.txt").readLines()

fun readInputForDay(d: Int, test: Boolean = false): List<String> {
    val dayNum = d.toString().padStart(2, '0');
    val name = if (test) "Day${dayNum}_test" else "Day$dayNum"
    return readInput(name, "day$dayNum")
}

fun readInputInOne(name: String, day: String) = File("src/$day", "$name.txt").readText()

fun readInputInOneForDay(d: Int, test: Boolean = false): String {
    val dayNum = d.toString().padStart(2, '0');
    val name = if (test) "Day${dayNum}_test" else "Day$dayNum"
    return readInputInOne(name, "day$dayNum")
}

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)
