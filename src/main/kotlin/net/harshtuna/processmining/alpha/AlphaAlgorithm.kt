package net.harshtuna.processmining.alpha

import net.harshtuna.processmining.wfnet.Arc
import net.harshtuna.processmining.wfnet.Constants.end
import net.harshtuna.processmining.wfnet.Constants.start
import net.harshtuna.processmining.wfnet.Place
import net.harshtuna.processmining.wfnet.Transition
import net.harshtuna.processmining.wfnet.WorkflowNet

object AlphaAlgorithm {
    fun apply(eventLog: SimpleEventLog): WorkflowNet {
        val transitions = eventLog.flatten().distinct().map { Transition(it) }.associateBy { it.name }
        val firstTransitions = eventLog.mapNotNull { transitions[it.first()] }.toSet()
        val lastTransitions = eventLog.mapNotNull { transitions[it.last()] }.toSet()
        val (innerPlaces, innerArcs) = splitByCausality(eventLog)
            .fold(mutableSetOf<Place>() to mutableSetOf<Arc>()) { acc, (cause, effect) ->
                val (places, arcs) = acc
                Place(cause.joinToString(",") + " -> " + effect.joinToString(",")).apply {
                    places.add(this)
                    cause.mapNotNull { transitions[it] }.forEach { arcs.add(Arc(it, this)) }
                    effect.mapNotNull { transitions[it] }.forEach { arcs.add(Arc(this, it)) }
                }
                acc
            }
        val places = innerPlaces + setOf(start, end)
        val arcs = innerArcs +
                firstTransitions.map { Arc(start, it) }.toSet() +
                lastTransitions.map { Arc(it, end) }.toSet()
        return WorkflowNet(
            places = places,
            transitions = transitions.values.toSet(),
            arcs = arcs,
            start = start,
            end = end
        )
    }

    internal fun splitByCausality(eventLog: SimpleEventLog): List<Pair<Set<Event>, Set<Event>>> {
        val (causality, parallel) = calculateEventRelationships(eventLog)
        return splitByCausality(causality, parallel)
    }

    private fun splitByCausality(
        causality: Set<Pair<Event, Event>>,
        parallel: Set<Pair<Event, Event>>
    ): List<Pair<Set<Event>, Set<Event>>> = causality.asSequence()
        .filterNot { (first, second) -> (first to first) in parallel || (second to second) in parallel }
        .fold(mutableListOf<Pair<MutableSet<Event>, MutableSet<Event>>>()) { acc, (first, second) ->
            // todo - optimize
            acc.filter { (cause, effect) ->
                cause.all { isChoice(it, first, causality, parallel) && it to second in causality } &&
                        effect.all { isChoice(it, second, causality, parallel) && first to it in causality }
            }.ifEmpty {
                listOf(mutableSetOf<Event>() to mutableSetOf<Event>()).apply {
                    acc.addAll(this)
                }
            }.forEach { (cause, effect) ->
                cause.add(first)
                effect.add(second)
            }
            acc
        }

    private fun isChoice(
        event1: Event,
        event2: Event,
        causality: Set<Pair<Event, Event>>,
        parallel: Set<Pair<Event, Event>>
    ) = event1 to event2 !in parallel && event1 to event2 !in causality && event2 to event1 !in causality

    private fun calculateEventRelationships(
        eventLog: SimpleEventLog
    ): Pair<MutableSet<Pair<Event, Event>>, MutableSet<Pair<Event, Event>>> = eventLog.asSequence()
        .flatMap { it.windowed(2).asSequence() }
        .map { it[0] to it[1] }
        .fold(mutableSetOf<Pair<Event, Event>>() to mutableSetOf()) { acc, it ->
            fun noop() {}
            val (causality, parallel) = acc
            when {
                it in causality -> noop()
                it in parallel -> noop()
                it.first == it.second -> parallel.add(it)
                causality.remove(it.second to it.first) -> {
                    parallel.add(it.second to it.first)
                    parallel.add(it)
                }
                else -> causality.add(it)
            }
            acc
        }
}