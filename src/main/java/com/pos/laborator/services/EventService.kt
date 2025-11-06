package com.pos.laborator.services

import com.pos.laborator.repositories.EventRepository
import com.pos.laborator.model.Event
import org.springframework.stereotype.Service

@Service
open class EventService(private val repo: EventRepository) {

    fun addEvent(event: Event): Event = repo.save(event)

    fun getEvent(id: Int) = repo.findById(id).get()

    fun updateEvent(event: Event) {
        repo.save(event)
    }

    fun deleteEvent(id: Int) = repo.deleteById(id)

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

    fun getEventsByIds(ids: List<Int>): List<Event> {
        return repo.findAllById(ids).toList()
    }
}