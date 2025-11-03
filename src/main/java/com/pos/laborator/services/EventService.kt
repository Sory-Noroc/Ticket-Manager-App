package com.pos.laborator.services

import com.pos.laborator.repositories.EventRepository
import com.pos.laborator.model.Event
import com.pos.laborator.model.Packet
import com.pos.laborator.model.Ticket
import org.springframework.stereotype.Service

@Service
open class EventService(private val repo: EventRepository) {
//    val events: MutableList<Event> = mutableListOf(
//        Event(0, 1, "Balul Bobocilor", "Iasi", "Va asteptam la acest eveniment minunat!", 200),
//        Event(1, 0, "Nunta", "Chisinau", "Fiti alaturi de noi in aceasta sarbatoare de neuitat!", 100),
//        Event(2, 1, "Careu de elevi", "Iasi", "Careu de 1 Septembrie", 650)
//    )
//    val packets: MutableList<Packet> = mutableListOf(
//        Packet(0, 0, "Packet Nunta", "Chisinau", "Include inscrierea, cununia, nunta, zeama."),
//        Packet(1, 1, "Packet Scoala", "Iasi", "Incepeti anul scolar cu sarbatori!;)"),
//    )
//    val tickets: MutableList<Ticket> = mutableListOf(
//        Ticket("Bilet4510", 0, 1),
//        Ticket("Bilet2372", 1, 2),
//        Ticket("Bilet3011", 1, 0),
//        Ticket("Bilet0952", 1, 0),
//        Ticket("Bilet1861", 0, 2),
//    )

    fun addEvent(event: Event) = repo.save(event)

    fun getEvent(id: Int) = repo.findById(id)

    fun getEventsByNameAndLocation(name: String, location: String): List<Event> {
        try {
            if (name != "") {
                return listOf(repo.findByName(name))
            } else if (location != "") {
                return repo.findByLocationContainingIgnoreCase(location)
            }
        } catch (e: Exception) {
            println("Exception occurred while trying to get events for $name")
            return listOf()
        }
        return listOf()
    }

    fun getEventsByPacket(packetId: Int) {
        // return events.filter { it.ownerId == 0 }  // TODO()
    }

    fun updateEvent(event: Event) {
        repo.save(event)
    }

    fun deleteEvent(id: Int) = repo.deleteById(id)

//    fun getPacketsByEventId(id: Int): List<Packet> {
//        return tickets.filter { it.EventID == id }.map { packets[it.GroupID] }
//    }

//    fun patchTicket(cod: String, groupId: Int, eventId: Int): Boolean {
//        val ticket = tickets.find { it.CODE == cod }
//        ticket?.let {
//            ticket.GroupID = groupId
//            ticket.EventID = eventId
//            return true
//        }
//        return false
//    }

//    fun getTicketByEventId(id: Int, cod: String): Ticket {
//        return tickets.find { it.CODE == cod && it.EventID == id }!!
//    }
//
//    fun getTicketByPacketId(id: Int, cod: String): Ticket {
//        return tickets.find { it.CODE == cod && it.GroupID == id }!!
//    }

    fun getEventsByParameters(location: String?, subname: String?, subdesc: String?): List<Event> {
        val events: List<Event> = repo.findAll().toList()

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
}