package com.pos.laborator.services

import com.pos.laborator.model.Event
import com.pos.laborator.repositories.EventRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional
import org.mockito.ArgumentMatchers.any

@ExtendWith(MockitoExtension::class)
class EventServiceTest {

    @Mock
    lateinit var repo: EventRepository

    @InjectMocks
    lateinit var service: EventService

    @Test
    fun `addEvent saves and returns event`() {
        val event = Event(id = 1, ownerId = 1, name = "Test", location = "Loc", description = "Desc", seats = 10)
        `when`(repo.save(event)).thenReturn(event)

        val result = service.addEvent(event)
        assertEquals(event, result)
        verify(repo).save(event)
    }

    @Test
    fun `getEvent returns event when found`() {
        val event = Event(id = 1, ownerId = 1, name = "Test", location = "Loc", description = "Desc", seats = 10)
        `when`(repo.findById(1)).thenReturn(Optional.of(event))

        val result = service.getEvent(1)
        assertEquals(event, result)
    }

    @Test
    fun `getEventsByParameters filters correctly`() {
        val event1 = Event(id = 1, ownerId = 1, name = "Alpha", location = "City", description = "First", seats = 10)
        val event2 = Event(id = 2, ownerId = 1, name = "Beta", location = "Town", description = "Second", seats = 20)
        
        `when`(repo.findEventsByFilters("City", null, null)).thenReturn(listOf(event1))

        val result = service.getEventsByParameters("City", null, null)
        assertEquals(1, result.size)
        assertEquals("Alpha", result[0].name)
    }
}
