package lt.tlistas.crowbar.service

import lt.tlistas.crowbar.exception.IncorrectConfirmationCodeException
import lt.tlistas.crowbar.repository.ConfirmationCodeRepository
import lt.tlistas.crowbar.repository.UserTokenRepository
import lt.tlistas.crowbar.type.entity.UserToken
import java.util.*

class TokenService(private val confirmationCodeRepository: ConfirmationCodeRepository,
                   private val userTokenRepository: UserTokenRepository) {

    fun confirmCode(confirmationCode: String): String {
        if (!confirmationCodeRepository.existsByCode(confirmationCode))
            throw IncorrectConfirmationCodeException()

        val confirmation = confirmationCodeRepository.findByCode(confirmationCode)
        confirmationCodeRepository.delete(confirmation)

        val token = generate()
        userTokenRepository.save(UserToken(confirmation.id!!, token))

        return token
    }

    fun getUserId(token: String) = userTokenRepository.findByToken(token).id

    fun tokenExists(token: String) = userTokenRepository.existsByToken(token)

    internal fun generate(): String {
        val token = UUID.randomUUID().toString()
        if (tokenExists(token))
            generate()
        return token
    }
}