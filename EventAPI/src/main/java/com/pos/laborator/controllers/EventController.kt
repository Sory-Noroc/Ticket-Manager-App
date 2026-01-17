package com.pos.laborator.controllers

import com.pos.laborator.services.EventService
import com.pos.laborator.utils.buildHateoasCollection
import com.pos.laborator.utils.buildHateoasModel
import com.pos.laborator.model.Event
import com.pos.laborator.interfaces.DataObject
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.orm.ObjectOptimisticLockingFailureException
import java.util.NoSuchElementException

/**
 * OpenAPI Docs available after deployment at: http://localhost:8080/swagger-ui/index.html#/
 */
@RestController
@RequestMapping("/api/event-manager")
class EventController(private val eventService: EventService) {

    companion object {
        val EVENT_EXAMPLE = Event(id = 0, ownerId = 0, name = "Event Example", location = "Location Example", description = "Description Example", seats = 100)
    }

    /**
     * Creeaza un eveniment nou.
     * Returneaza 201 Created cu un header Location.
     */
    @PostMapping("/events")
    fun addEvent(@RequestBody @Valid event: Event): ResponseEntity<EntityModel<Event>> {
        val savedEvent = eventService.addEvent(event)

        val json = buildHateoasModel(savedEvent) {
            val selfLink = linkTo(EventController::class.java).slash("event").slash(savedEvent.id).withSelfRel()
            addManualLink(selfLink)
            parent { methodOn(EventController::class.java).getEvents(null, null, null) }
        }

        val locationUri = json.getLink("self").get().toUri()

        // Returnam 201 Created
        return ResponseEntity.created(locationUri).body(json)
    }

    /**
     * Obtine un eveniment specific dupa ID.
     */
    @GetMapping("/events/{id}")
    fun getEvent(@PathVariable id: Int): ResponseEntity<EntityModel<Event>> {
        return try {
            val event = eventService.getEvent(id)
            val eventModel = buildHateoasModel(event) {
                addManualLink(linkTo(EventController::class.java).slash("events").slash(id).withSelfRel())
                parent { methodOn(EventController::class.java).getEvents(null, null, null) }
            }
            ResponseEntity(eventModel, HttpStatus.OK)
        } catch (_: NoSuchElementException) { // Prindem exceptia specifica
            val eventModel = buildHateoasModel(EVENT_EXAMPLE) {
                addManualLink(linkTo(EventController::class.java).slash("events").slash(id).withSelfRel())
                parent { methodOn(EventController::class.java).getEvents(null, null, null) }
            }
            ResponseEntity(eventModel, HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Actualizeaza un eveniment existent.
     */
    @PutMapping("/events/{id}")
    fun updateEvent(@PathVariable id: Int, @RequestBody event: Event): ResponseEntity<EntityModel<Event>> {
        try {
            event.id = id
            eventService.updateEvent(event) // Poate arunca NoSuchElementException daca nu gaseste
            val json = buildHateoasModel(event) {
                self { methodOn(EventController::class.java).getEvent(id) }
                parent { methodOn(EventController::class.java).getEvents(null, null, null) }
            }
            return ResponseEntity.ok(json)
        } catch (_: ObjectOptimisticLockingFailureException) {
            val json = buildHateoasModel(EVENT_EXAMPLE) {
                addManualLink(linkTo(EventController::class.java).slash("events").slash(id).withSelfRel())
                parent { methodOn(EventController::class.java).getEvents(null, null, null) }
            }
            return ResponseEntity(json, HttpStatus.NOT_ACCEPTABLE)
        }
    }

    /**
     * Sterge un eveniment existent.
     */
    @DeleteMapping("/events/{id}")
    fun deleteEvent(@PathVariable id: Int): ResponseEntity<EntityModel<Event>> {
        try {
            val event = eventService.getEvent(id)
            eventService.deleteEvent(id)
            val json = buildHateoasModel(event) {
                self { methodOn(EventController::class.java).getEvent(id) }
                parent { methodOn(EventController::class.java).getEvents(null, null, null) }
            }
            return ResponseEntity(json, HttpStatus.OK)
        } catch (_: NoSuchElementException) {
            val json = buildHateoasModel(EVENT_EXAMPLE) {
                addManualLink(linkTo(EventController::class.java).slash("events").slash(id).withSelfRel())
                parent { methodOn(EventController::class.java).getEvents(null, null, null) }
            }
            return ResponseEntity( json, HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Obtine o lista de evenimente filtrate dupa parametri.
     * Daca nu se gasesc, returneaza 200 OK cu o lista goala.
     */
    @GetMapping("/events")
    fun getEvents(@RequestParam(required=false) location: String?,
                  @RequestParam(required=false) name: String?,
                  @RequestParam(required=false) description: String?
    ): ResponseEntity<CollectionModel<EntityModel<DataObject>>> {
        val events: List<Event> = eventService.getEventsByParameters(location, name, description)
        val json = buildHateoasCollection(events) {
            addManualLink(linkTo(EventController::class.java).slash("events").withSelfRel())
        }
        return ResponseEntity(json, HttpStatus.OK)
    }
}