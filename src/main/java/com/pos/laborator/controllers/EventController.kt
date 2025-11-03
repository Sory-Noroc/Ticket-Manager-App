package com.pos.laborator.controllers

import com.pos.laborator.services.EventService
import com.pos.laborator.utils.buildHateoasCollection
import com.pos.laborator.utils.buildHateoasModel
import com.pos.laborator.model.Event
import com.pos.laborator.interfaces.DataObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn

/**
 * OpenAPI Docs available after deployment at: http://localhost:8080/swagger-ui/index.html#/
 */
@RestController
@RequestMapping("/api/event-manager")
open class EventController(private val eventService: EventService) {

    companion object {
        val EVENT_EXAMPLE = Event(ID = 0, ownerId = 0, name = "Event Example", location = "Location Example", description = "Description Example", seats = 100)
    }

    @RequestMapping(value = ["/event"], method = [RequestMethod.POST])
    fun addEvent(@RequestBody event: Event): ResponseEntity<EntityModel<Event>> {
        eventService.addEvent(event)
        val json = buildHateoasModel(event) {
            addManualLink(linkTo(EventController::class.java).slash("/event").withSelfRel())
            parent { methodOn(EventController::class.java).getEvents(null, null, null) }
        }
        return ResponseEntity(json, HttpStatus.OK)
    }

    @RequestMapping("/events/{id}", method = [RequestMethod.GET])
    fun getEvent(@PathVariable id: Int): ResponseEntity<EntityModel<Event>> {
        return try {
            val event = eventService.getEvent(id).get()
            val eventModel = buildHateoasModel(event) {
                addManualLink(linkTo(EventController::class.java).slash(id).withSelfRel())
                parent { methodOn(EventController::class.java).getEvents(null, null, null) }
            }
            ResponseEntity(eventModel, HttpStatus.OK)
        } catch (e: Exception) {
            val eventModel = buildHateoasModel(EVENT_EXAMPLE) {
                addManualLink(linkTo(EventController::class.java).slash(id).withSelfRel())
                parent { methodOn(EventController::class.java).getEvents(null, null, null) }
            }
            ResponseEntity(eventModel, HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Performs a search by substring of name, but if name is not provided, it will perform
     * the search of events by location substring.
     */
    @RequestMapping("/events/", method = [RequestMethod.GET])
    fun getEventByNameOrLocation(@RequestParam(required = false, defaultValue = "") name: String, @RequestParam(required = false, defaultValue = "") location: String): ResponseEntity<CollectionModel<EntityModel<DataObject>>> {
        return try {
            val events = eventService.getEventsByNameAndLocation(name, location)
            val eventModel = buildHateoasCollection(events) {
                addManualLink(linkTo(EventController::class.java).slash("").withSelfRel())
                parent { methodOn(EventController::class.java).getEvents(null, null, null) }
            }
            ResponseEntity(eventModel, HttpStatus.OK)
        } catch (e: Exception) {
            val eventModel = buildHateoasCollection(mutableListOf()) {
                addManualLink(linkTo(EventController::class.java).slash(location).withSelfRel())
                parent { methodOn(EventController::class.java).getEvents(null, null, null) }
            }
            ResponseEntity(eventModel, HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/events/{id}"], method = [RequestMethod.PUT])
    fun updateEvent(@PathVariable id: Int, @RequestBody event: Event): ResponseEntity<EntityModel<Event>> {
        try {
            event.ID = id
            eventService.updateEvent(event)
            val json = buildHateoasModel(event) {
                self { methodOn(EventController::class.java).getEvent(id) }
                parent { methodOn(EventController::class.java).getEvents(null, null, null) }
            }
            return ResponseEntity.ok(json)
        } catch (e: Exception) {
            val json = buildHateoasModel(EVENT_EXAMPLE) {
                self { methodOn(EventController::class.java).updateEvent(id, event) }
                parent { methodOn(EventController::class.java).getEvents(null, null, null) }
            }
            return ResponseEntity(json, HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/events/{id}"], method = [RequestMethod.DELETE])
    fun deleteEvent(@PathVariable id: Int): ResponseEntity<EntityModel<Event>> {
        try {
            val event = eventService.getEvent(id).get()
            eventService.deleteEvent(id)
            val json = buildHateoasModel(event) {
                self { methodOn(EventController::class.java).getEvent(id) }
                parent { methodOn(EventController::class.java).getEvents(null, null, null) }
            }
            return ResponseEntity(json, HttpStatus.OK)
        } catch (e: Exception) {
            val json = buildHateoasModel(EVENT_EXAMPLE) {
                self { methodOn(EventController::class.java).deleteEvent(id) }
                parent { methodOn(EventController::class.java).getEvents(null, null, null) }
            }
            return ResponseEntity( json, HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/events"], method = [RequestMethod.GET])
    fun getEvents(@RequestParam(required=false) location: String?,
                            @RequestParam(required=false) name: String?,
                            @RequestParam(required=false) description: String?
    ): ResponseEntity<CollectionModel<EntityModel<DataObject>>> {
        val events: List<Event> = eventService.getEventsByParameters(location, name, description)
        val json = buildHateoasCollection(events) {
            addManualLink(linkTo(EventController::class.java).withSelfRel())
            parent { methodOn(EventController::class.java).getEvents(null, null, null) }
        }
        return ResponseEntity(json, HttpStatus.OK)
    }
}
