package com.pos.laborator.model

import com.pos.laborator.repositories.PacketRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
open class PacketRepositoryTest {

    @Autowired
    private lateinit var packetRepository: PacketRepository

    private lateinit var packet1: Packet
    private lateinit var packet2: Packet

    @BeforeEach
    fun setUp() {
        packet1 = Packet(
            ownerId = 1,
            name = "VIP Packet",
            location = "Main Stage",
            description = "Access to VIP area"
        )
        packet2 = Packet(
            ownerId = 2,
            name = "Standard Packet",
            location = "General Area",
            description = "General access"
        )

        packet1 = packetRepository.save(packet1)
        packet2 = packetRepository.save(packet2)
    }

    @Test
    fun `test findByName`() {
        val foundPacket = packetRepository.findByName("VIP Packet")
        assertNotNull(foundPacket)
        assertEquals("Main Stage", foundPacket!!.location)
    }

    @Test
    fun `test findAllByDescriptionContainingIgnoreCase`() {
        val packets = packetRepository.findAllByDescriptionContainingIgnoreCase("access")
        assertEquals(2, packets.size) // Both descriptions contain "access" ("Access to VIP area", "General access")
    }

    @Test
    fun `test CRUD operations`() {
        // Read
        val found = packetRepository.findById(packet1.id!!).orElse(null)
        assertNotNull(found)

        // Update
        found!!.name = "Updated Packet"
        packetRepository.save(found)
        val updated = packetRepository.findById(packet1.id!!).get()
        assertEquals("Updated Packet", updated.name)

        // Delete
        packetRepository.deleteById(packet1.id!!)
        val deleted = packetRepository.findById(packet1.id!!).orElse(null)
        assertNull(deleted)
    }
}
