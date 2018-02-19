package lt.tlistas.mobile.number.confirmation.repository

import lt.tlistas.mobile.number.confirmation.type.entity.Confirmation
import org.springframework.data.mongodb.repository.MongoRepository

interface ConfirmationRepository : MongoRepository<Confirmation, String> {

    fun deleteByCode(confirmationCode: String)

    fun existsByCode(confirmationCode: String): Boolean

    fun findByCode(confirmationCode: String): Confirmation
}