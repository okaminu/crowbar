package lt.tlistas.mobile.number.confirmation.service

import lt.tlistas.mobile.number.confirmation.exception.AuthenticationException
import lt.tlistas.mobile.number.confirmation.repository.AuthenticationRepository
import lt.tlistas.mobile.number.confirmation.type.entity.Authentication
import lt.tlistas.core.type.entity.Collaborator
import lt.tlistas.mobile.number.confirmation.exception.InvalidConfirmationCodeException
import java.util.*

class AuthenticationService(private val confirmationService: ConfirmationService,
                            private val repository: AuthenticationRepository) {

    fun getAuthenticationToken(confirmationCode: String): String {
        if (!confirmationService.confirmationCodeExists(confirmationCode))
            throw InvalidConfirmationCodeException()

        val confirmation = confirmationService.findByCode(confirmationCode)
        confirmationService.removeValidConfirmation(confirmation.code)

        val token = generate()
        save(Authentication(confirmation.collaborator, token))

        return token
    }

    fun getCollaboratorByToken(token: String): Collaborator {
        if (!tokenExists(token))
            throw AuthenticationException()

        return repository.findByToken(token).collaborator
    }

    internal fun generate(): String {
        val token = UUID.randomUUID().toString()
        if (tokenExists(token))
            generate()
        return token
    }

    private fun save(authentication: Authentication) = repository.save(authentication)

    private fun tokenExists(token: String) = repository.existsByToken(token)

}