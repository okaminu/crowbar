package lt.tlistas.mobile.number.confirmation.repository

import lt.tlistas.mobile.number.confirmation.type.entity.Confirmation

interface ConfirmationRepository {

    fun save(confirmation: Confirmation)

    fun delete(confirmation: Confirmation)

    fun existsByCode(confirmationCode: String): Boolean

    fun findByCode(confirmationCode: String): Confirmation
}