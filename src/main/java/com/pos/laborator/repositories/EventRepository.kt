package com.pos.laborator.repositories

import com.pos.laborator.model.Event
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EventRepository: CrudRepository<Event, Int> {
    fun findByName(name: String): Event
    fun findByLocation(location: String): List<Event>
    fun findByOwnerId(ownerId: Int): List<Event>
    fun findByLocationContainingIgnoreCase(locationSubstring: String): List<Event>

    //@Query("SELECT e FROM Event e WHERE LOWER(e.location) LIKE LOWER(CONCAT('%', :locationSubstring, '%'))")
    //fun searchByLocationSubstring(@Param("locationSubstring") locationSubstring: String): List<Event>

//    @Query("SELECT e FROM Event e WHERE e.name LIKE :name OR e.ID = :id")
//    fun findSimilarEvent(@Param("id") id: Int?, @Param("name") name: String): List<Event>
}