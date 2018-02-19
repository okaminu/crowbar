package lt.tlistas.mobile.number.confirmation.repository

import lt.tlistas.mobile.number.confirmation.type.entity.Authentication
import org.springframework.data.mongodb.repository.MongoRepository

interface AuthenticationRepository : MongoRepository<Authentication, String> {

    fun existsByToken(token: String): Boolean

    fun findByToken(token: String): Authentication
}