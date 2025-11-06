package com.pos.laborator.model

import com.pos.laborator.interfaces.DataObject
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.hateoas.server.core.Relation
import java.security.SecureRandom
import java.util.Base64

@Entity
@Table(name = "tickets")
@Relation(collectionRelation = "tickets")
data class Ticket(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var code: String? = null,
    var groupID: Int,
    var eventID: Int
): DataObject {
    companion object {
        fun generateTicketCodeBase64(): String {
            val random = SecureRandom()

            val bytes = ByteArray(12)
            random.nextBytes(bytes)
            val encoder = Base64.getUrlEncoder().withoutPadding()

            return encoder.encodeToString(bytes)
        }
    }
}