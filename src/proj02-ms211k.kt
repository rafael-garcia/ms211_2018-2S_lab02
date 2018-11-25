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
fun yOfT(t: Double, y0: Double = DEF_Y0, r: Double = DEF_R, K: Double = DEF_K): Double {
    val expRT = Math.exp(r*t) // e^(r*t)
    return (K*y0*expRT) / (K + y0*(expRT - 1))
}

/**
 * Método iterativo de aproximação de Euler.
 * A cada iteração um yn_+1 novo é calculado, baseado no yn anterior, no passo e no f(t,yn)
 * Os resultados de cada yn são salvos numa lista e retornados
 */
fun euler(t0: Double = DEF_T0, y0: Double = DEF_Y0, h: Double = DEF_H, r: Double = DEF_R, K: Double = DEF_K, tn: Int = DEF_TN): List<Double> {
    println("Aproximação por método de Euler:")
    val results = mutableListOf<Double>()
    var tj = t0;
    var yn = y0;

    val steps: Int  = Math.ceil(tn / h).toInt()
    for (i in 0..steps) {
        println("| t{$i} = ${tj} | y{$i} = ${yn} | h = ${h} | r = ${r} | K = ${K} | n = ${tn} |")
        yn += h * yOfT(tj, yn, r, K)
        results.add(yn)
        tj += h;
    }

    return results
}

/**
 * Função que compara os resultados obtidos pelo método de Euler e pelo y(t) analítico para t0 até tn, sendo acrescido
 * do passo h a cada iteração.
 */
fun analytical(t0: Double = DEF_T0, y0: Double = DEF_Y0, h: Double = DEF_H, r: Double = DEF_R, K: Double = DEF_K, tn: Int = DEF_TN): List<Double> {
    println("Forma analítica de y(t)")
    val analyticalList = mutableListOf<Double>()

    val steps: Int  = Math.ceil(tn / h).toInt()
    for (i in 0..steps) {
        val tn = t0 + h
        val yn = yOfT(tn, y0, r, K)
        println("| t{$i} = ${tn} | y(t${i}) = ${yn} | h = ${h} | r = ${r} | K = ${K} | tn = ${tn} |")
        analyticalList.add(yn)
    }

    return analyticalList
}


fun main(args: Array<String>) {
    euler()
    analytical()

//    val eulerList = euler(t0, y0, h, r, K, n)
}

const val PRECISION = -7
const val FORMAT = -PRECISION
const val MAX_ITERATIONS = 1000L // apenas um limite de segurança para evitar loops infinitos
const val DEF_R = 0.5
const val DEF_K = 10.0
const val DEF_H = 0.05
const val DEF_Y0 = 1.0
const val DEF_T0 = 0.0
const val DEF_TN = 4

