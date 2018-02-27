package lt.tlistas.crowbar.repository

import lt.tlistas.crowbar.type.entity.Confirmation

interface ConfirmationRepository {

    fun save(confirmation: Confirmation)

    fun delete(confirmation: Confirmation)

    fun existsByCode(confirmationCode: String): Boolean

    fun findByCode(confirmationCode: String): Confirmation
}