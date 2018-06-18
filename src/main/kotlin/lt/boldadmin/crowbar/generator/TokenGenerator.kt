package lt.boldadmin.crowbar.generator

import lt.boldadmin.crowbar.repository.UserTokenRepository
import lt.boldadmin.crowbar.type.entity.UserToken
import java.util.*

class TokenGenerator(private val userTokenRepository: UserTokenRepository) {

    fun generateAndStore(userId: String) {
        val uniqueToken = generateUnique()

        userTokenRepository.save(UserToken(userId, uniqueToken))
    }

    private fun generateUnique(): String {
        var token = generate()
        if (userTokenRepository.existsByToken(token))
            token = generateUnique()

        return token
    }

    private fun generate() = UUID.randomUUID().toString()

    fun getTokenById(userId: String) = userTokenRepository.findById(userId).get().token

    fun getUserIdByToken(userId: String) = userTokenRepository.findByToken(userId).id

    fun doesTokenExist(token: String) = userTokenRepository.existsByToken(token)
}