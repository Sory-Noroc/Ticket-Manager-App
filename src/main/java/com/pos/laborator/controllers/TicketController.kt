package com.pos.laborator.controllers

import com.pos.laborator.model.Database
import com.pos.laborator.utils.buildHateoasCollection
import com.pos.laborator.utils.buildHateoasModel
import com.pos.laborator.view.Entity
import com.pos.laborator.view.Event
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.pos.laborator.view.Packet
import com.pos.laborator.view.Ticket
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn

/**
 * OpenAPI Docs available after deployment at: http://localhost:8080/swagger-ui/index.html#/
 */

@RestController
@RequestMapping("/api/event-manager")
open class TicketController(private val database: Database) {

    companion object {
        val TICKET_EXAMPLE = Ticket(
            CODE = "Ticket1234",
            GroupID = 0,
            EventID = 0
        )
    }

    @RequestMapping(value = ["/tickets/{cod}"], method = [RequestMethod.GET])
    fun getTicketByCode(@PathVariable cod: String): ResponseEntity<Ticket> {
        try {
            val ticket = database.getTicket(cod)
            return ResponseEntity.ok(ticket)
        } catch (e: Exception) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/tickets/{cod}"], method = [RequestMethod.PUT])
    fun updateTicketByCode(@PathVariable cod: String, ticket: Ticket): ResponseEntity<Ticket> {
        val response = database.updateTicket(cod, ticket)
        return if (response) {
            ResponseEntity(HttpStatus.ACCEPTED)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/tickets/{cod}"], method = [RequestMethod.PATCH])
    fun patchTicket(@PathVariable cod: String, eventId: Int, groupId: Int): ResponseEntity<Ticket> {
        val response = database.patchTicket(cod, eventId, groupId)
        return if (response) {
            ResponseEntity(HttpStatus.ACCEPTED)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/tickets/{cod}"], method = [RequestMethod.DELETE])
    fun deleteTicketByCode(@PathVariable cod: String): ResponseEntity<Ticket> {
        val response = database.deleteTicket(cod)
        return if (response) {
            ResponseEntity(HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/tickets"], method = [RequestMethod.GET])
    fun getTickets(): ResponseEntity<List<Ticket>> {
        val tickets = database.tickets
        return ResponseEntity.ok(tickets)
    }

    @RequestMapping(value = ["/ticket"], method = [RequestMethod.POST])
    fun addTicket(@RequestBody ticket: Ticket): ResponseEntity<Ticket> {
        database.addTicket(ticket)
        return ResponseEntity(HttpStatus.OK)
    }
}
