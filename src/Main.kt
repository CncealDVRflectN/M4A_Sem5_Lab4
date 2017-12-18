fun calcF(x: Double): Double {
    return 0.5 - Math.pow(x, 2.0)
}

fun calcP(x: Double): Double {
    return -6 * x / (3 * Math.pow(x, 2.0))
}

fun calcQ(x: Double): Double {
    return -1 / x
}

fun gauss(aMtr: Matrix, bVect: Vector): Vector {
    val a = Matrix(aMtr)
    val b = Vector(bVect)
    val x = Vector(b.size)
    val xIndexes = Vector(b.size)
    var max: Double
    var maxk: Int
    var index: Int

    for (i in 0 until x.size) {
        xIndexes[i] = i.toDouble()
    }

    for (k in 0 until x.size) {
        max = a[k][k]
        maxk = k
        for (i in k + 1 until x.size) {
            if (Math.abs(max) < Math.abs(a[k][i])) {
                max = a[k][i]
                maxk = i
            }
        }
        if (maxk != k) {
            a.swapColumns(k, maxk)
            xIndexes.swap(k, maxk)
        }
        for (j in k until x.size) {
            a[k][j] /= max
        }
        b[k] /= max
        for (i in k + 1 until x.size) {
            for (j in k + 1 until x.size) {
                a[i][j] -= a[i][k] * a[k][j]
            }
            b[i] -= a[i][k] * b[k]
            a[i][k] = 0.0
        }
    }

    for (i in x.size - 1 downTo 0) {
        index = xIndexes[i].toInt()
        x[index] = b[i]
        for (j in i + 1 until x.size) {
            x[index] -= a[i][j] * x[xIndexes[j].toInt()]
        }
    }

    return x
}

fun calcFirstCauchyProblem(nodes: Vector, step: Double): Pair<Vector, Vector> {
    val resultY1 = Vector(nodes.size)
    val resultY2 = Vector(nodes.size)
    var fi: Double

    resultY1[0] = 0.0
    resultY2[0] = 0.0
    for (i in 1 until nodes.size) {
        fi = calcF(nodes[i - 1]) - calcP(nodes[i - 1]) * resultY2[i - 1] - calcQ(nodes[i - 1]) * resultY1[i - 1]
        resultY2[i] = resultY2[i - 1] + step * (fi  + calcF(nodes[i]) - calcP(nodes[i]) * (resultY2[i - 1] + step * fi) -
                calcQ(nodes[i]) * (resultY1[i - 1] + step * resultY2[i - 1])) / 2
        resultY1[i] = resultY1[i - 1] + step * (resultY2[i - 1] + resultY2[i - 1] + step * fi) / 2
    }

    return Pair(resultY1, resultY2)
}

fun calcSecondCauchyProblem(nodes: Vector, step: Double): Pair<Vector, Vector> {
    val resultY1 = Vector(nodes.size)
    val resultY2 = Vector(nodes.size)
    var fi: Double

    resultY1[0] = 1.0
    resultY2[0] = 0.0
    for (i in 1 until nodes.size) {
        fi = -calcP(nodes[i - 1]) * resultY2[i - 1] - calcQ(nodes[i - 1]) * resultY1[i - 1]
        resultY2[i] = resultY2[i - 1] + step * (fi - calcP(nodes[i]) * (resultY2[i - 1] + step * fi) -
                calcQ(nodes[i]) * (resultY1[i - 1] + step * resultY2[i - 1])) / 2
        resultY1[i] = resultY1[i - 1] + step * (resultY2[i - 1] + resultY2[i - 1] + step * fi) / 2
    }

    return Pair(resultY1, resultY2)
}

fun calcThirdCauchyProblem(nodes: Vector, step: Double): Pair<Vector, Vector> {
    val resultY1 = Vector(nodes.size)
    val resultY2 = Vector(nodes.size)
    var fi: Double

    resultY1[0] = 0.0
    resultY2[0] = 1.0
    for (i in 1 until nodes.size) {
        fi = -calcP(nodes[i - 1]) * resultY2[i - 1] - calcQ(nodes[i - 1]) * resultY1[i - 1]
        resultY2[i] = resultY2[i - 1] + step * (fi - calcP(nodes[i]) * (resultY2[i - 1] + step * fi) -
                calcQ(nodes[i]) * (resultY1[i - 1] + step * resultY2[i - 1])) / 2
        resultY1[i] = resultY1[i - 1] + step * (resultY2[i - 1] + resultY2[i - 1] + step * fi) / 2
    }

    return Pair(resultY1, resultY2)
}

fun calcReductionMethod(nodes: Vector, step: Double, alpha: Vector, beta: Vector, gamma: Vector): Vector {
    val solutionFirstProblem = calcFirstCauchyProblem(nodes, step)
    val solutionSecondProblem = calcSecondCauchyProblem(nodes, step)
    val solutionThirdProblem = calcThirdCauchyProblem(nodes, step)
    val coefsMtr = Matrix(2, 2)
    val coefsVect = Vector(2)
    val coefs: Vector
    val result = Vector(nodes.size)

    coefsMtr[0][0] = alpha[0]
    coefsMtr[0][1] = beta[0]
    coefsMtr[1][0] = alpha[1] * solutionSecondProblem.first[nodes.size - 1] + beta[1] * solutionSecondProblem.second[nodes.size - 1]
    coefsMtr[1][1] = alpha[1] * solutionThirdProblem.first[nodes.size - 1] + beta[1] * solutionThirdProblem.second[nodes.size - 1]

    coefsVect[0] = gamma[0]
    coefsVect[1] = gamma[1] - alpha[1] * solutionFirstProblem.first[nodes.size - 1] - beta[1] * solutionFirstProblem.second[nodes.size - 1]

    coefs = gauss(coefsMtr, coefsVect)

    for (i in 0 until nodes.size) {
        result[i] = solutionFirstProblem.first[i] + coefs[0] * solutionSecondProblem.first[i] +
                coefs[1] * solutionThirdProblem.first[i]
    }

    return result
}

fun main(args: Array<String>) {
    val n = 10
    val intervalBottom = 0.5
    val intervalUpper = 1.0
    val step = (intervalUpper - intervalBottom) / n
    val nodes = Vector(n + 1)
    val alpha = Vector(2)
    val beta = Vector(2)
    val gamma = Vector(2)

    for (i in 0 until nodes.size) {
        nodes[i] = intervalBottom + i * step
    }

    alpha[0] = 0.0
    alpha[1] = 2.0
    beta[0] = 1.0
    beta[1] = 1.0
    gamma[0] = 0.25
    gamma[1] = 3.5

    println("Метод редукции:")
    println("Узлы:")
    nodes.print()
    println("Значения искомой функции в узлax:")
    calcReductionMethod(nodes, step, alpha, beta, gamma).print()
    println()
}