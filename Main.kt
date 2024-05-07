package converter

fun main() {
    print("Enter a number and a measure: ")
    val (a, b) = readln().split(" ")
    val measure = b.lowercase()
    val number = try {
        a.toInt()
    } catch (e: NumberFormatException) {
        println("Wrong input")
        return
    }
    if (number == 1 && measure == "km" || measure == "kilometer") {
        println("1 kilometer is 1000 meters")
    } else if (measure == "km" || measure == "kilometers") {
        println("$number kilometers is ${number}000 meters")
    } else {
        println("Wrong input")
    }
}
