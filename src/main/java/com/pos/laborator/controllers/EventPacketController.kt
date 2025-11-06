package com.pos.laborator.controllers

import com.pos.laborator.interfaces.DataObject
import com.pos.laborator.utils.buildHateoasCollection
import com.pos.laborator.utils.buildHateoasModel
import com.pos.laborator.model.Packet
import com.pos.laborator.model.Ticket
import com.pos.laborator.services.PacketService
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.NoSuchElementException // Import necesar

@RestController
@RequestMapping("/api/event-manager")
class EventPacketController(private val packetService: PacketService) {

    companion object {
        val PACKET_EXAMPLE = Packet(id = 0, ownerId = 0, name = "Packet Example", location = "Location Example", description = "Description Example")
    }

    /**
     * Creeaza un pachet nou.
     * Returneaza 201 Created cu un header Location.
     */
    @PostMapping("/event-packets") // URL-ul este acum la plural
    fun addPacket(@RequestBody packet: Packet): ResponseEntity<EntityModel<Packet>> {
        val savedPacket = packetService.addPacket(packet)

        val json = buildHateoasModel(savedPacket) {
            val selfLink = linkTo(EventPacketController::class.java).slash(savedPacket.id).withSelfRel()
            addManualLink(selfLink)
            parent { methodOn(EventPacketController::class.java).getEventPackets(null, null) }
        }

        val locationUri = json.getLink("self").get().toUri()
        return ResponseEntity.created(locationUri).body(json)
    }

    /**
     * Obtine un pachet specific dupa ID.
     */
    @GetMapping("/event-packets/{packetId}")
    fun getEventPacket(@PathVariable packetId: Int): ResponseEntity<EntityModel<Packet>> {
        return try {
            val packet = packetService.getPacket(packetId) // Arunca NoSuchElementException
            val json = buildHateoasModel(packet) {
                addManualLink(linkTo(EventPacketController::class.java).slash(packetId).withSelfRel())
                parent { methodOn(EventPacketController::class.java).getEventPackets(null, null) }
            }
            ResponseEntity.ok(json)
        } catch (e: NoSuchElementException) {
            val json = buildHateoasModel(PACKET_EXAMPLE) {
                addManualLink(linkTo(EventPacketController::class.java).slash(packetId).withSelfRel())
                parent { methodOn(EventPacketController::class.java).getEventPackets(null, null) }
            }
            ResponseEntity(json, HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Obtinem packetele cu tipul introdus
     */
    @GetMapping("/event-packets/")
    fun getEventPackets(
        @RequestParam(required = false) type: String?,
        @RequestParam(required = false) availableTickets: Int?
    ): ResponseEntity<CollectionModel<EntityModel<DataObject>>> {
        val packets = packetService.getEventPackets(type, availableTickets)
        val json = buildHateoasCollection(packets) {
            addManualLink(linkTo(EventPacketController::class.java).slash("event-packets/").withSelfRel())
            parent { methodOn(EventPacketController::class.java).getEventPackets(null, null) }
        }
        return ResponseEntity.ok(json)
    }

    /**
     * Actualizeaza un pachet existent.
     */
    @PutMapping("/event-packets/{packetId}")
    fun updateEventPacket(@PathVariable packetId: Int, @RequestBody packet: Packet): ResponseEntity<EntityModel<Packet>> {
        packet.id = packetId
        packetService.updatePacket(packet)
        val updatedPacket = packetService.getPacket(packetId)
        val json = buildHateoasModel(updatedPacket) {
            self { methodOn(EventPacketController::class.java).getEventPacket(packetId) }
            parent { methodOn(EventPacketController::class.java).getEventPackets(null, null) }
        }
        return ResponseEntity(json, HttpStatus.OK)
    }

    /**
     * Sterge un pachet.
     */
    @DeleteMapping("/event-packets/{packetId}")
    fun deleteEventPacket(@PathVariable packetId: Int): ResponseEntity<EntityModel<Packet>> {
        return try {
            val packet = packetService.getPacket(packetId)
            packetService.deletePacket(packetId)

            val json = buildHateoasModel(packet) {
                parent { methodOn(EventPacketController::class.java).getEventPackets(null, null) }
            }
            ResponseEntity(json, HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            val json = buildHateoasModel(PACKET_EXAMPLE) {
                addManualLink(linkTo(EventPacketController::class.java).slash(packetId).withSelfRel())
                parent { methodOn(EventPacketController::class.java).getEventPackets(null, null) }
            }
            ResponseEntity(json, HttpStatus.NOT_FOUND)
        }
    }
}