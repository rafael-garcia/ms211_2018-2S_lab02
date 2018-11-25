import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.exp
import kotlin.math.pow

/**
 * Autor: Rafael Matheus Garcia - RA 121295
 * MS211 - Turma K - 2o semestre de 2018
 * Professor João Batista Florindo
 * Enunciado - [http://www.ime.unicamp.br/~jbflorindo/Teaching/2018/MS211/Projeto2_Completo.pdf]
 *
 * Esse arquivo foi criado em Kotlin, uma linguagem que roda na JVM.
 * Para executar o código online, basta copiá-lo nesse interpretador: http://try.kotlinlang.org/
 *
 */

/**
 * Função que calcula a equação logística, i.e. o y' = f(tn, yn) do enunciado.
 */
fun fTnYn(tn: Double, yn: Double, r: Double = DEF_R, K: Double = DEF_K): Double {
    return r*yn*(1 - (yn/K))
}

/**
 * Definição de y(t) analítico conforme enunciado.
 * Deixei parametrizado o r e o K, mas com valores padrão conforme o item a do laboratório.
 */
fun yOfT(t: Double, y0: Double, r: Double = DEF_R, K: Double = DEF_K): Double {
    val expRT = Math.exp(r*t) // e^(r*t)
    return (K*y0*expRT) / (K + y0*(expRT - 1))
}

/**
 * Método iterativo de aproximação de Euler.
 * A cada iteração um yn_+1 novo é calculado, baseado no yn anterior, no passo e no f(t,yn)
 * Os resultados de cada yn são salvos numa lista e retornados
 */
fun euler(t0: Double, y0: Double, h: Double, r: Double = DEF_R, K: Double = DEF_K, n: Int = DEF_N): List<Double> {
    val results = mutableListOf<Double>()
    var yn = y0 + h * fTnYn(t0, y0, r, K) // primeira iteração
    results.add(yn)

    val steps: Int  = (n / h).toInt()

    for (i in 0..steps) {
        println("i = ${i}")
        val tn = t0 + h
        yn += h * yOfT(tn, yn, r, K)
        results.add(yn)
    }

    return results
}

/**
 * Função que compara os resultados obtidos pelo método de Euler e pelo y(t) analítico para t0 até tn, sendo acrescido
 * do passo h a cada iteração.
 */
fun compareEulerToAnalytical(t0: Double, y0: Double, h: Double, r: Double, K: Double, n: Int): Map<String, List<Double>> {
    val eulerList = euler(t0, y0, h, r, K, n)
    val analyticalList = mutableListOf<Double>()

    val steps: Int  = Math.ceil(n / h).toInt()
    for (i in 0..steps) {
        val tn = t0 + h
        analyticalList.add(yOfT(tn, y0, r, K))
    }

    return mapOf(Pair("euler", eulerList), Pair("analytical", analyticalList))
}

/**
 * Definição da equação de Butler-Volmer conforme enunciado.
 * Deixei parametrizado o alpha e o Beta, mas com os valores padrão seguindo os valores dados.
 */
fun butlerVolmer(x: Double, alpha: Double, beta: Double): Double {
    //e^( alpha * x) - e^( (1 - alpha) * x)

    return exp(alpha * x) - exp((alpha - 1) * x) - beta
}

fun firstDerivative(alpha: Double, x: Double): Double {
    return exp((alpha - 1) * x) * (alpha * (exp(x) - 1) + 1)
}

private fun acceptableInterval(a: Double, b: Double, prec: Double): Boolean {
    return (b - a) < prec
}

/**
 * Método iterativo de bisecção.
 */
fun bisection(a_param: Double, b_param: Double, prec: Double, alpha: Double, beta: Double): Pair<Int, Double> {

    var a = a_param
    var b = b_param

    var k = 0

    while (k < MAX_ITERATIONS) { // limite de iterações para evitar loop infinito
        val x: Double = (a + b) / 2

        if (acceptableInterval(a, b, prec)) { // (b - a) < ε
            return Pair(k, x) // encerra loop com o valor da aproximação de x
        }
        val m: Double = butlerVolmer(a, alpha, beta) // M = f(a)
        val f: Double = butlerVolmer(x, alpha, beta) // f(x) para o Bolzano
        if (m * f > 0) { // atualiza intervalo para [x, b]
            a = x
        } else {  // atualiza intervalo para [a, x]
            b = x
        }

        k++
    }
    // exceção quando não foi possível completar a execução num tempo razoável
    throw Exception("Número máx. de iterações alcançado.")
}

fun newton(x0: Double, prec1: Double, prec2: Double, alpha: Double, beta: Double): Pair<Int, Double> {
    var k = 0
    var x_0 = x0

    while (k < MAX_ITERATIONS) { // limite de iterações para evitar loop infinito
        val f_0: Double = butlerVolmer(x_0, alpha, beta) // calcula f(0)

        if (f_0.absoluteValue < prec1) { // menor do que a tolerância, pode retornar o valor aproximado
            return Pair(k, x_0)
        }

        val d_f_0: Double = firstDerivative(alpha, x_0) // calcula a derivada primeira de f(x)
        val x_1: Double = x_0 - (f_0 / d_f_0) // x1 <-- o valor da nova aproximação
        val f_1: Double = butlerVolmer(x_1, alpha, beta) // calcula o valor da função com a nova aproximação

        if (f_1.absoluteValue < prec1 || abs(f_1 - f_0) < prec2) { // já está suficientemente próximo da raiz
            return Pair(k, x_1)
        }

        x_0 = x_1

        k++
    }
    // exceção quando não foi possível completar a execução num tempo razoável
    throw Exception("Número máx. de iterações alcançado")
}

fun main(args: Array<String>) {
    val alpha = 0.2
    val beta = 2.0
    val bisectionRanges = arrayOf<Pair<Double, Double>>( // a,b respectivamente
            Pair(3.4, 3.8),
            Pair(3.0, 4.0),
            Pair(1.0, 5.0),
            Pair(-15.0, 15.0),
            Pair(-150.0, 155.0))
    val newtonInitialValues = doubleArrayOf(
            3.4,
            3.8,
            3.0,
            4.0,
            1.0,
            5.0,
            -15.0,
            15.0,
            -150.0,
            155.0
    )

    println("Aproximações por método da Bisecção: |a|b|x|k|")
    // chama o método da bisecção para os valores colocados em bisection_ranges (a,b)
    for ((a, b) in bisectionRanges) {
        try {
            val (bisection_k, bisection_x) = bisection(a, b, 10.0.pow(PRECISION), alpha, beta)
            println("|$a|$b|%.${FORMAT}f|$bisection_k".format(bisection_x))
        } catch (e: Exception) { // alcançou o número máximo de iterações, grava e vai para a próxima iteração
            println("${e.message} para intervalo (a,b) = $a, $b")
            continue
        }
    }

    println("Aproximações por método da Newton: |x0|x|k|")
    for (x in newtonInitialValues) {
        try {
            val (newton_k, newton_x) = newton(x, 10.0.pow(PRECISION), 10.0.pow(PRECISION), alpha, beta)
            println("|$x|%.${FORMAT}f|$newton_k".format(newton_x))
        } catch (e: Exception) { // alcançou o número máximo de iterações, grava e vai para a próxima iteração
            println("${e.message} para x0 = $x")
            continue
        }
    }
}

const val PRECISION = -7
const val FORMAT = -PRECISION
const val MAX_ITERATIONS = 1000L // apenas um limite de segurança para evitar loops infinitos
const val DEF_R = 0.5
const val DEF_K = 10.0
const val DEF_N = 4
const val DEF_H = 0.05

