package com.pos.laborator.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DatabaseTest: Database() {
    @Test
    fun addEvent() {
        addEvent(events[0])
        Assertions.assertEquals(events.size, 4)
    }

    @Test
    fun addPacket() {
    }

    @Test
    fun addTicket() {
    }

    @Test
    fun getEvent() {
    }

    @Test
    fun getPacket() {
    }

    @Test
    fun getEventsByPacket() {
    }

    @Test
    fun updateEvent() {
    }

    @Test
    fun updatePacket() {
    }

    @Test
    fun patchEvent() {
    }

    @Test
    fun patchPacket() {
    }

    @Test
    fun deleteEvent() {
    }

    @Test
    fun deletePacket() {
    }

    @Test
    fun getPacketsByEventId() {
    }

    @Test
    fun getTicket() {
    }

    @Test
    fun updateTicket() {
    }

    @Test
    fun patchTicket() {
    }

    @Test
    fun deleteTicket() {
    }

    @Test
    fun getTicketByEventId() {
    }

    @Test
    fun getTicketByPacketId() {
    }

    @Test
    fun getEventsByLocation() {
        Assertions.assertEquals(2, events.count { it.location == "Iasi" })

    }

    @Test
    fun getEventPackets() {
    }

    @Test
    fun getEventsBySubName() {
    }

}