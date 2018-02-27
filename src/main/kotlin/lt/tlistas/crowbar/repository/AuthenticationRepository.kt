package lt.tlistas.crowbar.repository

import lt.tlistas.crowbar.type.entity.Authentication

interface AuthenticationRepository {

    fun save(authentication: Authentication)

    fun existsByToken(token: String): Boolean

    fun findByToken(token: String): Authentication
}