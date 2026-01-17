package com.pos.laborator.model

import com.pos.laborator.repositories.EventRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
open class EventRepositoryTest {

    @Autowired
    private lateinit var eventRepository: EventRepository

    private lateinit var event1: Event
    private lateinit var event2: Event

    @BeforeEach
    @Throws(Exception::class)
    fun setUp() {
        event1 = Event(
            ownerId = 1,
            name = "Test event",
            location = "Here",
            description = "Forgot description",
            seats = 69
        )
        event2 = Event(
            ownerId = 1,
            name = "Other event",
            location = "There",
            description = "Another event",
            seats = 10
        )

        assertNull(event1.id)
        assertNull(event2.id)

        event1 = eventRepository.save(event1)
        event2 = eventRepository.save(event2)

        assertNotNull(event1.id)
        assertNotNull(event2.id)
    }

    @Test
    fun testFetchData() {
        val foundEvent: Event = eventRepository.findById(event2.id!!).get()

        assertNotNull(foundEvent)
        assertEquals(10, foundEvent.seats)
        assertEquals("There", foundEvent.location)

        val events: Iterable<Event> = eventRepository.findAll()
        assertEquals(2, events.count())
    }

    @Test
    fun testFetchFirstElement() {
        // Test to see if id starts from 1 - true
        assertThrows(NoSuchElementException::class.java) {
            eventRepository.findById(0).get()
        }
        val firstEvent: Event = eventRepository.findByName("Test event")
        val secondEvent: Event = eventRepository.findByName("Other event")
        assertEquals(69, firstEvent.seats)
        assertEquals("Here", firstEvent.location)
    }

    @Test
    fun testFetchByLocation() {
        val events: List<Event> = eventRepository.findByLocation(event1.location!!)
        assertNotNull(events)
        assertEquals(2, events.size)
        assertEquals(events[0], event1)
    }

    @Test
    fun testFetchByOwnerId() {
        // both have ownerId = 1
        val events: List<Event> = eventRepository.findByOwnerId(event1.ownerId)
        assertEquals(2, events.size)
        assertEquals(events[0].ownerId, 1)
        assertEquals(events[0], event1)
    }

    @Test
    fun testAddData() {
        val newEvent = Event(
            ownerId = 1,
            name = "my event",
            location = "New York",
            description = "My awesome event",
            seats = 67
        )
        eventRepository.save(newEvent)
        val returnedEvent: Event = eventRepository.findByName(newEvent.name)
        assertNotNull(returnedEvent.id)
        assertEquals(returnedEvent, newEvent)
        val events = eventRepository.findAll()
        assertEquals(3, events.count())
    }

    @Test
    fun testDeleteData() {
        eventRepository.delete(event1)

        val events = eventRepository.findAll()
        assertEquals(1, events.count())
    }

    @Test
    fun updateData() {
        var events = eventRepository.findAll().toList()
        assertEquals("Other event", events[1].name)
        event2.name = "Other name"
        eventRepository.save(event2)
        events = eventRepository.findAll().toList()
        assertEquals(2, events.size)
        assertEquals("Other name", events[1].name)
    }

    @Test
    fun `test complex filter method`() {
        // event1: name="Test event", location="Here", description="Forgot description"
        // event2: name="Other event", location="There", description="Another event"

        val result1 = eventRepository.findEventsByFilters(
            "here", "test", "forgot"
        )
        assertEquals(1, result1.size)
        assertEquals(event1.id, result1[0].id)

        val result2 = eventRepository.findEventsByFilters(
            "ere", "event", ""
        )
        // Both locations contain "ere" ("Here", "There"). Both names contain "event".
        assertEquals(2, result2.size)
    }
}