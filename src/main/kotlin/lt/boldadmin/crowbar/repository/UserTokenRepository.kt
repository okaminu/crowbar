package lt.boldadmin.crowbar.repository

import lt.boldadmin.crowbar.type.entity.UserToken
import java.util.*

interface UserTokenRepository {

    fun save(token: UserToken)

    fun existsByToken(token: String): Boolean

    fun findByToken(token: String): UserToken

    fun findById(id: String): Optional<UserToken>
}