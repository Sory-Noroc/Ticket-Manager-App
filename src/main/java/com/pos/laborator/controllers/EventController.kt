package com.pos.laborator.controllers

import com.pos.laborator.controllers.TicketController.Companion.TICKET_EXAMPLE
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
open class EventController(private val database: Database) {

    companion object {
        val EVENT_EXAMPLE = Event(ID = 0, ID_OWNER = 0, name = "Event Example", location = "Location Example", description = "Description Example", seats = 100)
    }

    @RequestMapping("/events/{id}", method = [RequestMethod.GET])
    fun getEvent(@PathVariable id: Int): ResponseEntity<EntityModel<Event>> {
        return try {
            val event = database.getEvent(id)
            val eventModel = buildHateoasModel(event) {
                self { methodOn(EventController::class.java).getEvent(id) }
                parent { methodOn(EventController::class.java).getEvents() }
            }
            ResponseEntity(eventModel, HttpStatus.OK)
        } catch (e: Exception) {
            val eventModel = buildHateoasModel(EVENT_EXAMPLE) {
                self { methodOn(EventController::class.java).getEvent(id) }
                parent { methodOn(EventController::class.java).getEvents() }
            }
            ResponseEntity(eventModel, HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/events/{id}"], method = [RequestMethod.PUT])
    fun updateEvent(@PathVariable id: Int, @RequestBody event: Event): ResponseEntity<EntityModel<Event>> {
        try {
            event.ID = id
            database.updateEvent(event)
            val json = buildHateoasModel(event) {
                self { methodOn(EventController::class.java).getEvent(id) }
                parent { methodOn(EventController::class.java).getEvents() }
            }
            return ResponseEntity.ok(json)
        } catch (e: Exception) {
            val json = buildHateoasModel(EVENT_EXAMPLE) {
                self { methodOn(EventController::class.java).updateEvent(id, event) }
                parent { methodOn(EventController::class.java).getEvents() }
            }
            return ResponseEntity(json, HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/events/{id}"], method = [RequestMethod.DELETE])
    fun deleteEvent(@PathVariable id: Int): ResponseEntity<EntityModel<Event>> {
        try {
            val event = database.getEvent(id)
            database.deleteEvent(id)
            val json = buildHateoasModel(event) {
                self { methodOn(EventController::class.java).getEvent(id) }
                parent { methodOn(EventController::class.java).getEvents() }
            }
            return ResponseEntity(json, HttpStatus.OK)
        } catch (e: Exception) {
            val json = buildHateoasModel(EVENT_EXAMPLE) {
                self { methodOn(EventController::class.java).deleteEvent(id) }
                parent { methodOn(EventController::class.java).getEvents() }
            }
            return ResponseEntity( json, HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/events/{id}"], method = [RequestMethod.PATCH])
    fun patchEvent(@PathVariable id: Int,
                   @RequestParam(required = false) ownerId: Int?,
                   @RequestParam(required = false) name: String?,
                   @RequestParam(required = false) location: String?,
                   @RequestParam(required = false) description: String?,
                   @RequestParam(required = false) seats: Int?
    ): ResponseEntity<EntityModel<Event>> {
        val changed = database.patchEvent(id, ownerId, name, location, description, seats)
        if (changed) {
            val confirmation = database.getEvent(id)
            val json = buildHateoasModel(confirmation) {
                self { methodOn(EventController::class.java).getEvent(id) }
                parent { methodOn(EventController::class.java).getEvents() }
            }
            return ResponseEntity(json, HttpStatus.OK)
        } else {
            val json = buildHateoasModel(EVENT_EXAMPLE) {
                self { methodOn(EventController::class.java).patchEvent(id, ownerId, name, location, description, seats) }
                parent { methodOn(EventController::class.java).getEvents() }
            }
            return ResponseEntity(json, HttpStatus.NOT_FOUND)

        }
    }

    @RequestMapping(value = ["/events/{id}/event-packets"], method = [RequestMethod.GET])
    fun getEventPacketsByEventId(@PathVariable id: Int): ResponseEntity<CollectionModel<EntityModel<Entity>>> {
        val response = database.getPacketsByEventId(id)
        return if (response.isNotEmpty()) {
            val json = buildHateoasCollection(response) {
                self { methodOn(EventController::class.java).getEventPacketsByEventId(id) }
                parent { methodOn(EventController::class.java).getEvent(id) }
            }
            ResponseEntity(json, HttpStatus.OK)
        } else {
            val json = buildHateoasCollection(response) {
                self { methodOn(EventController::class.java).getEventPacketsByEventId(id) }
                parent { methodOn(EventController::class.java).getEvents() }
            }
            ResponseEntity(json, HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/events/{id}/tickets/{cod}"], method = [RequestMethod.GET])
    fun getTicketByEvent(@PathVariable id: Int, @PathVariable cod: String): ResponseEntity<EntityModel<Ticket>> {
        try {
            val response = database.getTicketByEventId(id, cod)
            val json = buildHateoasModel(response) {
                self { methodOn(EventController::class.java).getTicketByEvent(id, cod) }
                parent { methodOn(EventController::class.java).getEvent(id) }
            }
            return ResponseEntity(json, HttpStatus.OK)
        } catch (e: Exception) {
            val json = buildHateoasModel(TICKET_EXAMPLE) {
                self { methodOn(EventController::class.java).getEvent(id) }
                parent { methodOn(TicketController::class.java).getTickets() }
            }
            return ResponseEntity(json, HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/event"], method = [RequestMethod.POST])
    fun addEvent(@RequestBody event: Event): ResponseEntity<EntityModel<Event>> {
        database.addEvent(event)
        val json = buildHateoasModel(event) {
            self { methodOn(EventController::class.java).getEvent(event.ID) }
            parent { methodOn(EventController::class.java).getEvents() }
        }
        return ResponseEntity(json, HttpStatus.OK)
    }

    @RequestMapping(value = ["/events"], method = [RequestMethod.GET])
    fun getEventsByParameters(@RequestParam(required=false) location: String?,
                            @RequestParam(required=false) subname: String?,
                            @RequestParam(required=false) subdescription: String?
    ): ResponseEntity<CollectionModel<EntityModel<Entity>>> {
        val events: List<Event> = database.getEventsByParameters(location, subname, subdescription)
        val json = buildHateoasCollection(events) {
            self { methodOn(EventController::class.java).getEventsByParameters(location, subname, subdescription) }
            parent { methodOn(EventController::class.java).getEvents() }
        }
        return ResponseEntity(json, HttpStatus.OK)
    }

    fun getEvents() = getEventsByParameters(null, null, null)
}
