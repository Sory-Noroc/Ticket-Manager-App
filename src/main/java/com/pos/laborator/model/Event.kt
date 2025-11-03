package com.pos.laborator.model

import com.pos.laborator.interfaces.DataObject
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.hateoas.server.core.Relation

@Entity
@Table(name = "events")
@Relation(collectionRelation = "events")
data class Event(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var ID: Int? = null,
    var ownerId: Int,
    var name: String,
    var location: String?,
    var description: String?,
    var seats: Int?
): DataObject {
    fun setOwnerId(ownerId: Int): Event {
        this.ownerId = ownerId
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