package net.harshtuna.processmining.alpha

typealias SimpleEventLog = Set<Trace>

typealias Trace = List<Event>

typealias Event = String

typealias SimpleFootprint = Map<Event, Map<Event, EventRelation>>