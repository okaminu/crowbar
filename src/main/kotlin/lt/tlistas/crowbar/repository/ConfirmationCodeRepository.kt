package lt.tlistas.crowbar.repository

import lt.tlistas.crowbar.type.entity.ConfirmationCode

interface ConfirmationCodeRepository {

    fun save(confirmationCode: ConfirmationCode)

    fun delete(confirmationCode: ConfirmationCode)

    fun existsByCode(confirmationCode: String): Boolean

    fun findByCode(confirmationCode: String): ConfirmationCode
}