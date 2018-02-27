package lt.tlistas.crowbar.service

import lt.tlistas.crowbar.api.exception.ConfirmationCodeNotFoundException
import lt.tlistas.crowbar.repository.AuthenticationRepository
import lt.tlistas.crowbar.repository.ConfirmationRepository
import lt.tlistas.crowbar.type.entity.Authentication
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