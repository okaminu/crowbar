package lt.boldadmin.crowbar.generator

import lt.boldadmin.crowbar.repository.UserTokenRepository
import lt.boldadmin.crowbar.type.entity.UserToken
import java.util.*

class TokenGenerator(private val userTokenRepository: UserTokenRepository) {

    fun generateAndStore(userId: String) {
        userTokenRepository.save(UserToken(userId, UUID.randomUUID().toString()))
    }

    fun getTokenById(userId: String) = userTokenRepository.findById(userId).get().token

    fun getUserIdByToken(token: String) = userTokenRepository.findByToken(token).id

    fun doesTokenExist(token: String) = userTokenRepository.existsByToken(token)
}
