package com.pos.laborator.repositories

import com.pos.laborator.model.Ticket
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository: CrudRepository<Ticket, String> {

    fun getTicketByCode(code: String): Ticket?
    fun getTicketsByEventID(eventId: Int): List<Ticket>
    fun getTicketsByGroupID(groupId: Int): List<Ticket>
}