package lt.tlistas.crowbar.repository

import lt.tlistas.crowbar.type.entity.Request

interface RequestRepository {

    fun save(confirmation: Request)

    fun delete(confirmation: Request)

    fun existsByCode(confirmationCode: String): Boolean

    fun findByCode(confirmationCode: String): Request
}