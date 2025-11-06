package com.pos.laborator.services

import com.pos.laborator.model.Ticket
import com.pos.laborator.repositories.TicketRepository
import org.springframework.stereotype.Service

@Service
class TicketService(private val ticketRepo: TicketRepository) {

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

    fun getTicketByCode(code: String) = ticketRepo.getTicketByCode(code)

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
}