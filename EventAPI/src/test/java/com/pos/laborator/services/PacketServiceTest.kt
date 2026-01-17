package com.pos.laborator.services

import com.pos.laborator.model.Packet
import com.pos.laborator.repositories.PacketRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class PacketServiceTest {

    @Mock
    lateinit var packetRepo: PacketRepository

    @Mock
    lateinit var ticketService: TicketService

    @Mock
    lateinit var eventService: EventService

    @InjectMocks
    lateinit var service: PacketService

    @Test
    fun `getPacket returns packet`() {
        val packet = Packet(id = 1, ownerId = 1, name = "P", location = "L", description = "D")
        `when`(packetRepo.findById(1)).thenReturn(Optional.of(packet))

        val result = service.getPacket(1)
        assertEquals(packet, result)
    }

    @Test
    fun `getEventPackets filters by type`() {
        val p1 = Packet(id = 1, ownerId = 1, name = "P1", location = "L", description = "VIP Access")
        val p2 = Packet(id = 2, ownerId = 1, name = "P2", location = "L", description = "General")
        
        `when`(packetRepo.findAll()).thenReturn(listOf(p1, p2))
        // We need to mock ticketService and eventService because getEventPackets calls them even if we filter by type only
        `when`(ticketService.getAllTickets()).thenReturn(emptyList())
        `when`(eventService.getEventsByParameters(null, null, null)).thenReturn(emptyList())

        val result = service.getEventPackets("VIP", null)
        assertEquals(1, result.size)
        assertEquals("VIP Access", result[0].description)
    }
}
