class Matrix(var lineNum: Int, var columnNum: Int) {
    var matrix: Array<Array<Double>> = Array(lineNum, { Array(columnNum, { 0.0 }) })

    constructor(initMtr: Array<Array<Double>>) : this(initMtr.size, initMtr[0].size) {
        for (i in matrix.indices) {
            for (j in matrix[0].indices) {
                matrix[i][j] = initMtr[i][j]
            }
        }
    }

    constructor(initMtr: Matrix) : this(initMtr.matrix)

    fun swapColumns(from: Int, to: Int) {
        var tmp: Double
        for (i in 0 until lineNum) {
            tmp = matrix[i][from]
            matrix[i][from] = matrix[i][to]
            matrix[i][to] = tmp
        }
    }

    fun print() {
        for (i in 0 until lineNum) {
            for (j in 0 until columnNum) {
                print(matrix[i][j].toString() + " ")
            }
            println()
        }
    }

    operator fun times(b: Vector): Vector {
        var result: Vector
        if (columnNum != b.size) {
            throw Exception("Incorrect size")
        }
        result = Vector(lineNum)
        for (i in 0 until lineNum) {
            result[i] = (0 until columnNum).sumByDouble { matrix[i][it] * b[it] }
        }
        return result
    }

    operator fun get(i: Int): Array<Double> {
        return matrix[i]
    }
}