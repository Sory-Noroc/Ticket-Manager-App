package com.pos.laborator.view

import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = "packets")
data class Packet(
    var ID: Int = 0,
    var ID_OWNER: Int,
    var name: String,
    var location: String?,
    var description: String?
): Entity() {
    fun setOwnerId(ownerId: Int): Packet {
        ID_OWNER = ownerId
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
