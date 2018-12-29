
package net.harshtuna.processmining

import net.harshtuna.processmining.alpha.AlphaAlgorithm

fun main(args: Array<String>) {
    println(AlphaAlgorithm.apply(
        setOf(
            listOf("a", "b", "c", "d"),
            listOf("a", "c", "b", "d"),
            listOf("a", "e", "d")
        )
    ))
}
