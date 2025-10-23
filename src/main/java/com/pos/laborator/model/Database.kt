package com.pos.laborator.model

import com.pos.laborator.view.Event
import com.pos.laborator.view.Packet
import com.pos.laborator.view.Ticket
import org.springframework.stereotype.Component

@Component
open class Database {
    val events: MutableList<Event> = mutableListOf(
        Event(0, 1, "Balul Bobocilor", "Iasi", "Va asteptam la acest eveniment minunat!", 200),
        Event(1, 0, "Nunta", "Chisinau", "Fiti alaturi de noi in aceasta sarbatoare de neuitat!", 100),
        Event(2, 1, "Careu de elevi", "Iasi", "Careu de 1 Septembrie", 650)
    )
    val packets: MutableList<Packet> = mutableListOf(
        Packet(0, 0, "Packet Nunta", "Chisinau", "Include inscrierea, cununia, nunta, zeama."),
        Packet(1, 1, "Packet Scoala", "Iasi", "Incepeti anul scolar cu sarbatori!;)"),
    )
    val tickets: MutableList<Ticket> = mutableListOf(
        Ticket("Bilet4510", 0, 1),
        Ticket("Bilet2372", 1, 2),
        Ticket("Bilet3011", 1, 0),
        Ticket("Bilet0952", 1, 0),
        Ticket("Bilet1861", 0, 1),
    )

    fun addEvent(event: Event) = events.add(event)
    fun addPacket(packet: Packet) = packets.add(packet)
    fun addTicket(ticket: Ticket) = tickets.add(ticket)

    fun getEvent(id: Int) = events[id]
    fun getPacket(id: Int) = packets[id]

    fun getEventsByPacket(packetId: Int): List<Event> {
        return events.filter { it.ID_OWNER == 0 }  // TODO()
    }

    fun updateEvent(event: Event) {
        events[event.ID] = event
    }

    fun updatePacket(packet: Packet) {
        packets[packet.ID] = packet
    }

    fun patchEvent(id: Int, ownerId: Int? = null, name: String? = null, location: String? = null, description: String? = null, seats: Int? = null): Boolean {
        var changed = false
        ownerId?.let { events[id].setOwnerId(it); changed = true }
        name?.let { events[id].setName(it); changed = true }
        location?.let { events[id].setLocation(it); changed = true }
        description?.let { events[id].setDescription(it); changed = true }
        seats?.let { events[id].setSeats(it); changed = true }
        return changed
    }

    fun patchPacket(id: Int, ownerId: Int? = null, name: String? = null, location: String? = null, description: String? = null) {
        ownerId?.let { packets[id].setOwnerId(it) }
        name?.let { packets[id].setName(it) }
        location?.let { packets[id].setLocation(it) }
        description?.let { packets[id].setDescription(it) }
    }

    fun deleteEvent(id: Int) = events.removeIf { it.ID == id }
    fun deletePacket(id: Int) = packets.removeIf { it.ID == id }

    fun getPacketsByEventId(id: Int): List<Packet> {
        return tickets.filter { it.EventID == id }.map { packets[it.GroupID] }
    }
    ///////////////////
    fun getTicket(cod: String) = tickets.find { it.CODE == cod }

    fun updateTicket(cod: String, ticket: Ticket): Boolean {
        ticket.CODE = cod
        tickets.find { it.CODE == cod }?.let {
            tickets.remove(it)
            tickets.add(ticket)
            return true
        }
        return false
    }

    fun patchTicket(cod: String, groupId: Int, eventId: Int): Boolean {
        val ticket = tickets.find { it.CODE == cod }
        ticket?.let {
            ticket.GroupID = groupId
            ticket.EventID = eventId
            return true
        }
        return false
    }

    fun deleteTicket(cod: String): Boolean {
        return tickets.removeIf { it.CODE == cod }
    }

    ///////
    fun getTicketByEventId(id: Int, cod: String): Ticket {
        return tickets.find { it.CODE == cod && it.EventID == id }!!
    }

    fun getTicketByPacketId(id: Int, cod: String): Ticket {
        return tickets.find { it.CODE == cod && it.GroupID == id }!!
    }

    fun getEventsByParameters(location: String?, subname: String?, subdesc: String?): List<Event> {
        val events: List<Event> = this.events

        return events
            .filter { event ->
                location.isNullOrBlank() || event.location?.contains(location, ignoreCase = true) == true
            }
            .filter { event ->
                subname.isNullOrBlank() || event.name.contains(subname, ignoreCase = true)
            }
            .filter { event ->
                subdesc.isNullOrBlank() || event.description?.contains(subdesc, ignoreCase = true) == true
            }
    }

    fun getEventPackets(page: Int, packetsPerPage: Int): List<Packet> {
        return packets.chunked(packetsPerPage)[page]
    }
}
