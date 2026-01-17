package com.pos.laborator.repositories

import com.pos.laborator.model.Event
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface EventRepository: CrudRepository<Event, Int> {
    fun findByName(name: String): Event
    @Query("SELECT e FROM Event e WHERE LOWER(e.location) LIKE LOWER(CONCAT('%', :locationSubstring, '%'))")
    fun findByLocation(locationSubstring: String): List<Event>

    fun findByOwnerId(ownerId: Int): List<Event>

    @Query("SELECT e FROM Event e WHERE " +
            "(:location IS NULL OR LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:description IS NULL OR LOWER(e.description) LIKE LOWER(CONCAT('%', :description, '%')))")
    fun findEventsByFilters(
        @Param("location") location: String?,
        @Param("name") name: String?,
        @Param("description") description: String?
    ): List<Event>
    //@Query("SELECT e FROM Event e WHERE LOWER(e.location) LIKE LOWER(CONCAT('%', :locationSubstring, '%'))")
    //fun searchByLocationSubstring(@Param("locationSubstring") locationSubstring: String): List<Event>

//    @Query("SELECT e FROM Event e WHERE e.name LIKE :name OR e.ID = :id")
//    fun findSimilarEvent(@Param("id") id: Int?, @Param("name") name: String): List<Event>
}