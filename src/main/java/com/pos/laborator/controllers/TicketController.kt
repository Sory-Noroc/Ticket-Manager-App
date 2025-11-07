package com.pos.laborator.controllers

import com.pos.laborator.interfaces.DataObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.pos.laborator.model.Ticket
import com.pos.laborator.services.EventService
import com.pos.laborator.services.PacketService
import com.pos.laborator.services.TicketService
import com.pos.laborator.utils.buildHateoasCollection
import com.pos.laborator.utils.buildHateoasModel
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json

/**
 * OpenAPI Docs available after deployment at: http://localhost:8080/swagger-ui/index.html#/
 */

@RestController
@RequestMapping("/api/event-manager")
open class TicketController(
    private val ticketService: TicketService
) {

    companion object {
        val TICKET_EXAMPLE = Ticket(
            code = "Bilet0000",
            groupID = 0,
            eventID = 0
        )
    }

    @RequestMapping(value = ["/ticket"], method = [RequestMethod.POST])
    fun addTicket(@RequestBody ticket: Ticket): ResponseEntity<EntityModel<Ticket>> {
        val savedTicket = ticketService.addTicket(ticket)
        val json = buildHateoasModel(savedTicket) {
            val selfLink = linkTo(TicketController::class.java).slash(savedTicket.code).withSelfRel()
            addManualLink(selfLink)
            parent { methodOn(TicketController::class.java).getTickets() }
        }

        val locationUri = json.getLink("self").get().toUri()
        return ResponseEntity.created(locationUri).body(json)
    }

    @RequestMapping(value = ["/tickets/{cod}"], method = [RequestMethod.GET])
    fun getTicketByCode(@PathVariable cod: String): ResponseEntity<EntityModel<Ticket>> {
        return try {
            val ticket: Ticket = ticketService.getTicketByCode(cod)!!
            val json = buildHateoasModel(ticket) {
                addManualLink(linkTo(TicketController::class.java).slash("tickets/{cod}").withSelfRel())
                parent { methodOn(TicketController::class.java).getTickets() }
            }
            ResponseEntity.ok(json)
        } catch (_: NullPointerException) {
            val json = buildHateoasModel(TICKET_EXAMPLE) {
                addManualLink(linkTo(TicketController::class.java).slash("tickets/{cod}").withSelfRel())
                parent { methodOn(TicketController::class.java).getTickets() }
            }
            ResponseEntity(json, HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/events/{id}/tickets/{cod}"], method = [RequestMethod.GET])
    fun getTicketsByEvent(@PathVariable id: Int, @PathVariable cod: String): ResponseEntity<EntityModel<Ticket>> {
        var json = buildHateoasModel(TICKET_EXAMPLE) {
            addManualLink(linkTo(TicketController::class.java).slash("/events/{id}tickets/{cod}").withSelfRel())
            parent { methodOn(TicketController::class.java).getTickets() }
        }
        try {
            val ticket = ticketService.getTicketByEvent(id, cod)
            json = buildHateoasModel(ticket) {
                addManualLink(linkTo(TicketController::class.java).slash("/events/{id}tickets/{cod}").withSelfRel())
                parent { methodOn(EventController::class.java).getEvent(id) }
            }
            return ResponseEntity(json, HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            // No such event
            return ResponseEntity(json, HttpStatus.NOT_FOUND)
        } catch (e: IndexOutOfBoundsException) {
            return ResponseEntity(json, HttpStatus.BAD_REQUEST)
        }
    }

    @RequestMapping(value = ["/tickets"], method = [RequestMethod.GET])
    fun getTickets(): ResponseEntity<CollectionModel<EntityModel<DataObject>>> {
        val tickets = ticketService.getAllTickets()
        val json = buildHateoasCollection(tickets) {
            addManualLink(linkTo(TicketController::class.java).slash("/events/{id}tickets/{cod}").withSelfRel())
        }
        return ResponseEntity(json, HttpStatus.OK)
    }

    @RequestMapping(value = ["/tickets/{cod}"], method = [RequestMethod.PUT])
    fun updateTicketByCode(@PathVariable cod: String, ticket: Ticket): ResponseEntity<EntityModel<Ticket>> {
        ticket.code = cod
        val addedTicket = ticketService.updateTicket(ticket)
        return if (addedTicket != null) {
            val json = buildHateoasModel(ticket) {
                addManualLink(linkTo(TicketController::class.java).slash("/events/{id}tickets/{cod}").withSelfRel())
                parent { methodOn(TicketController::class.java).getTickets() }
            }
            ResponseEntity(json, HttpStatus.OK)
        } else {
            val json = buildHateoasModel(TICKET_EXAMPLE) {
                self { methodOn(TicketController::class.java).getTicketByCode(cod) }
                parent { methodOn(TicketController::class.java).getTickets() }
            }
            return ResponseEntity(json, HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/tickets/{cod}"], method = [RequestMethod.DELETE])
    fun deleteTicket(@PathVariable cod: String): ResponseEntity<EntityModel<Ticket>> {
        val ticket: Ticket? = ticketService.getTicketByCode(cod)
        val deleted = ticketService.deleteTicket(cod)
        val json = buildHateoasModel(ticket ?: TICKET_EXAMPLE) {
            addManualLink(linkTo(TicketController::class.java).slash("/events/{id}tickets/{cod}").withSelfRel())
            parent { methodOn(TicketController::class.java).getTickets() }
        }
        return if (deleted) {
            ResponseEntity(json, HttpStatus.OK)
        } else {
            ResponseEntity(json, HttpStatus.GONE)
        }
    }
}
