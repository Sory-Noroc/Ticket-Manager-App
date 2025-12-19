package com.pos.laborator.services

import com.pos.laborator.model.Packet
import com.pos.laborator.repositories.PacketRepository
import org.springframework.stereotype.Service
import kotlin.collections.contains
import kotlin.collections.filter

@Service
class PacketService(
    private val packetRepo: PacketRepository,
    private val ticketService: TicketService,
    private val eventService: EventService
) {

    fun addPacket(packet: Packet): Packet = packetRepo.save(packet)

    fun getPacket(id: Int): Packet? = packetRepo.findById(id).orElse(null)

    fun updatePacket(packet: Packet) {
        packetRepo.save(packet)
    }

    fun deletePacket(id: Int) = packetRepo.deleteById(id)

    fun getEventPackets(type: String?, availableTickets: Int?): List<Packet> {
        val packets = packetRepo.findAll()
        val tickets = ticketService.getAllTickets()
        val events = eventService.getEventsByParameters(null, null, null)

        return packets
            .filter { packet ->
                type == null || packet.description?.contains(type) == true
            }
            .filter { packet -> availableTickets == null || tickets.filter { e -> e.eventID in (
                        // aici scoatem evenimentele care au bilete disponibile
                        events.filter { (it.seats ?: 0) - tickets.count { t -> t.eventID == it.id } > availableTickets }
                            .map {i -> i.id}
                    )
                }.map { i -> i.groupID }.contains(packet.id)
            }
    }

    fun getPacketsByParameters(ownerId: Int?, location: String?, subname: String?, subdesc: String?): List<Packet> {
        val packets: List<Packet> = packetRepo.findAll().toList()

        return packets
            .filter { packet ->
                ownerId == null || packet.ownerId == ownerId
            }
            .filter { packet ->
                location.isNullOrBlank() || packet.location?.contains(location, ignoreCase = true) == true
            }
            .filter { packet ->
                subname.isNullOrBlank() || packet.name.contains(subname, ignoreCase = true)
            }
            .filter { packet ->
                subdesc.isNullOrBlank() || packet.description?.contains(subdesc, ignoreCase = true) == true
            }
    }

    fun getPacketsByIds(ids: List<Int>) = packetRepo.findAllById(ids).toList()
}