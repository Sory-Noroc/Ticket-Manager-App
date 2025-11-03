package com.pos.laborator.controllers

import com.pos.laborator.controllers.EventController.Companion.EVENT_EXAMPLE
import com.pos.laborator.utils.buildHateoasCollection
import com.pos.laborator.utils.buildHateoasModel
import com.pos.laborator.model.Packet
import com.pos.laborator.interfaces.DataObject
import com.pos.laborator.model.Ticket
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

//
//@RestController
//@RequestMapping("/api/event-manager")
//open class EventPacketController(private val packetService: PacketService) {
//
//    companion object {
//        val PACKET_EXAMPLE = Packet(ID = 0, ownerId = 0, name = "Packet Example", location = "Location Example", description = "Description Example")
//    }
//
//    @RequestMapping(value = ["/event-packets/{packetId}/events"], method = [RequestMethod.GET])
//    fun getEventsByPacket(@PathVariable packetId: Int): ResponseEntity<CollectionModel<EntityModel<DataObject>>> {
//        val response = packetService.getEventsByPacket(packetId)
//        if (response.isNotEmpty()) {
//            val json = buildHateoasCollection(response) {
//                self { methodOn(EventPacketController::class.java).getEventsByPacket(packetId) }
//                parent { methodOn(EventPacketController::class.java).getEventPacket(packetId) }
//            }
//            return ResponseEntity(json, HttpStatus.OK)
//        } else {
//            val json = buildHateoasCollection(listOf(EVENT_EXAMPLE)) {
//                self { methodOn(EventPacketController::class.java).getEventsByPacket(packetId) }
//                parent { methodOn(EventPacketController::class.java).getEventPacket(packetId) }
//            }
//            return ResponseEntity(json, HttpStatus.NOT_FOUND)
//        }
//    }
//
//    @RequestMapping(value = ["/event-packets/{packetId}"], method = [RequestMethod.GET])
//    fun getEventPacket(@PathVariable packetId: Int): ResponseEntity<EntityModel<Packet>> {
//        try {
//            val response = packetService.getPacket(packetId)
//            val json = buildHateoasModel(response) {
//                self { methodOn(EventPacketController::class.java).getEventPacket(packetId) }
//                parent { methodOn(EventPacketController::class.java).getPackets() }
//            }
//            return ResponseEntity.ok(json)
//        } catch (e: Exception) {
//
//            val json = buildHateoasModel(null) {
//                parent { methodOn(EventPacketController::class.java).getPackets() }
//            }
//            return ResponseEntity.notFound().build()
//        }
//    }
//
//    @RequestMapping(value = ["/events/{id}/event-packets"], method = [RequestMethod.GET])
//    fun getEventPacketsByEventId(@PathVariable id: Int): ResponseEntity<CollectionModel<EntityModel<DataObject>>> {
//        val response = packetService.getPacketsByEventId(id)
//        return if (response.isNotEmpty()) {
//            val json = buildHateoasCollection(response) {
//                self { methodOn(EventController::class.java).getEventPacketsByEventId(id) }
//                parent { methodOn(EventController::class.java).getEvent(id) }
//            }
//            ResponseEntity(json, HttpStatus.OK)
//        } else {
//            val json = buildHateoasCollection(response) {
//                self { methodOn(EventController::class.java).getEventPacketsByEventId(id) }
//                parent { methodOn(EventController::class.java).getEvents(null, null, null) }
//            }
//            ResponseEntity(json, HttpStatus.NOT_FOUND)
//        }
//    }
//
//    @RequestMapping(value = ["/event-packets/{packetId}"], method = [RequestMethod.PUT])
//    fun updateEventPacket(@PathVariable packetId: Int, @RequestBody packet: Packet): ResponseEntity<EntityModel<Packet>> {
//        try {
//            packetService.getPacket(packetId)
//            packet.ID = packetId
//            packetService.updatePacket(packet)
//            val updated = packetService.getPacket(packetId)
//            val json = buildHateoasModel(updated) {
//                parent { methodOn(EventPacketController::class.java).getPackets() }
//            }
//            return ResponseEntity(json, HttpStatus.ACCEPTED)
//        } catch (e: Exception) {
//            val json = buildHateoasModel(null) {
//                self { methodOn(EventPacketController::class.java).getEventPacket(packetId) }
//                parent { methodOn(EventPacketController::class.java).getPackets() }
//            }
//            return ResponseEntity.notFound().build()
//        }
//    }
//
//    @RequestMapping(value = ["/event-packets/{packetId}"], method = [RequestMethod.PATCH])
//    fun patchEventPacket(@PathVariable packetId: Int,
//                         @RequestParam ownerId: Int,
//                         @RequestParam name: String,
//                         @RequestParam location: String,
//                         @RequestParam description: String,
//    ): ResponseEntity<EntityModel<Packet>> {
//        try {
//            packetService.patchPacket(packetId, ownerId, name, location, description)
//            val packet = packetService.getPacket(packetId)
//            val json = buildHateoasModel(packet) {
//                self { methodOn(EventPacketController::class.java).patchEventPacket(packetId, ownerId, name, location, description) }
//                parent { methodOn(EventPacketController::class.java).getPackets() }
//            }
//            return ResponseEntity.ok(json)
//        } catch (e: Exception) {
//            return ResponseEntity(HttpStatus.NOT_FOUND)
//        }
//    }
//
//    @RequestMapping(value = ["/event-packets/{packetId}"], method = [RequestMethod.DELETE])
//    fun deleteEventPacket(@PathVariable packetId: Int): ResponseEntity<Packet> {
//        val response = packetService.deletePacket(packetId)
//        return if (response) {
//            ResponseEntity(HttpStatus.OK)
//        } else {
//            ResponseEntity(HttpStatus.NOT_FOUND)
//        }
//    }
//
//
//    @RequestMapping(value = ["/event-packets/{packetId}/tickets/{cod}"], method = [RequestMethod.GET])
//    fun getTicketByPacket(@PathVariable packetId: Int, @PathVariable cod: String): ResponseEntity<Ticket> {
//        try {
//            val response = packetService.getTicketByPacketId(packetId, cod)
//            return ResponseEntity.ok(response)
//        } catch (e: Exception) {
//            return ResponseEntity(HttpStatus.NOT_FOUND)
//        }
//    }
//
//
//    @RequestMapping(value = ["/event-packets"], method = [RequestMethod.GET])
//    fun getPackets(): ResponseEntity<List<Packet>> {
//        val packets = packetService.packets
//        return ResponseEntity.ok(packets)
//    }
//
//    @RequestMapping(value = ["/event-packet"], method = [RequestMethod.POST])
//    fun addPacket(@RequestBody packet: Packet): ResponseEntity<Packet> {
//        packetService.addPacket(packet)
//        return ResponseEntity(HttpStatus.OK)
//    }
//
//
//    @RequestMapping(value = ["/event-packets?page={page}&items_per_page={count}"],
//        method = [RequestMethod.GET])
//    fun getEventPackets(@PathVariable page: Int, @PathVariable count: Int = 3): ResponseEntity<List<Packet>> {
//        try {
//            val response = packetService.getEventPackets(page, count)
//            return ResponseEntity.ok(response)
//        } catch (e: Exception) {
//            return ResponseEntity(HttpStatus.NOT_FOUND)
//        }
//    }
//
//}