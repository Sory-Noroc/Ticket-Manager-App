package com.pos.laborator.view

import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = "tickets")
data class Ticket(
    var CODE: String,
    var GroupID: Int,
    var EventID: Int
): Entity()
