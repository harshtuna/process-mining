package net.harshtuna.processmining.alpha

import net.harshtuna.processmining.wfnet.Arc
import net.harshtuna.processmining.wfnet.Constants.end
import net.harshtuna.processmining.wfnet.Constants.start
import net.harshtuna.processmining.wfnet.Place
import net.harshtuna.processmining.wfnet.Transition
import net.harshtuna.processmining.wfnet.WorkflowNet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class AlphaAlgorithmTest {

    @Test
    fun simpleSequence() {
        val a = Transition("A")
        val b = Transition("B")
        val a2b = Place("A -> B")
        assertEquals(
            WorkflowNet(
                places = setOf(start, a2b, end),
                transitions = setOf(a, b),
                arcs = setOf(Arc(start, a), Arc(a, a2b), Arc(a2b, b), Arc(b, end)),
                start = start,
                end = end
            ),
            AlphaAlgorithm.apply(setOf(listOf("A", "B")))
        )
    }

    @Test
    fun simpleSplitJoin() {
        val a = Transition("a")
        val b = Transition("b")
        val c = Transition("c")
        val d = Transition("d")
        val e = Transition("e")
        val a2be = Place("a -> b,e")
        val a2ce = Place("a -> c,e")
        val be2d = Place("b,e -> d")
        val ce2d = Place("c,e -> d")
        assertEquals(
            WorkflowNet(
                places = setOf(start, a2be, a2ce, be2d, ce2d, end),
                transitions = setOf(a, b, c, d, e),
                arcs = setOf(Arc(start, a),
                    Arc(a, a2be), Arc(a2be, b), Arc(a2be, e),
                    Arc(a, a2ce), Arc(a2ce, c), Arc(a2ce, e),
                    Arc(b, be2d), Arc(e, be2d), Arc(be2d, d),
                    Arc(c, ce2d), Arc(e, ce2d), Arc(ce2d, d),
                    Arc(d, end)),
                start = start,
                end = end
            ),
            AlphaAlgorithm.apply(
                setOf(
                    listOf("a", "b", "c", "d"),
                    listOf("a", "c", "b", "d"),
                    listOf("a", "e", "d")
                )
            )
        )
    }

    @Test
    fun testSplitByCausality() {
        assertEquals(
            listOf(setOf("A") to setOf("B")),
            AlphaAlgorithm.splitByCausality(setOf(listOf("A", "B")))
        )
        assertEquals(
            listOf(
                setOf("a") to setOf("b", "e"),
                setOf("a") to setOf("c", "e"),
                setOf("b", "e") to setOf("d"),
                setOf("c", "e") to setOf("d")
            ),
            AlphaAlgorithm.splitByCausality(
                setOf(
                    listOf("a", "b", "c", "d"),
                    listOf("a", "c", "b", "d"),
                    listOf("a", "e", "d")
                )
            )
        )
    }
}