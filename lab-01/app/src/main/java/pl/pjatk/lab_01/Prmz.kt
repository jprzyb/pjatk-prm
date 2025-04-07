import java.io.File
import java.lang.NumberFormatException
import java.time.LocalDate
import java.time.LocalDateTime

typealias MyLambda = (() -> Int) -> (() -> Boolean)

const val topLevel = "Top level val"

fun main() {
    //conversion on demand
    val byteVal = 123
    //byteVal = 1
//    var floatVal: Double = 1.34
//    floatVal = 2.3f.toDouble()
    //val intVal: Int = byteVal
    val intVal: Int = byteVal.toInt()
    println(topLevel)

    //bitwise operations
    val bitwiseOr = 0b10.or(0b01)
    val bitwiseAnd = 0b11 and 0b01
    val bitwiseShiftLeft = 0b001 shl 2
    println(topLevel)

    //raw String
    val string = """
        |multiline\r\n
        |string
        |hello
        |$byteVal
        |hin
    """.trimMargin()
    println(string)
    val input = readLine()

    //Smart cast
    var number: Number = 123
//    println(number.toString(2))
    if (number is Int) {
        println(number.toString(2))
    }

    //methods/functions
//    fun addAll(vararg numbers: Int): Int = numbers[0] + numbers[1] + ... + numbers[numbers.size - 1]
    fun add(a: Int, b: Int = 1, c: Int = 0): Int = a + b + c
    fun setParam(a: Int) {
//        var a = a
//        a = 1
        println(a)
    }
    add(1)
    add(1, 2)
    add(1, c = 10)
    add(1, c = 10, b = 2)

    setParam(9)

    //loops
    iLoop@ for (i in 1..4) {
        for (j in 1..4) {
            if (i == 2 && j == i) break@iLoop
        }
        println()
    }

    println("---- ${2 in 4 downTo 1}")

    //when as else if
    val any: Any = bitwiseShiftLeft
    val aResult = when {
        bitwiseOr == 1 -> "1"
        any is Long -> "Long"
        else -> "nic powyżej"
    }

    //lambda
    { println("Hello from lambda") }.invoke();
    { println("Hello from lambda") }()
    val lambdaType : () -> Unit = { println("Hello from lambda") }
    lambdaType()

    val lambdaType2: (Int, Long) -> Boolean = la@{ a, b ->
        a + b == b
        return@la false
    }
    println(lambdaType2(0, 12L))

    val lambdaType3: (Int) -> Unit = { println("$it * 2 = ${it * 2}") }
    println(lambdaType3(2))

    fun runLambda(value: Int = 42, lambda: (Int) -> Unit) = lambda(value)
    runLambda(12, { println("hi $it") })
    runLambda(12) { println("hi $it") }
    runLambda {
        println("hi $it") 
    }

    //collections
    val aList = listOf(1, 2, 3)
    aList[1]
    //aList[1] = 123

    val aMutableList = mutableListOf(1, 2, 3)
    aMutableList[1] = 123
    aMutableList.add(4)

    println("-===-")
    val aValue = aList
        .asSequence()
        .filter {
            println("filter $it")
            it % 2 == 0
        }
        .map {
            println("map $it")
            it * 2
        }
        .first()
    println(aValue)
    println("-===-")

//    val myMap = mapOf<String, Int>(Pair("Ala", 200), "Ewa" to 300)
    val myMap = mutableMapOf<String, Int>(Pair("Ala", 200), "Ewa" to 300)
    myMap["Mateusz"] = 1000
    println(myMap["Ala"])

    //classes
    println(MyClass.a)
    MyClass.a = 10

    println(Singleton.a)
    Singleton.a = 10

    val res: Result = Result.Ok()
    val msg: String = when (res) {
        is Result.Error -> "Error ${res.code}"
        is Result.Ok -> "Ok"
    }

    val implements: Power = Power { it * it }
    doOnLambdaThis(21) {
        println(this * 2)
    }
    val mc = MyClass()
    mc.asdf = 12
    MyClass2(2).asdf

    val me = Me("Mateusz", 1994)
    println(me)
    println(me.age)

    try {
        println("12a3".toInt())
    } catch (e: NumberFormatException) {
        println(e)
    } finally {
        println("after")
    }

    File("src/main/kotlin/Prmz.kt")
        .reader()
        .use {
            println(it.readLines().first())
        }
}

enum class Planet {
    Mercury, Venus, Earth, Mars
}

class Outer {
    var foo: String = "bar"
        private set(value) {
            if (value.startsWith("a")) field = value
        }
        get() {
            return "bar $field"
        }
    class Nested {
        //fun say() = println(foo)
    }
    inner class Inner {
        fun say() {
            println(foo)
            foo = "ala"
        }
    }
}

class OldStyle(style: Int) {
    val style: Int
    init {
        this.style = style
    }
}

class MyClass (var asdf: Int = 123) {
    val bcd: Int = 0
    init {

    }

    constructor(long: Long) : this(long.toInt()) {

    }

    companion object {
        var a = -1

        fun met() {

        }
    }
}

object Singleton {
    var a: Int = -1
}

data class Me(
    val name: String,
    val birthYear: Int
) {
    val age: Int
        get() = LocalDate.now().year - birthYear
}

sealed class Result(val code: Int) {
    class Ok: Result(200)
    class Error(errorCode: Int): Result(errorCode)
}

class MyClass2 (var asdf: Int = 123)

class Props {
    lateinit var lateInited: String
    var nullable: String? = null
    val nonNullable: String by lazy { LocalDateTime.now().toString() }

    var everything: Int = 10
        get() = 42

    private var _myList = mutableListOf(1, 2, 3)
    val myList: List<Int> = _myList

    var ten: Int = 10
        private set(value) {
            if (value != 10) {
                println("You can only assign 10 to this var")
            } else {
                field = value
            }
        }

    init {
//        ::lateInited.isInitialized
    }
}

fun interface Power {
    fun powOfTwo(a: Int)
}

fun MyClass.Companion.beta() = 2

fun doOnLambdaThis(value: Int, lambda: Int.() -> Unit) = value.lambda()

interface Expression {
    fun calculate(first: Int, second: Int): Int
}

object AddExpression : Expression {
    override fun calculate(first: Int, second: Int): Int = first + second
}

class SomeCalculator: Expression by AddExpression

abstract class AbstractClass {
    abstract val a: Int

    abstract fun method()
}