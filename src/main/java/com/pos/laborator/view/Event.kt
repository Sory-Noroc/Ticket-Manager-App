package com.pos.laborator.view

import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = "events")
data class Event(
    var ID: Int = 0,
    var ID_OWNER: Int,
    var name: String,  // Unique
    var location: String?,
    var description: String?,
    var seats: Int?
): Entity() {
    fun setOwnerId(ownerId: Int): Event {
        ID_OWNER = ownerId
        return this
    }

    fun setName(name: String): Event {
        this.name = name
        return this
    }

    fun setLocation(location: String?): Event {
        this.location = location
        return this
    }

    fun setDescription(description: String?): Event {
        this.description = description
        return this
    }

    fun setSeats(seats: Int?): Event {
        this.seats = seats
        return this
    }
}
