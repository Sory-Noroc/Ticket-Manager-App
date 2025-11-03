package com.pos.laborator.model

import com.pos.laborator.interfaces.DataObject
import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = "packets")
data class Packet(
    var ID: Int? = null,
    var ownerId: Int,
    var name: String,
    var location: String?,
    var description: String?
): DataObject {
    fun setOwnerId(ownerId: Int): Packet {
        this.ownerId = ownerId
        return this
    }

    fun setName(name: String): Packet {
        this.name = name
        return this
    }

    fun setLocation(location: String?): Packet {
        this.location = location
        return this
    }

    fun setDescription(description: String?): Packet {
        this.description = description
        return this
    }
}