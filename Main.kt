package converter

enum class Measure {
    Length,
    Weight
}

enum class Unit(val meas: Measure, val abrv: String, val sing: String, val plur: String, val rate: Double) {
    Meter       (meas = Measure.Length, abrv = "m",  sing = "meter", plur = "meters", rate = 1.0),
    Kilometer   (meas = Measure.Length, abrv = "km", sing = "kilometer", plur = "kilometers", rate = 1_000.0),
    Centimeter  (meas = Measure.Length, abrv = "cm", sing = "centimeter", plur = "centimeters", rate = 0.01),
    Millimeter  (meas = Measure.Length, abrv = "mm", sing = "millimeter", plur = "millimeters", rate = 0.001),
    Mile        (meas = Measure.Length, abrv = "mi", sing = "mile", plur = "miles", rate = 1_609.35),
    Yard        (meas = Measure.Length, abrv = "yd", sing = "yard", plur = "yards", rate = 0.9144),
    Foot        (meas = Measure.Length, abrv = "ft", sing = "foot", plur = "feet", rate = 0.3048),
    Inch        (meas = Measure.Length, abrv = "in", sing = "inch", plur = "inches", rate = 0.0254),
    Gram        (meas = Measure.Weight, abrv = "g",  sing = "gram", plur = "grams", rate = 1.0),
    Kilogram    (meas = Measure.Weight, abrv = "kg", sing = "kilogram", plur = "kilograms", rate = 1_000.0),
    Milligram   (meas = Measure.Weight, abrv = "mg", sing = "milligram", plur = "milligrams", rate = 0.001),
    Pound       (meas = Measure.Weight, abrv = "lb", sing = "pound", plur = "pounds", rate = 453.592),
    Ounce       (meas = Measure.Weight, abrv = "oz", sing = "ounce", plur = "ounces", rate = 28.3495),
}

fun main() {
    while (true) {
        print("Enter what you want to convert (or exit): ")
        val input = readln()
        if (input == "exit") {
            return
        }
        val (a, b, _, c) = input.split(" ")
        val number = a.toDouble()
        val sourceResult = chooseUnit(b.lowercase())
        val targetResult = chooseUnit(c.lowercase())
        if (sourceResult.isFailure || targetResult.isFailure) {
            val sourceName =
                if (sourceResult.isFailure) {
                    "???"
                } else {
                    sourceResult.getOrThrow().plur
                }
            val targetName =
                if (targetResult.isFailure) {
                    "???"
                } else {
                    targetResult.getOrThrow().plur
                }
            println("Conversion from $sourceName to $targetName is impossible")
            println()
            continue
        }
        val source = sourceResult.getOrThrow()
        val target = targetResult.getOrThrow()
        if (source.meas != target.meas) {
            println("Conversion from ${source.plur} to ${target.plur} is impossible")
            println()
            continue
        }

        val convertedNumber = number * source.rate / target.rate
        val sourceName = if (number == 1.0) source.sing else source.plur
        val targetName = if (convertedNumber == 1.0) target.sing else target.plur
        println("$number $sourceName is $convertedNumber $targetName")
        println()
    }
}

fun chooseUnit(measureInput: String): Result<Unit> {
    for (measure in Unit.values()) {
        if (measureInput in listOf(measure.abrv, measure.sing, measure.plur)) {
            return Result.success(measure)
        }
    }
    return Result.failure(IllegalArgumentException(measureInput))
}