package net.harshtuna.processmining.wfnet

data class WorkflowNet(
    val places: Set<Place>,
    val transitions: Set<Transition>,
    val arcs: Set<Arc>,
    val start: Place,
    val end: Place
)

data class Place(
    val name: String
)

data class Transition(
    val name: String
)

data class Arc(
    val place: Place,
    val transition: Transition,
    val type: ArcType
) {
    constructor(place: Place, transition: Transition) : this(place, transition, ArcType.PT)
    constructor(transition: Transition, place: Place) : this(place, transition, ArcType.TP)
}

enum class ArcType { PT, TP }

object Constants {
    val start = Place("start")
    val end = Place("end")
}