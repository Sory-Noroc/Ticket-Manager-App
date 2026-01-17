package com.pos.laborator.model

import com.pos.laborator.interfaces.DataObject
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Min
import org.springframework.hateoas.server.core.Relation

@Entity
@Table(name = "events")
@Relation(collectionRelation = "events")
data class Event(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,
    @param:Min(0)
    var ownerId: Int,
    var name: String,
    var location: String?,
    var description: String?,
    @param:Min(0)
    var seats: Int = 0
): DataObject