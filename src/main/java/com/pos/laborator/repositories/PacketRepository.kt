package com.pos.laborator.repositories

import com.pos.laborator.model.Packet
import org.springframework.data.repository.CrudRepository

interface PacketRepository: CrudRepository<Packet, Int> {
    fun findByName(name: String): Packet?
    fun findAllByDescriptionContainingIgnoreCase(description: String): List<Packet>
}