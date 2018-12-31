package net.harshtuna.processmining.alpha

object LogFootprintCalculator {
    fun footprint(eventLog: SimpleEventLog): SimpleFootprint {
        val directSuccession = eventLog.asSequence()
            .flatMap { it.windowed(2).asSequence() }
            .groupBy({ it[0] }, { it[1] })
            .mapValues { it.value.toSet() }

        val events = eventLog.flatten().distinct()
        return events.map { first ->
            first to events.map { second ->
                val firstThenSecond = directSuccession[first]?.contains(second) ?: false
                val secondThenFirst = directSuccession[second]?.contains(first) ?: false
                second to when {
                    firstThenSecond && secondThenFirst -> EventRelation.PARALLEL
                    firstThenSecond && !secondThenFirst -> EventRelation.CAUSALITY
                    !firstThenSecond && secondThenFirst -> EventRelation.REVERSE
                    else -> EventRelation.CHOICE
                }
            }.toMap()
        }.toMap()
    }
}