package net.harshtuna.processmining.alpha

import net.harshtuna.processmining.wfnet.Arc
import net.harshtuna.processmining.wfnet.Constants.end
import net.harshtuna.processmining.wfnet.Constants.start
import net.harshtuna.processmining.wfnet.Place
import net.harshtuna.processmining.wfnet.Transition
import net.harshtuna.processmining.wfnet.WorkflowNet

object AlphaAlgorithm {
    fun apply(eventLog: SimpleEventLog): WorkflowNet {
        val transitions = eventLog.flatten().distinct().map { Transition(it) }.toSet()
        // todo - simplify
        val firstTransitions = eventLog.map { event -> transitions.first { it.name == event.first() } }.toSet()
        val lastTransitions = eventLog.map { event -> transitions.first { it.name == event.last() } }.toSet()
        val (innerPlaces, innerArcs) = splitByCausality(eventLog).fold(mutableSetOf<Place>() to mutableSetOf<Arc>()) { acc, (cause, effect) ->
            val (places, arcs) = acc
            val place = Place(cause.joinToString(",") + " -> " + effect.joinToString(","))
            places.add(place)
            cause.map { event -> transitions.first { it.name == event } }.forEach { arcs.add(Arc(it, place)) }
            effect.map { event -> transitions.first { it.name == event } }.forEach { arcs.add(Arc(place, it)) }
            acc
        }
        val places = innerPlaces + setOf(start, end)
        val arcs = innerArcs +
                firstTransitions.map { Arc(start, it) }.toSet() +
                lastTransitions.map { Arc(it, end) }.toSet()
        return WorkflowNet(
            places = places,
            transitions = transitions,
            arcs = arcs,
            start = start,
            end = end
        )
    }

    // fixme - brute force, optimize
    internal fun splitByCausality(eventLog: SimpleEventLog): List<Pair<Set<String>, Set<String>>> {
        val footprint = LogFootprintCalculator.footprint(eventLog)
        val result = mutableListOf<Pair<MutableSet<String>, MutableSet<String>>>()
        footprint.forEach { first ->
            first.value.forEach { second ->
                if (
                    second.value == EventRelation.CAUSALITY &&
                    footprint[first.key]?.get(first.key) == EventRelation.CHOICE &&
                    footprint[second.key]?.get(second.key) == EventRelation.CHOICE
                ) {
                    result.filter { (cause, effect) ->
                        cause.all {
                            footprint[it]?.get(first.key) == EventRelation.CHOICE &&
                                    footprint[it]?.get(second.key) == EventRelation.CAUSALITY
                        } && effect.all {
                            footprint[it]?.get(second.key) == EventRelation.CHOICE &&
                                    footprint[first.key]?.get(it) == EventRelation.CAUSALITY
                        }
                    }
                        .let {
                            if (it.isEmpty()) listOf(mutableSetOf<String>() to mutableSetOf<String>()).apply {
                                result.addAll(
                                    this
                                )
                            } else it
                        }
                        .forEach { causeEffectPair ->
                            causeEffectPair.first.add(first.key)
                            causeEffectPair.second.add(second.key)
                        }
                }
            }
        }
        return result
    }
}