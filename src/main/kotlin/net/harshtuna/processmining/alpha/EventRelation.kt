package net.harshtuna.processmining.alpha

enum class EventRelation(val symbol: String) {
    //    DIRECT_SUCCESSION(">"),
    CAUSALITY("->"),
    REVERSE("<-"),
    PARALLEL("||"),
    CHOICE("#")
}