package com.pos.laborator.services

import com.pos.laborator.model.Event
import com.pos.laborator.model.Ticket
import com.pos.laborator.repositories.TicketRepository
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.stereotype.Service

@Service
class TicketService(
    private val ticketRepo: TicketRepository,
    private val eventService: EventService
) {

    fun createNewTicket(ticket: Ticket): Ticket {
        if (ticket.code != null) {
            return ticket
        }
        var newCode: String
        var isCodeUnique = false

        do {
            newCode = Ticket.generateTicketCodeBase64()
            // Check if this code already exists in the repository
            val existingTicket = ticketRepo.getTicketByCode(newCode)

            if (existingTicket == null) {
                isCodeUnique = true
            }
        } while (!isCodeUnique)

        ticket.code = newCode
        return ticketRepo.save(ticket)
    }

    fun addTicket(ticket: Ticket) = ticketRepo.save(createNewTicket(ticket))

    fun updateTicket(ticket: Ticket): Ticket = ticketRepo.save(ticket)

    fun getTicketByCode(code: String) = ticketRepo.getTicketByCode(code)

    fun getTicketByEvent(eventId: Int, code: String): Ticket {
        val event = eventService.getEvent(eventId)
        val tickets = ticketRepo.getTicketsByEventID(event.id!!)
        return tickets.filter { it.code == code }[0]
    }

    fun getTicketsByPacket(packetId: Int): List<Ticket> = ticketRepo.getTicketsByGroupID(packetId)
    /**
     * Return: If deleted: true; else false(not existent)
     */
    fun deleteTicket(code: String): Boolean {
        val ticket = ticketRepo.getTicketByCode(code)
        if (ticket != null) {
            ticketRepo.delete(ticket)
            return true
        }
        return false
    }

    fun getAllTickets(): List<Ticket> = ticketRepo.findAll().toList()

    fun getEventsByPacket(packetId: Int): List<Event> {
        val eventIds = getAllTickets().filter { it.groupID == packetId }. map { it.eventID }
        return eventService.getEventsByIds(eventIds)
    }
}