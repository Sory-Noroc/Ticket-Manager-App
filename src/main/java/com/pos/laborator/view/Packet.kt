package com.pos.laborator.view

data class Packet(
    var ID: Int = 0,
    private var ID_OWNER: Int,
    private var name: String,
    private var location: String?,
    private var description: String?
) {
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
