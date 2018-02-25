package lt.tlistas.mobile.number.confirmation.service

import lt.tlistas.mobile.number.confirmation.api.exception.ConfirmationCodeNotFoundException
import lt.tlistas.mobile.number.confirmation.repository.AuthenticationRepository
import lt.tlistas.mobile.number.confirmation.repository.ConfirmationRepository
import lt.tlistas.mobile.number.confirmation.type.entity.Authentication
import java.util.*

class AuthenticationService(private val confirmationRepository: ConfirmationRepository,
                            private val authenticationRepository: AuthenticationRepository) {

    fun getAuthenticationToken(confirmationCode: String): String {
        if (!confirmationRepository.existsByCode(confirmationCode))
            throw ConfirmationCodeNotFoundException()

        val confirmation = confirmationRepository.findByCode(confirmationCode)
        confirmationRepository.delete(confirmation)

        val token = generate()
        authenticationRepository.save(Authentication(confirmation.id!!, token))

        return token
    }

    fun getUserId(token: String) = authenticationRepository.findByToken(token).id

    fun tokenExists(token: String) = authenticationRepository.existsByToken(token)

    internal fun generate(): String {
        val token = UUID.randomUUID().toString()
        if (tokenExists(token))
            generate()
        return token
    }
}