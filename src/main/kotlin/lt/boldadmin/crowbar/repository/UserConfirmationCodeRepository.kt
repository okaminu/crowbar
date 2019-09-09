package lt.boldadmin.crowbar.repository

import lt.boldadmin.crowbar.type.entity.UserConfirmationCode
import java.util.*

interface UserConfirmationCodeRepository {

    fun save(code: UserConfirmationCode)

    fun deleteByCode(id: String)

    fun findById(id: String): Optional<UserConfirmationCode>

    fun existsByCode(code: String): Boolean

    fun findByCode(code: String): UserConfirmationCode

}
