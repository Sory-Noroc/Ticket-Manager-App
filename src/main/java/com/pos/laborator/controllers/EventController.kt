package com.pos.laborator.controllers

import com.pos.laborator.model.Database
import com.pos.laborator.view.Event
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.pos.laborator.view.Packet
import com.pos.laborator.view.Ticket
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder

/**
 * OpenAPI Docs available after deployment at: http://localhost:8080/swagger-ui/index.html#/
 */

@RestController
@RequestMapping("/api/event-manager")
open class EventController(private val database: Database) {

    @RequestMapping("/events/{id}", method = [RequestMethod.GET])
    fun getEvent(@PathVariable id: Int): ResponseEntity<EntityModel<Event>> {
        return try {
            val event = database.getEvent(id)
            val eventModel = EntityModel.of(
                event,
                WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(EventController::class.java).getEvent(id)
                ).withSelfRel(),
                WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(EventController::class.java).getEvents()
                ).withRel("events")
            )
            ResponseEntity.ok(eventModel)
        } catch (e: Exception) {
            println("Error: " + e.message)
            ResponseEntity.notFound().build()
        }
    }


    @RequestMapping(value = ["/events/{id}"], method = [RequestMethod.PUT])
    fun updateEvent(@PathVariable id: Int, @RequestBody event: Event): ResponseEntity<Unit> {
        try {
            database.getEvent(id)
            event.ID = id
            database.updateEvent(event)
            return ResponseEntity(HttpStatus.ACCEPTED)
        } catch (e: Exception) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/events/{id}"], method = [RequestMethod.DELETE])
    fun deleteEvent(@PathVariable id: Int): ResponseEntity<Unit> {
        try {
            database.deleteEvent(id)
            return ResponseEntity(HttpStatus.OK)
        } catch (e: Exception) {
            return ResponseEntity(Unit, HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/events/{id}"], method = [RequestMethod.PATCH])
    fun patchEvent(@PathVariable id: Int,
                   @RequestParam ownerId: Int,
                   @RequestParam name: String,
                   @RequestParam location: String,
                   @RequestParam description: String,
                   @RequestParam seats: Int
    ): ResponseEntity<Unit> {
            val response = database.patchEvent(id, ownerId, name, location, description, seats)
            if (response) {
                return ResponseEntity(HttpStatus.OK)
            } else {
                return ResponseEntity(HttpStatus.NOT_FOUND)
            }
    }

    ///////////////////
    @RequestMapping(value = ["/event-packets/{packetId}/events"], method = [RequestMethod.GET])
    fun getEventsByPacket(@PathVariable packetId: Int): ResponseEntity<List<Event>> {
        val response = database.getEventsByPacket(packetId)
        return if (response.isNotEmpty()) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    ////////////////
    @RequestMapping(value = ["/event-packets/{packetId}"], method = [RequestMethod.GET])
    fun getEventPacket(@PathVariable packetId: Int): ResponseEntity<Packet> {
        try {
            val response = database.getPacket(packetId)
            return ResponseEntity.ok(response)
        } catch (e: Exception) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/event-packets/{packetId}"], method = [RequestMethod.PUT])
    fun updateEventPacket(@PathVariable packetId: Int, @RequestBody packet: Packet): ResponseEntity<Unit> {
        try {
            database.getPacket(packetId)
            packet.ID = packetId
            database.updatePacket(packet)
            return ResponseEntity(HttpStatus.ACCEPTED)
        } catch (e: Exception) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/event-packets/{packetId}"], method = [RequestMethod.PATCH])
    fun patchEventPacket(@PathVariable packetId: Int,
                          @RequestParam ownerId: Int,
                          @RequestParam name: String,
                          @RequestParam location: String,
                          @RequestParam description: String,
                          ): ResponseEntity<Unit> {
        try {
            database.patchPacket(packetId, ownerId, name, location, description)
            return ResponseEntity(HttpStatus.OK)
        } catch (e: Exception) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/event-packets/{packetId}"], method = [RequestMethod.DELETE])
    fun deleteEventPacket(@PathVariable packetId: Int, @RequestBody packet: Packet): ResponseEntity<Unit> {
        val response = database.deletePacket(packetId)
        return if (response) {
            ResponseEntity(HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /////////////
    @RequestMapping(value = ["/events/{id}/event-packets"], method = [RequestMethod.GET])
    fun getEventPacketsByEventId(@PathVariable id: Int): ResponseEntity<List<Packet>> {
        val response = database.getPacketsByEventId(id)
        return if (response.isNotEmpty()) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    //////////////////////////////////
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
    fun updateTicketByCode(@PathVariable cod: String, ticket: Ticket): ResponseEntity<Unit> {
        val response = database.updateTicket(cod, ticket)
        return if (response) {
            ResponseEntity(HttpStatus.ACCEPTED)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/tickets/{cod}"], method = [RequestMethod.PATCH])
    fun patchTicket(@PathVariable cod: String, eventId: Int, groupId: Int): ResponseEntity<Unit> {
        val response = database.patchTicket(cod, eventId, groupId)
        return if (response) {
            ResponseEntity(HttpStatus.ACCEPTED)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/tickets/{cod}"], method = [RequestMethod.DELETE])
    fun deleteTicketByCode(@PathVariable cod: String): ResponseEntity<Unit> {
        val response = database.deleteTicket(cod)
        return if (response) {
            ResponseEntity(HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/events/{id}/tickets/{cod}"], method = [RequestMethod.GET])
    fun getTicketByEvent(@PathVariable id: Int, @PathVariable cod: String): ResponseEntity<Ticket> {
        try {
            val response = database.getTicketByEventId(id, cod)
            return ResponseEntity.ok(response)
        } catch (e: Exception) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/event-packets/{packetId}/tickets/{cod}\n"], method = [RequestMethod.GET])
    fun getTicketByPacket(@PathVariable packetId: Int, @PathVariable cod: String): ResponseEntity<Ticket> {
        try {
            val response = database.getTicketByPacketId(packetId, cod)
            return ResponseEntity.ok(response)
        } catch (e: Exception) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    //////////
    @RequestMapping(value = ["/events"], method = [RequestMethod.GET])
    fun getEvents(): ResponseEntity<List<Event>> {
        val events = database.getEvents()
        return ResponseEntity.ok(events)
    }

    @RequestMapping(value = ["/event"], method = [RequestMethod.POST])
    fun addEvent(@RequestBody event: Event): ResponseEntity<Unit> {
        database.addEvent(event)
        return ResponseEntity(HttpStatus.OK)
    }

    @RequestMapping(value = ["/event-packets"], method = [RequestMethod.GET])
    fun getPackets(): ResponseEntity<List<Packet>> {
        val packets = database.getPackets()
        return ResponseEntity.ok(packets)
    }

    @RequestMapping(value = ["/event-packet"], method = [RequestMethod.POST])
    fun addPacket(@RequestBody packet: Packet): ResponseEntity<Unit> {
        database.addPacket(packet)
        return ResponseEntity(HttpStatus.OK)
    }

    @RequestMapping(value = ["/tickets"], method = [RequestMethod.GET])
    fun getTickets(): ResponseEntity<List<Ticket>> {
        val tickets = database.getTickets()
        return ResponseEntity.ok(tickets)
    }

    @RequestMapping(value = ["/ticket"], method = [RequestMethod.POST])
    fun addTicket(@RequestBody ticket: Ticket): ResponseEntity<Unit> {
        database.addTicket(ticket)
        return ResponseEntity(HttpStatus.OK)
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = ["/events?location={location}"], method = [RequestMethod.GET])
    fun getEventsByLocation(@PathVariable location: String): ResponseEntity<List<Event>> {
        val response = database.getEventsByLocation(location)
        return ResponseEntity.ok(response)
    }

    @RequestMapping(value = ["/event-packets?page={page}&items_per_page={count}"],
        method = [RequestMethod.GET])
    fun getEventPackets(@PathVariable page: Int, @PathVariable count: Int = 3): ResponseEntity<List<Packet>> {
        try {
            val response = database.getEventPackets(page, count)
            return ResponseEntity.ok(response)
        } catch (e: Exception) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/events?name={subname}"], method = [RequestMethod.GET])
    fun getEventsByName(@PathVariable subname: String): ResponseEntity<List<Event>> {
        val response = database.getEventsBySubName(subname)
        return ResponseEntity.ok(response)
    }
}
