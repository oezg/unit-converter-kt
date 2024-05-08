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