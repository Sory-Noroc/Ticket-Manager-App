package com.pos.laborator

import com.fasterxml.jackson.databind.ObjectMapper
import com.pos.laborator.model.Ticket
import com.pos.laborator.services.TicketService
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class TicketControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var ticketService: TicketService

    @Test
    fun `when GET tickets endpoint is called, it should return 200 OK with a list of tickets`() {
        val tickets = listOf(
            Ticket(code = "TICKET1", eventID = 1, groupID = 0),
            Ticket(code = "TICKET2", eventID = 1, groupID = 0)
        )
        `when`(ticketService.getAllTickets()).thenReturn(tickets)

        mockMvc.perform(get("/api/event-manager/tickets"))
            .andExpect(status().isOk)
    }

    @Test
    fun `given a ticket exists, when GET ticket by code is called, it should return 200 OK with ticket details`() {
        val ticketCode = "TEST12345"
        val ticket = Ticket(code = ticketCode, eventID = 1, groupID = 0)

        `when`(ticketService.getTicketByCode(any())).thenReturn(Optional.of(ticket))

        mockMvc.perform(get("/api/event-manager/tickets/{code}", ticketCode))
            .andExpect(status().isOk)
    }

    @Test
    fun `given a non-existent ticket code, when GET ticket by code is called, it should return 404 Not Found`() {
        val nonExistentTicketCode = "NONEXISTENT"
        // Given: The service will return an empty Optional, indicating the ticket was not found.
        `when`(ticketService.getTicketByCode(nonExistentTicketCode)).thenReturn(Optional.empty())

        // When & Then: We expect a 404 Not Found response.
        mockMvc.perform(get("/api/event-manager/tickets/{code}", nonExistentTicketCode))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `when POST a new valid ticket, it should return 201 Created`() {
        val newTicket = Ticket(code = "NEWTICKET", eventID = 1, groupID = 0)

        // Given: The service will successfully save the new ticket.
        `when`(ticketService.addTicket(any())).thenReturn(newTicket)

        // When & Then: We post the new ticket and expect a 201 Created status.
        mockMvc.perform(post("/api/event-manager/ticket")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newTicket)))
            .andExpect(status().isCreated)
    }

    @Test
    fun `when POST an invalid ticket (ex missing eventID), it should return 400 Bad Request`() {
        // This tests the validation layer, not the service. The malformed JSON should be rejected before the service is even called.
        val invalidTicketPayload = """
            {
                "code": "BADTICKET",
                "groupID": 0
                // eventID is missing
            }
        """.trimIndent()

        mockMvc.perform(post("/api/event-manager/ticket")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidTicketPayload))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `when DELETE an existing ticket, it should return 204 No Content`() {
        val ticketCode = "TODELETE"
        `when`(ticketService.deleteTicket(ticketCode)).thenReturn(true)

        // When & Then: We expect a 204 No Content, which is a common successful response for a DELETE operation.
        mockMvc.perform(delete("/api/event-manager/tickets/{code}", ticketCode))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `when DELETE a non-existent ticket, it should return 404 Not Found`() {
        val nonExistentTicketCode = "NONE"

        // Given: The service will indicate failure (e.g., return false or throw) when trying to delete a non-existent ticket.
        `when`(ticketService.deleteTicket(nonExistentTicketCode)).thenReturn(false)

        // When & Then: We expect a 404 Not Found response.
        mockMvc.perform(delete("/api/event-manager/tickets/{code}", nonExistentTicketCode))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `when PUT to update an existing ticket, it should return 200 OK`() {
        val ticketCode = "TOUPDATE"
        val updatedTicket = Ticket(code = ticketCode, eventID = 2, groupID = 1) // Updated info

        // Given: The service will return the updated ticket upon a successful update.
        `when`(ticketService.updateTicket(any())).thenReturn(Optional.of(updatedTicket))

        // When & Then: We send the PUT request and expect a 200 OK status.
        mockMvc.perform(put("/api/event-manager/tickets/{code}", ticketCode)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedTicket)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.eventID", equalTo(2)))
            .andExpect(jsonPath("$.groupID", equalTo(1)))
    }

    @Test
    fun `when PUT to update a non-existent ticket, it should return 404 Not Found`() {
        val nonExistentCode = "NONEXISTENT"
        val ticketPayload = Ticket(code = nonExistentCode, eventID = 1, groupID = 1)

        // Given: The service will return an empty Optional to signify the ticket to update was not found.
        `when`(ticketService.updateTicket(any())).thenReturn(Optional.empty())

        // When & Then
        mockMvc.perform(put("/api/event-manager/tickets/{code}", nonExistentCode)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(ticketPayload)))
            .andExpect(status().isNotFound)
    }
}
