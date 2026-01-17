package com.pos.laborator.model

import com.pos.laborator.interfaces.DataObject
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.UniqueConstraint
import jakarta.persistence.Table
import org.checkerframework.common.aliasing.qual.Unique
import org.springframework.hateoas.server.core.Relation
import java.security.SecureRandom
import java.util.*

@Entity
@Table(name = "tickets")
@Relation(collectionRelation = "tickets")
data class Ticket(
    @Id
    @Column(unique = true, nullable = false)
    var code: String? = null,
    var groupID: Int? = null,
    var eventID: Int? = null
): DataObject {
    companion object {
        fun generateTicketCodeBase64(): String {
            val random = SecureRandom()

            val bytes = ByteArray(12)
            random.nextBytes(bytes)
            val encoder = Base64.getUrlEncoder().withoutPadding()

            return "Ticket_" + encoder.encodeToString(bytes)
        }
    }
}
