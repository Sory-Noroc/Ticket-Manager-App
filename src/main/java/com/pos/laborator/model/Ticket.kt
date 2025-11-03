package com.pos.laborator.model

import com.pos.laborator.interfaces.DataObject
import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = "tickets")
data class Ticket(
    var CODE: String,
    var GroupID: Int,
    var EventID: Int
): DataObject