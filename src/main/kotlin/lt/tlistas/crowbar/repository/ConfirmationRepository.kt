package lt.tlistas.crowbar.repository

import lt.tlistas.crowbar.type.entity.Confirmation

interface ConfirmationRepository {

    fun save(authentication: Confirmation)

    fun existsByToken(token: String): Boolean

    fun findByToken(token: String): Confirmation
}