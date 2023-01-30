package converter

enum class DistancesUnits(val nameList: List<String>, val meterMultiplier: Double) {
    METERS(listOf("m", "meter", "meters"), 1.0),
    KILOMETERS(listOf("km", "kilometer", "kilometers"), 1000.0),
    CENTIMETERS(listOf("cm", "centimeter", "centimeters"), 0.01),
    MILLIMETERS(listOf("mm", "millimeter", "millimeters"),0.001),
    MILES(listOf("mi", "mile", "miles"), 1609.35),
    YARDS(listOf("yd", "yard", "yards"), 0.9144),
    FEET(listOf("ft", "foot", "feet"), 0.3048),
    INCHES(listOf("in", "inch", "inches"), 0.0254)
}

enum class WeightUnits(val nameList: List<String>, val gramMultiplier: Double) {
    GRAMS(listOf("g", "gram", "grams"), 1.0),
    KILOGRAMS(listOf("kg", "kilogram", "kilograms"), 1000.0),
    MILLIGRAMS(listOf("mg", "milligram", "milligrams"), 0.001),
    POUNDS(listOf("lb", "pound", "pounds"),453.592),
    OUNCES(listOf("oz", "ounce", "ounces"), 28.3495)
}

enum class TemperatureUnits(val nameList: List<String>) {
    CELSIUS(listOf("degree Celsius", "degrees Celsius", "celsius", "dc", "c")),
    FAHRENHEIT(listOf("degree Fahrenheit", "degrees Fahrenheit", "fahrenheit", "df", "f")),
    KELVINS(listOf("kelvin", "kelvins", "k"))
}

data class UnitsConverter(
    var sourceUnit: String,
    var sourceUnitType: String,
    var sourceUnitTypeSingular: String,
    var targetUnit: String,
    var targetUnitType: String,
    var targetUnitTypeSingular: String
) {
    private var sourceMultiplier = 0.0
    private var targetMultiplier = 0.0

    init {
        while (true) {
            // Reset
            "".let {
                sourceUnit = it; sourceUnitType = it; sourceUnitTypeSingular = it
                targetUnit = it; targetUnitType = it; targetUnitTypeSingular = it
            }
            0.0.let { sourceMultiplier = it; targetMultiplier = it }

            // Start
            print("Enter what you want to convert (or exit): ")
            val input = readln()
            if (input == "exit") {
                break
            } else if (!Regex(
                    "^-?\\d+(.\\d+)?\\s[a-z]+(\\s[a-z]+)?\\s([a-z]+)?to\\s[a-z]+(\\s[a-z]+)?\$|" +
                           "^-?\\d+(.\\d+)?\\s[a-z]+(\\s[a-z]+)?\\s([a-z]+)?in\\s[a-z]+(\\s[a-z]+)?\$",
                           RegexOption.IGNORE_CASE).matches(input)) {
                println("Parse error")
                continue
            }
            val (src, sourceStr, placeholder, targetStr) = input.split(' ').filter {
                it.lowercase() != "degrees" && it.lowercase() != "degree"
            }
            val source = src.toDouble()

            val (isConversionValidBool, isConversionValidStr) = isConversionValid(
                sourceStr.lowercase(), targetStr.lowercase()
            )
            if (source < 0.0 && sourceUnit == DistancesUnits.METERS.javaClass.toString()) {
                println("Length shouldn't be negative")
                continue
            } else if (source < 0.0 && sourceUnit == WeightUnits.GRAMS.javaClass.toString()) {
                println("Weight shouldn't be negative")
                continue
            } else if (!isConversionValidBool) {
                println(isConversionValidStr)
                continue
            }

            val result = if (sourceUnitType == targetUnitType) {
                source
            } else if (sourceMultiplier == 0.0 && targetMultiplier == 0.0) {
                temperatureConversion(source)
            } else (source * sourceMultiplier) / targetMultiplier
            val resultStrSource = if (source == 1.0) sourceUnitTypeSingular else sourceUnitType
            val resultStrTarget = if (result == 1.0) targetUnitTypeSingular else targetUnitType

            println("$source $resultStrSource is $result $resultStrTarget")
        }
    }

    private fun temperatureConversion(src: Double): Double {
        val (celsius, fahrenheit, kelvin) = Triple(
            TemperatureUnits.CELSIUS.nameList[1],
            TemperatureUnits.FAHRENHEIT.nameList[1],
            TemperatureUnits.KELVINS.nameList[1]
        )
        if (sourceUnitType == celsius && targetUnitType == fahrenheit) {
            return src * 9 / 5 + 32
        } else if (sourceUnitType == fahrenheit && targetUnitType == celsius) {
            return (src - 32.0) * 5 / 9
        } else if (sourceUnitType == kelvin && targetUnitType == celsius) {
            return src - 273.15
        } else if (sourceUnitType == celsius && targetUnitType == kelvin) {
            return src + 273.15
        }
        return if (sourceUnitType == fahrenheit && targetUnitType == kelvin) {
            (src + 459.67) * 5 / 9
        } else src * 9 / 5 - 459.67
    }

    private fun isConversionValid(source: String, target: String): Pair<Boolean, String> {
        var sourceCheckResult = "???"
        var targetCheckResult = "???"

        DistancesUnits.values().forEach { entry ->
            if (entry.nameList.contains(source)) {
                sourceCheckResult = entry.nameList[2].also { sourceUnitType = it }
                sourceUnit = entry.javaClass.toString()
                sourceUnitTypeSingular = entry.nameList[1]
                sourceMultiplier = entry.meterMultiplier
            }
            if (entry.nameList.contains(target)) {
                targetCheckResult = entry.nameList[2].also { targetUnitType = it }
                targetUnit = entry.javaClass.toString()
                targetUnitTypeSingular = entry.nameList[1]
                targetMultiplier = entry.meterMultiplier
            }
        }

        WeightUnits.values().forEach { entry ->
            if (entry.nameList.contains(source)) {
                sourceCheckResult = entry.nameList[2].also { sourceUnitType = it }
                sourceUnit = entry.javaClass.toString()
                sourceUnitTypeSingular = entry.nameList[1]
                sourceMultiplier = entry.gramMultiplier
            }
            if (entry.nameList.contains(target)) {
                targetCheckResult = entry.nameList[2].also { targetUnitType = it }
                targetUnit = entry.javaClass.toString()
                targetUnitTypeSingular = entry.nameList[1]
                targetMultiplier = entry.gramMultiplier
            }
        }

        TemperatureUnits.values().forEach { entry ->
            if (entry.nameList.contains(source)) {
                sourceCheckResult = entry.nameList[1].also { sourceUnitType = it }
                sourceUnit = entry.javaClass.toString()
                sourceUnitTypeSingular = entry.nameList[0]
            }
            if (entry.nameList.contains(target)) {
                targetCheckResult = entry.nameList[1].also { targetUnitType = it }
                targetUnit = entry.javaClass.toString()
                targetUnitTypeSingular = entry.nameList[0]
            }
        }

        return if (sourceUnitType.isEmpty() || targetUnitType.isEmpty() || sourceUnit != targetUnit) {
            Pair(false, "Conversion from $sourceCheckResult to $targetCheckResult is impossible")
        } else Pair(true, "Conversion from $sourceCheckResult to $targetCheckResult is possible")
    }
}

fun main() {
    UnitsConverter("", "", "", "", "", "")
}