import java.math.BigDecimal
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
 * Função auxiliar usada para imprimir na saída padrão alguma string que possa ajudar no ambiente de debug.
 * Para isso basta passar o parametro debugging = true
 */
fun debug(str: String, debugging: Boolean = false) {
    if (debugging) {
        println(str)
    }
}

/**
 * Função que calcula a equação logística, i.e. o y' = f(tn, yn) do enunciado.
 * Note que não há termo t nessa equação, ela é toda em função de yn.
 */
fun fTnYn(yn: Double, r: Double = DEF_R, K: Double = DEF_K): Double {
    return r*yn*(1 - (yn/K))
}

/**
 * Definição de y(t) analítico conforme enunciado.
 * Deixei parametrizado o r e o K, mas com valores padrão conforme o item 'A' do laboratório.
 */
fun yOfT(t: Double, y0: Double = DEF_Y0, r: Double = DEF_R, K: Double = DEF_K): Double {
    val expRT = Math.exp(r*t) // e^(r*t)
    return (K*y0*expRT) / (K + y0*(expRT - 1))
}

/**
 * Método iterativo de aproximação de Euler.
 * A cada iteração um y_n+1 novo é calculado, baseado no yn anterior, no passo e no f(t,yn)
 * Os resultados de cada yn são salvos numa lista e retornados
 */
fun euler(t0: Double = DEF_T0, y0: Double = DEF_Y0, h: Double = DEF_H, r: Double = DEF_R, K: Double = DEF_K, tnMax: Int = DEF_TN): List<Double> {
    debug("Aproximação por método de Euler para t em [$t0..$tnMax]:")
    debug(", tn , y(tn) , h , r , K ,")
    val results = mutableListOf<Double>()

    // inicializa as variáveis da iteração com t0 e y0
    var tn = BigDecimal(t0) // usando BigDecimal para melhorar a precisão de operações com ponto flutuante
    var yn = y0

    while (tn <= tnMax.toBigDecimal()) { // enquanto o tn atual for menor ou igual ao tnMáximo
        debug(", %.${FORMAT}f , %.${FORMAT}f , ${h} , ${r} , ${K} ,".format(tn, yn))
        yn += h * fTnYn(yn, r, K)
        results.add(yn)
        tn = tn.add(h.toBigDecimal()) // tn = tn + h
    }

    return results
}

/**
 * Função que compara os resultados obtidos pelo método de Euler e pelo y(t) analítico para t0 até tn, sendo acrescido
 * do passo h a cada iteração.
 */
fun analytical(t0: Double = DEF_T0, y0: Double = DEF_Y0, h: Double = DEF_H, r: Double = DEF_R, K: Double = DEF_K, tnMax: Int = DEF_TN): List<Double> {
    debug("Forma analítica de y(t) para t em [$t0..$tnMax]:")
    debug(", tn , y(tn) , h , r , K ,")
    val analyticalList = mutableListOf<Double>()

    // inicializa tn com t0
    var tn = BigDecimal(t0)

    while (tn <= tnMax.toBigDecimal()) { // enquanto o tn atual for menor ou igual ao tnMáximo
        val yn = yOfT(tn.toDouble(), y0, r, K) // calcula o valor de y(tn) analítico
        debug(", %.${FORMAT}f , %.${FORMAT}f , ${h} , ${r} , ${K} ,".format(tn, yn))
        analyticalList.add(yn)
        tn = tn.add(h.toBigDecimal()) // tn = tn + h
    }

    return analyticalList
}

/**
 * Método iterativo de aproximação por Runge-Kutta de 4a ordem.
 * A cada iteração um y_n+1 novo é calculado baseaddo em K1, K2, K3 e K4
 * K1 = h*f(tn, yn)
 * K2 = h*f(tn + h/2, yn + K1/2)
 * K3 = h*f(tn + h/2, yn + K2/2)
 * K4 = h*f(tn + h, yn + K3)
 * y_n+1 = yn + (1/6)*(K1 + 2K2 + 2K3 + K4)
 * Os resultados de cada yn são salvos numa lista e retornados
 */
fun rungeKutta4th(t0: Double = DEF_T0, y0: Double = DEF_Y0, h: Double = DEF_H, r: Double = DEF_R, K: Double = DEF_K, tnMax: Int = DEF_TN): List<Double> {
    debug("Aproximação por Runge-Kutta 4a ordem para t em [$t0..$tnMax]:")
    debug(", tn , y(tn) , h , r , K ,")
    val results = mutableListOf<Double>()

    // inicializa as variáveis da iteração com t0 e y0
    var tn = BigDecimal(t0) // usando BigDecimal para melhorar a precisão de operações com ponto flutuante
    var yn = y0

    while (tn <= tnMax.toBigDecimal()) { // enquanto o tn atual for menor ou igual ao tnMáximo
        debug(", %.${FORMAT}f , %.${FORMAT}f , ${h} , ${r} , ${K} ,".format(tn, yn))
        val k1 = h * fTnYn(yn)
        val k2 = h * fTnYn(yn + k1/2, r, K)
        val k3 = h * fTnYn(yn + k2/2, r, K)
        val k4 = h * fTnYn(yn + k3, r, K)
        yn += ((k1 + 2*k2 + 2*k3 + k4) / 6)// aqui calculamos o y_n+1
        results.add(yn)
        tn = tn.add(h.toBigDecimal()) // tn = tn + h
    }

    return results
}

fun compareResults(euler: List<Double>, analytical: List<Double>, rk4th: List<Double>, h: Double) {
    println("tn + $h , y(tn) , euler(tn) , rk4th(tn), | y(tn) - euler(tn) |, | y(tn) - rk4th(tn) | , | euler(tn) - rk4th(tn) | ,")
    for (i in euler.indices) {
        val deltaEuler = Math.abs(analytical[i] - euler[i])
        val deltaRK4th = Math.abs(analytical[i] - rk4th[i])
        val deltaEulerToRK = Math.abs(euler[i] - rk4th[i])
        println("%.${FORMAT}f , %.${FORMAT}f , %.${FORMAT}f , %.${FORMAT}f, %.${FORMAT}f, %.${FORMAT}f, %.${FORMAT}f ,"
            .format(h*i, analytical[i], euler[i], rk4th[i], deltaEuler, deltaRK4th, deltaEulerToRK))
    }
    println("=".padEnd(80, '=') + "\n")
}

fun main(args: Array<String>) {
//======================================================================================================================
    // item A, executa o método de Euler e compara a aprox. obtida com o resultado de cada y(tn) analítico correspondente.
    // resultado com K = 10, h = 0.05
    compareResults(euler(), analytical(), rungeKutta4th(), DEF_H)

//======================================================================================================================
// item B, modifica alguns parâmetros e faz novamente a comparação.

    val hBad = 1.0
    // resultado com K = 10, h = 1.0
    compareResults(euler(h = hBad), analytical(h = hBad), rungeKutta4th(h = hBad), hBad)


    val h1 = 0.5
    // resultado com K = 10, h = 0.5
    compareResults(euler(h = h1), analytical(h = h1), rungeKutta4th(h = h1), h1)

    val h2 = 0.0005
    // resultado com K = 10, h = 0.0005
    compareResults(euler(h = h2), analytical(h = h2), rungeKutta4th(h = h2), h2)

    val hOpt = 0.00001
    // resultado com K = 10, h = 0.00001
    compareResults(euler(h = hOpt), analytical(h = hOpt), rungeKutta4th(h = hOpt), hOpt)

// Para K = 100
// Resultado com K = 100, h = 0.05
//    compareResults(euler(K = 100.0), analytical(K = 100.0), DEF_H)
// Resultado com K = 100, h = 1.5
//    compareResults(euler(h = hBad, K = 100.0), analytical(h = hBad, K = 100.0), hBad)
// Resultado com K = 100, h = 0.5
//    compareResults(euler(h = h1, K = 100.0), analytical(h = h1, K = 100.0), h1)
// Resultado com K = 100, h = 0.0005
//    compareResults(euler(h = h2), analytical(h = h2), h2)
// Resultado com K = 100, h = 0.00001
//    compareResults(euler(h = hOpt), analytical(h = hOpt), hOpt)
}

const val PRECISION = -5
const val FORMAT = -PRECISION
const val DEF_R = 0.5
const val DEF_K = 10.0
const val DEF_H = 0.05
const val DEF_Y0 = 1.0
const val DEF_T0 = 0.0
const val DEF_TN = 4

