package com.pos.laborator.controllers

import com.pos.laborator.interfaces.DataObject
import com.pos.laborator.model.Ticket
import com.pos.laborator.services.TicketService
import com.pos.laborator.utils.buildHateoasCollection
import com.pos.laborator.utils.buildHateoasModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.jvm.optionals.getOrNull

/**
 * OpenAPI Docs available after deployment at: http://localhost:8080/swagger-ui/index.html#/
 */

@RestController
@RequestMapping("/api/event-manager")
open class TicketController(
    private var ticketService: TicketService
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

    @GetMapping(value = ["/tickets/{code}"])
    fun getTicketByCode(@PathVariable code: String): ResponseEntity<EntityModel<Ticket>> {
        return try {
            val ticket: Ticket = ticketService.getTicketByCode(code).get()
            val json = buildHateoasModel(ticket) {
                addManualLink(linkTo(TicketController::class.java).slash("tickets/{cod}").withSelfRel())
                parent { methodOn(TicketController::class.java).getTickets() }
            }
            ResponseEntity.ok(json)
        } catch (_: NoSuchElementException) {
            val json = buildHateoasModel(TICKET_EXAMPLE) {
                addManualLink(linkTo(TicketController::class.java).slash("tickets/{cod}").withSelfRel())
                parent { methodOn(TicketController::class.java).getTickets() }
            }
            ResponseEntity(json, HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping(value = ["/events/{id}/tickets/{code}"])
    fun getTicketsByEvent(@PathVariable id: Int, @PathVariable code: String): ResponseEntity<EntityModel<Ticket>> {
        var json = buildHateoasModel(TICKET_EXAMPLE) {
            addManualLink(linkTo(TicketController::class.java).slash("/events/{id}tickets/{cod}").withSelfRel())
            parent { methodOn(TicketController::class.java).getTickets() }
        }
        try {
            val ticket = ticketService.getTicketByEvent(id, code)
            json = buildHateoasModel(ticket) {
                addManualLink(linkTo(TicketController::class.java).slash("/events/{id}tickets/{cod}").withSelfRel())
                parent { methodOn(EventController::class.java).getEvent(id) }
            }
            return ResponseEntity(json, HttpStatus.OK)
        } catch (_: NoSuchElementException) {
            // No such event
            return ResponseEntity(json, HttpStatus.NOT_FOUND)
        } catch (_: IndexOutOfBoundsException) {
            return ResponseEntity(json, HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping(value = ["/event-packets/{id}/tickets/{code}"])
    fun getTicketsByPacket(@PathVariable id: Int, @PathVariable code: String): ResponseEntity<EntityModel<Ticket>> {
        var json = buildHateoasModel(TICKET_EXAMPLE) {
            addManualLink(linkTo(TicketController::class.java).slash("event-packets").slash(id).slash("tickets").slash(code).withSelfRel())
            parent { methodOn(TicketController::class.java).getTickets() }
        }
        try {
            val ticket: Ticket = ticketService.getTicketsByPacket(id).find { it.code == code }!!

            json = buildHateoasModel(ticket) {
                addManualLink(linkTo(TicketController::class.java).slash("event-packets").slash(id).slash("tickets").slash(code).withSelfRel())
                parent { methodOn(EventController::class.java).getEvent(id) }
            }
            return ResponseEntity(json, HttpStatus.OK)
        } catch (_: IndexOutOfBoundsException) {
            return ResponseEntity(json, HttpStatus.BAD_REQUEST)
        } catch (_: Exception) {
            // Nu avem asa event
            return ResponseEntity(json, HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping(value = ["/tickets"])
    fun getTickets(): ResponseEntity<CollectionModel<EntityModel<DataObject>>> {
        val tickets = ticketService.getAllTickets()
        val json = buildHateoasCollection(tickets) {
            addManualLink(linkTo(TicketController::class.java).slash("/events/{id}tickets/{cod}").withSelfRel())
        }
        return ResponseEntity(json, HttpStatus.OK)
    }

    @PutMapping(value = ["/tickets/{code}"])
    fun updateTicketByCode(@PathVariable code: String, @RequestBody ticket: Ticket): ResponseEntity<EntityModel<Ticket>> {
        val existingTicket = ticketService.getTicketByCode(code)
        return if (existingTicket.isPresent) {
            val addedTicket = ticketService.updateTicket(ticket).get()
            val json = buildHateoasModel(addedTicket) {
                addManualLink(linkTo(TicketController::class.java).slash("/events/{id}tickets/{cod}").withSelfRel())
                parent { methodOn(TicketController::class.java).getTickets() }
            }
            ResponseEntity(json, HttpStatus.OK)
        } else {
            val json = buildHateoasModel(TICKET_EXAMPLE) {
                self { methodOn(TicketController::class.java).getTicketByCode(code) }
                parent { methodOn(TicketController::class.java).getTickets() }
            }
            return ResponseEntity(json, HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping(value = ["/tickets/{code}"])
    fun deleteTicket(@PathVariable code: String): ResponseEntity<EntityModel<Ticket>> {
        val ticket: Optional<Ticket> = ticketService.getTicketByCode(code)
        val deleted = ticketService.deleteTicket(code)
        val json = buildHateoasModel(ticket.getOrNull() ?: TICKET_EXAMPLE) {
            addManualLink(linkTo(TicketController::class.java).slash("/events/{id}tickets/{cod}").withSelfRel())
            parent { methodOn(TicketController::class.java).getTickets() }
        }
        return if (deleted) {
            ResponseEntity(json, HttpStatus.OK)
        } else {
            ResponseEntity(json, HttpStatus.NOT_FOUND)
        }
    }
}
