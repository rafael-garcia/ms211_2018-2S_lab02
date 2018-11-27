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

fun debug(str: String, debugging: Boolean = false) {
    if (debugging) {
        println(str)
    }
}

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
fun yOfT(t: Double, y0: Double = DEF_Y0, r: Double = DEF_R, K: Double = DEF_K): Double {
    val expRT = Math.exp(r*t) // e^(r*t)
    return (K*y0*expRT) / (K + y0*(expRT - 1))
}

/**
 * Método iterativo de aproximação de Euler.
 * A cada iteração um yn_+1 novo é calculado, baseado no yn anterior, no passo e no f(t,yn)
 * Os resultados de cada yn são salvos numa lista e retornados
 */
fun euler(t0: Double = DEF_T0, y0: Double = DEF_Y0, h: Double = DEF_H, r: Double = DEF_R, K: Double = DEF_K, tnMax: Int = DEF_TN): List<Double> {
    debug("Aproximação por método de Euler para t em [$t0..$tnMax]:")
    debug(", tn , y(tn) , h , r , K ,")
    val results = mutableListOf<Double>()
    var tn = t0
    var yn = y0

    val steps: Int  = Math.ceil(tnMax / h).toInt()
    for (i in 0..steps) {
        debug(", %.${FORMAT}f , %.${FORMAT}f , ${h} , ${r} , ${K} ,".format(tn, yn))
        yn += h * fTnYn(tn, yn, r, K)
        results.add(yn)
        tn += h
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
    var tn = t0


    val steps: Int  = Math.ceil(tnMax / h).toInt()
    for (i in 0..steps) {
        val yn = yOfT(tn, y0, r, K)
        debug(", %.${FORMAT}f , %.${FORMAT}f , ${h} , ${r} , ${K} ,".format(tn, yn))
        analyticalList.add(yn)
        tn += h
    }

    return analyticalList
}

fun compareResults(euler: List<Double>, analytical: List<Double>, h: Double) {
    println("tn+1 = tn + $h , y(tn) , euler(tn) , EA ,")
    for (i in euler.indices) {
        val deltaT = Math.abs(analytical[i] - euler[i])
        println("%.${FORMAT}f, %.${FORMAT}f , %.${FORMAT}f , %.${FORMAT}f ,"
            .format(h*i, analytical[i], euler[i], deltaT))
    }
    println("=".padEnd(80, '=') + "\n")
}

fun main(args: Array<String>) {
//======================================================================================================================
    // item A, executa o método de Euler e compara a aprox. obtida com o resultado de cada y(tn) analítico correspondente.
    // resultado com K = 10, h = 0.05
    compareResults(euler(), analytical(), DEF_H)

//======================================================================================================================
// item B, modifica alguns parâmetros e faz novamente a comparação.

    // resultado com K = 100, h = 0.05
    compareResults(euler(K = 100.0), analytical(K = 100.0), DEF_H)

    val hBad = 1.5
    // resultado com K = 10, h = 1.5
    compareResults(euler(h = hBad), analytical(h = hBad), hBad)

    // resultado com K = 100, h = 1.5
    compareResults(euler(h = hBad, K = 100.0), analytical(h = hBad, K = 100.0), hBad)

    val h1 = 0.5
    // resultado com K = 10, h = 0.5
    compareResults(euler(h = h1), analytical(h = h1), h1)

    // resultado com K = 10, h = 0.5
    compareResults(euler(h = h1, K = 100.0), analytical(h = h1, K = 100.0), h1)

    val h2 = 0.0005
    // resultado com K = 10, h = 0.0005
    compareResults(euler(h = h2), analytical(h = h2), h2)

    // resultado com K = 100, h = 0.0005
    compareResults(euler(h = h2), analytical(h = h2), h2)

    val hOpt = 0.00001
    // resultado com K = 10, h = 0.00001
    compareResults(euler(h = hOpt), analytical(h = hOpt), hOpt)

    // resultado com K = 100, h = 0.00001
    compareResults(euler(h = hOpt), analytical(h = hOpt), hOpt)
}

const val PRECISION = -5
const val FORMAT = -PRECISION
const val DEF_R = 0.5
const val DEF_K = 10.0
const val DEF_H = 0.05
const val DEF_Y0 = 1.0
const val DEF_T0 = 0.0
const val DEF_TN = 4

