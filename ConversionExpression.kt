package converter

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