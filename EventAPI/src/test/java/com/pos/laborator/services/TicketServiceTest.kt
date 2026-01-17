package com.pos.laborator.services

import com.pos.laborator.model.Event
import com.pos.laborator.model.Ticket
import com.pos.laborator.repositories.TicketRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.any

@ExtendWith(MockitoExtension::class)
class TicketServiceTest {

    @Mock
    lateinit var ticketRepo: TicketRepository

    @Mock
    lateinit var eventService: EventService

    @InjectMocks
    lateinit var service: TicketService

    @Test
    fun `addTicket generates code if missing and saves`() {
        val ticket = Ticket(groupID = 1, eventID = 1)
        `when`(ticketRepo.getTicketByCode(anyString())).thenReturn(Optional.empty())
        `when`(ticketRepo.save(any(Ticket::class.java))).thenAnswer { it.arguments[0] }

        val result = service.addTicket(ticket)
        
        assertNotNull(result.code)
        verify(ticketRepo).save(ticket)
    }

    @Test
    fun `getTicketByCode returns ticket`() {
        val ticket = Ticket(code = "CODE", groupID = 1, eventID = 1)
        `when`(ticketRepo.getTicketByCode("CODE")).thenReturn(Optional.of(ticket))

        val result = service.getTicketByCode("CODE")
        assertTrue(result.isPresent)
        assertEquals("CODE", result.get().code)
    }
    
    @Test
    fun `getTicketByEvent finds correct ticket`() {
        val event = Event(id = 1, ownerId = 1, name = "E", location = "L", description = "D", seats = 10)
        val ticket = Ticket(code = "CODE", groupID = 1, eventID = 1)
        
        `when`(eventService.getEvent(1)).thenReturn(event)
        `when`(ticketRepo.getTicketsByEventID(1)).thenReturn(listOf(ticket))
        
        val result = service.getTicketByEvent(1, "CODE")
        assertEquals(ticket, result)
    }
}
