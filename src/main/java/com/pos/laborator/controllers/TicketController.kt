//package com.pos.laborator.controllers
//
//import com.pos.laborator.model.EventRepository
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.*
//import com.pos.laborator.model.Ticket
//import com.pos.laborator.utils.buildHateoasModel
//import org.springframework.hateoas.EntityModel
//import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
//
///**
// * OpenAPI Docs available after deployment at: http://localhost:8080/swagger-ui/index.html#/
// */
//
//@RestController
//@RequestMapping("/api/event-manager")
//open class TicketController(private val packetService: EventRepository) {
//
//    companion object {
//        val TICKET_EXAMPLE = Ticket(
//            CODE = "Bilet0000",
//            GroupID = 0,
//            EventID = 0
//        )
//    }
//
//    @RequestMapping(value = ["/tickets/{cod}"], method = [RequestMethod.GET])
//    fun getTicketByCode(@PathVariable cod: String): ResponseEntity<Ticket> {
//        try {
//            val ticket = database.getTicket(cod)
//            return ResponseEntity.ok(ticket)
//        } catch (e: Exception) {
//            return ResponseEntity(HttpStatus.NOT_FOUND)
//        }
//    }
//
//    @RequestMapping(value = ["/events/{id}/tickets/{cod}"], method = [RequestMethod.GET])
//    fun getTicketByEvent(@PathVariable id: Int, @PathVariable cod: String): ResponseEntity<EntityModel<Ticket>> {
//        try {
//            val response = eventService.getTicketByEventId(id, cod)
//            val json = buildHateoasModel(response) {
//                self { methodOn(EventController::class.java).getTicketByEvent(id, cod) }
//                parent { methodOn(EventController::class.java).getEvent(id) }
//            }
//            return ResponseEntity(json, HttpStatus.OK)
//        } catch (e: Exception) {
//            val json = buildHateoasModel(TICKET_EXAMPLE) {
//                self { methodOn(EventController::class.java).getEvent(id) }
//                parent { methodOn(TicketController::class.java).getTickets() }
//            }
//            return ResponseEntity(json, HttpStatus.NOT_FOUND)
//        }
//    }
//
//
//    @RequestMapping(value = ["/tickets/{cod}"], method = [RequestMethod.PUT])
//    fun updateTicketByCode(@PathVariable cod: String, ticket: Ticket): ResponseEntity<Ticket> {
//        val response = database.updateTicket(cod, ticket)
//        return if (response) {
//            ResponseEntity(HttpStatus.ACCEPTED)
//        } else {
//            ResponseEntity(HttpStatus.NOT_FOUND)
//        }
//    }
//
//    @RequestMapping(value = ["/tickets/{cod}"], method = [RequestMethod.PATCH])
//    fun patchTicket(@PathVariable cod: String, eventId: Int, groupId: Int): ResponseEntity<Ticket> {
//        val response = database.patchTicket(cod, eventId, groupId)
//        return if (response) {
//            ResponseEntity(HttpStatus.ACCEPTED)
//        } else {
//            ResponseEntity(HttpStatus.NOT_FOUND)
//        }
//    }
//
//    @RequestMapping(value = ["/tickets/{cod}"], method = [RequestMethod.DELETE])
//    fun deleteTicketByCode(@PathVariable cod: String): ResponseEntity<Ticket> {
//        val response = database.deleteTicket(cod)
//        return if (response) {
//            ResponseEntity(HttpStatus.OK)
//        } else {
//            ResponseEntity(HttpStatus.NOT_FOUND)
//        }
//    }
//
//    @RequestMapping(value = ["/tickets"], method = [RequestMethod.GET])
//    fun getTickets(): ResponseEntity<List<Ticket>> {
//        val tickets = database.tickets
//        return ResponseEntity.ok(tickets)
//    }
//
//    @RequestMapping(value = ["/ticket"], method = [RequestMethod.POST])
//    fun addTicket(@RequestBody ticket: Ticket): ResponseEntity<Ticket> {
//        database.addTicket(ticket)
//        return ResponseEntity(HttpStatus.OK)
//    }
//}
