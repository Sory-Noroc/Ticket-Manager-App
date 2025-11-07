package com.pos.laborator.model

import com.pos.laborator.interfaces.DataObject
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.hateoas.server.core.Relation


@Entity
@Table(name = "packets")
@Relation(collectionRelation = "packets")
data class Packet(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,
    var ownerId: Int,
    var name: String,
    var location: String?,
    var description: String?,
): DataObject