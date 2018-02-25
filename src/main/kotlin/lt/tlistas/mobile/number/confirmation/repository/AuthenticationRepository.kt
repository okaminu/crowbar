package lt.tlistas.mobile.number.confirmation.repository

import lt.tlistas.mobile.number.confirmation.type.entity.Authentication

interface AuthenticationRepository {

    fun save(authentication: Authentication)

    fun existsByToken(token: String): Boolean

    fun findByToken(token: String): Authentication
}