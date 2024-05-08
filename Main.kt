package converter

enum class Measure {
    Length, Weight, Temperature
}

enum class Unit(val of: Measure, val names: List<String>, val rate: Double) {
    Meter       (of = Measure.Length, names = listOf("meter", "meters", "m"), rate = 1.0),
    Kilometer   (of = Measure.Length, names = listOf("kilometer", "kilometers", "km"), rate = 1_000.0),
    Centimeter  (of = Measure.Length, names = listOf("centimeter", "centimeters", "cm"), rate = 0.01),
    Millimeter  (of = Measure.Length, names = listOf("millimeter", "millimeters", "mm"), rate = 0.001),
    Mile        (of = Measure.Length, names = listOf("mile", "miles", "mi"), rate = 1_609.35),
    Yard        (of = Measure.Length, names = listOf("yard", "yards", "yd"), rate = 0.9144),
    Foot        (of = Measure.Length, names = listOf("foot", "feet", "ft"), rate = 0.3048),
    Inch        (of = Measure.Length, names = listOf("inch", "inches", "in"), rate = 0.0254),
    Gram        (of = Measure.Weight, names = listOf("gram", "grams", "g"), rate = 1.0),
    Kilogram    (of = Measure.Weight, names = listOf("kilogram", "kilograms", "kg"), rate = 1_000.0),
    Milligram   (of = Measure.Weight, names = listOf("milligram", "milligrams", "mg"), rate = 0.001),
    Pound       (of = Measure.Weight, names = listOf("pound", "pounds", "lb"), rate = 453.592),
    Ounce       (of = Measure.Weight, names = listOf("ounce", "ounces", "oz"), rate = 28.3495),
    Kelvin      (of = Measure.Temperature, names = listOf("kelvin", "kelvins", "k"), rate = 1.0),
    Celsius     (of = Measure.Temperature, names = listOf("degree Celsius", "degrees Celsius", "celsius", "dc", "c"), rate = 273.15)
    {
        override fun isAcceptableAmount(number: Double): Boolean = number >= -rate
        override fun convertToBase(number: Double): Double = number + rate
        override fun convertFromBase(number: Double): Double = number - rate
    },
    Fahrenheit  (of = Measure.Temperature, names = listOf("degree Fahrenheit", "degrees Fahrenheit", "fahrenheit", "df", "f"), rate = 459.67)
    {
        override fun isAcceptableAmount(number: Double): Boolean = number >= -rate
        override fun convertToBase(number: Double): Double = (number + rate) * 5.0 / 9.0
        override fun convertFromBase(number: Double): Double = number * 9.0 / 5.0 - rate
    };
    val singular: String get() = names.first()
    val plural: String get() = names[1]

    fun displayName(amount: Double = 0.0): String =
        if (amount == 1.0) {
            singular
        } else {
            plural
        }
    open fun convertToBase(number: Double): Double = number * rate
    open fun convertFromBase(number: Double): Double = number / rate
    fun isAcceptableName(s: String): Boolean = names.map { it.lowercase() }.contains(s)
    open fun isAcceptableAmount(number: Double): Boolean = number >= 0.0
    fun canBeConvertedTo(measure: Unit): Boolean = of == measure.of
}

fun main() {
    while (true) {
        print("Enter what you want to convert (or exit): ")
        val input = readln()
        if (input == "exit") {
            return
        }
        val parseResult = parse(input)
        if (parseResult.isFailure) {
            println("Parse error\n")
            continue
        }
        val conversionExpression = parseResult.getOrThrow()
        val parseMeasureResult = parseMeasures(conversionExpression)
        if (parseMeasureResult.isFailure) {
            parseMeasureResult.onFailure { println(it.message) }
            continue
        }
        val conversionMeasure = parseMeasureResult.getOrThrow()
        val validConversionResult = validateConversion(conversionMeasure)
        if (validConversionResult.isFailure) {
            validConversionResult.onFailure { println(it.message) }
            continue
        }
        val validConversion = validConversionResult.getOrThrow()
        val number = validConversion.number
        val sourceName = validConversion.source.displayName(number)
        val convertedNumber = validConversion.target.convertFromBase(validConversion.source.convertToBase(number))
        val targetName = validConversion.target.displayName(convertedNumber)
        println("$number $sourceName is $convertedNumber $targetName\n")
    }
}

data class ConversionExpression(val number: Double, val measure1: String, val measure2: String)
data class ConversionMeasures(val number: Double, val source: Unit, val target: Unit)

fun parseMeasures(conversionExpression: ConversionExpression): Result<ConversionMeasures> {
    val unitResult1 = chooseUnit(conversionExpression.measure1)
    val unitResult2 = chooseUnit(conversionExpression.measure2)
    if (unitResult1.isFailure || unitResult2.isFailure) {
        val sourceName = if (unitResult1.isFailure) "???" else unitResult1.getOrThrow().displayName()
        val targetName = if (unitResult2.isFailure) "???" else unitResult2.getOrThrow().displayName()
        val message = "Conversion from $sourceName to $targetName is impossible\n"
        return Result.failure(IllegalArgumentException(message))
    }
    return Result.success(ConversionMeasures(conversionExpression.number, unitResult1.getOrThrow(), unitResult2.getOrThrow()))
}

fun chooseUnit(s: String): Result<Unit> {
    for (unit in Unit.values()) {
        if (unit.isAcceptableName(s)) {
            return Result.success(unit)
        }
    }
    return Result.failure(IllegalArgumentException(s))
}

fun validateConversion(conversionMeasures: ConversionMeasures): Result<ConversionMeasures> {
    if (!conversionMeasures.source.canBeConvertedTo(conversionMeasures.target)) {
        val message = "Conversion from ${conversionMeasures.source.displayName()} to ${conversionMeasures.target.displayName()} is impossible\n"
        return Result.failure(IllegalArgumentException(message))
    }
    if (!conversionMeasures.source.isAcceptableAmount(conversionMeasures.number)) {
        val message = "${conversionMeasures.source.of} shouldn't be negative\n"
        return Result.failure(IllegalArgumentException(message))
    }
    return Result.success(conversionMeasures)
}

fun parse(input: String): Result<ConversionExpression> {
    val words = input.lowercase().split(" ")
    val number = try {
        words.first().toDouble()
    } catch (e: NumberFormatException) {
        return Result.failure(e)
    }
    if (words.size !in 4..6) {
        return Result.failure(IllegalArgumentException(input))
    }
    if (words.size == 4) {
        return Result.success(ConversionExpression(number, words[1], words.last()))
    }
    if (words.size == 6) {
        return Result.success(ConversionExpression(number,
            words.slice(1..2).joinToString(" "),
            words.slice(4..5).joinToString(" ")))
    }
    if (words[1].contains("degree")) {
        return Result.success(
            ConversionExpression(number,
                words.slice(1..2).joinToString(" "),
                words.last()))
    }
    return Result.success(
        ConversionExpression(number,
            words[1],
            words.slice(3..4).joinToString(" "))
    )
}
