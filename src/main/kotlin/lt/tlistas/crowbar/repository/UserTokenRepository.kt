package lt.tlistas.crowbar.repository

import lt.tlistas.crowbar.type.entity.UserToken

interface UserTokenRepository {

    fun save(userToken: UserToken)

    fun existsByToken(token: String): Boolean

    fun findByToken(token: String): UserToken
}