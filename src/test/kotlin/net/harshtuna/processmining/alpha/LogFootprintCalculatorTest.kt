package net.harshtuna.processmining.alpha

import net.harshtuna.processmining.alpha.EventRelation.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class LogFootprintCalculatorTest {

    @Test
    fun footprint() {
        assertEquals(
            mapOf(
                "A" to mapOf("A" to CHOICE, "B" to CAUSALITY),
                "B" to mapOf("A" to REVERSE, "B" to CHOICE)
            ),
            LogFootprintCalculator.footprint(setOf(listOf("A", "B")))
        )
    }

    @Test
    fun footprintL1() {
        assertEquals(
            mapOf(
                "a" to mapOf("a" to CHOICE, "b" to CAUSALITY, "c" to CAUSALITY, "d" to CHOICE, "e" to CAUSALITY),
                "b" to mapOf("a" to REVERSE, "b" to CHOICE, "c" to PARALLEL, "d" to CAUSALITY, "e" to CHOICE),
                "c" to mapOf("a" to REVERSE, "b" to PARALLEL, "c" to CHOICE, "d" to CAUSALITY, "e" to CHOICE),
                "d" to mapOf("a" to CHOICE, "b" to REVERSE, "c" to REVERSE, "d" to CHOICE, "e" to REVERSE),
                "e" to mapOf("a" to REVERSE, "b" to CHOICE, "c" to CHOICE, "d" to CAUSALITY, "e" to CHOICE)
            ),
            LogFootprintCalculator.footprint(
                setOf(
                    listOf("a", "b", "c", "d"),
                    listOf("a", "c", "b", "d"),
                    listOf("a", "e", "d")
                )
            )
        )
    }
}