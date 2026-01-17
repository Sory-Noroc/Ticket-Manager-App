package com.pos.laborator.model

import com.pos.laborator.repositories.TicketRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@Transactional
open class TicketRepositoryTest {

    @Autowired
    private lateinit var ticketRepository: TicketRepository

    private lateinit var ticket1: Ticket
    private lateinit var ticket2: Ticket

    @BeforeEach
    fun setUp() {
        ticket1 = Ticket(code = "TICKET_1", eventID = 1, groupID = 100)
        ticket2 = Ticket(code = "TICKET_2", eventID = 1, groupID = 200)

        ticketRepository.save(ticket1)
        ticketRepository.save(ticket2)
    }

    @Test
    fun `test getTicketByCode`() {
        val foundTicket = ticketRepository.getTicketByCode("TICKET_1")
        assertTrue(foundTicket.isPresent)
        assertEquals(100, foundTicket.get().groupID)
    }

    @Test
    fun `test getTicketsByEventID`() {
        val tickets = ticketRepository.getTicketsByEventID(1)
        assertEquals(2, tickets.size)
    }

    @Test
    fun `test getTicketsByGroupID`() {
        val tickets = ticketRepository.getTicketsByGroupID(100)
        assertEquals(1, tickets.size)
        assertEquals("TICKET_1", tickets[0].code)
    }
    
    @Test
    fun `test deleteTicket`() {
        ticketRepository.delete(ticket1)
        val foundTicket = ticketRepository.getTicketByCode("TICKET_1")
        assertFalse(foundTicket.isPresent)
    }
}
