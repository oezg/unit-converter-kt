package converter

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