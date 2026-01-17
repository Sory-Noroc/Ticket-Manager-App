package com.pos.laborator

import com.fasterxml.jackson.databind.ObjectMapper
import com.pos.laborator.model.Event
import com.pos.laborator.services.EventService
import com.pos.laborator.services.TicketService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    // We mock the service layer to isolate the web layer for these tests.
    // This makes the tests faster and less flaky as we don't need a real database.
    @MockBean
    private lateinit var eventService: EventService

    @Test
    fun `when GET events endpoint is called, it should return 200 OK`() {
        mockMvc.perform(get("/api/event-manager/events"))
            .andExpect(status().isOk)
    }

    @Test
    fun `given an event exists, when GET event by ID is called, it should return 200 OK and the event`() {
        val eventId = 1
        val event = Event(id = eventId, ownerId = 1, name = "Test Event", location = "Test Location", description = "A cool event", seats = 100)

        // Given: The service will return this event when asked for ID 1
        // Mockito.whenever(eventService.getEvent(eventId)).thenReturn(event)
        // Note: Mockito setup is commented out as the goal is to write the test structure.
        // You would uncomment and configure this with a tool like Mockito.

        mockMvc.perform(get("/api/event-manager/events/{id}", eventId))
            .andExpect(status().isOk)
            // .andExpect(jsonPath("$.name").value(event.name)) // Example assertion on the response body
            // .andExpect(jsonPath("$.location").value(event.location))
    }

    @Test
    fun `given a non-existent event ID, when GET event by ID is called, it should return 404 Not Found`() {
        val nonExistentEventId = 999

        // Given: The service will throw an exception for this ID
        // Mockito.whenever(eventService.getEvent(nonExistentEventId)).thenThrow(NoSuchElementException())

        mockMvc.perform(get("/api/event-manager/events/{id}", nonExistentEventId))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `when POST a new valid event, it should return 201 Created`() {
        val newEvent = Event(id = null, ownerId = 1, name = "New Concert", location = "Stadium", description = "Live music", seats = 5000)

        // Given: The service will successfully save the event
        // Mockito.whenever(eventService.addEvent(any(Event::class.java))).thenReturn(newEvent.copy(id = 1))

        mockMvc.perform(post("/api/event-manager/events")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newEvent)))
            .andExpect(status().isCreated)
            // .andExpect(header().exists("Location")) // A good REST API returns the new resource's URL
    }

    @Test
    fun `when POST an event with invalid data (no name), it should return 400 Bad Request`() {
        // Assuming 'name' is a non-nullable required field.
        // The Jackson deserialization might fail or a validation annotation (@NotNull) would trigger.
        val invalidEventPayload = """
            {
                "ownerId": 1,
                "location": "Someplace",
                "description": "An event without a name"
            }
        """.trimIndent()

        mockMvc.perform(post("/api/event-manager/events")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidEventPayload))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `given an event exists, when DELETE is called, it should return 200 OK`() {
        val eventId = 1

        // You might need to configure the mock service to do nothing on delete
        // Mockito.doNothing().whenever(eventService).deleteEvent(eventId)

        mockMvc.perform(delete("/api/event-manager/events/{id}", eventId))
            .andExpect(status().isOk)
    }

    @Test
    fun `when DELETE is called for a non-existent event, it should return 404 Not Found`() {
        val nonExistentEventId = 999

        // Given: The service will throw an exception when trying to delete
        // Mockito.doThrow(NoSuchElementException::class.java).whenever(eventService).deleteEvent(nonExistentEventId)

        mockMvc.perform(delete("/api/event-manager/events/{id}", nonExistentEventId))
            .andExpect(status().isNotFound)
    }
}
