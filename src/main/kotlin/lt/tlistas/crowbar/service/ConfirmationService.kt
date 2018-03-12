package lt.tlistas.crowbar.service

import lt.tlistas.crowbar.exception.IncorrectConfirmationCodeException
import lt.tlistas.crowbar.repository.ConfirmationRepository
import lt.tlistas.crowbar.repository.RequestRepository
import lt.tlistas.crowbar.type.entity.Confirmation
import java.util.*

class ConfirmationService(private val requestRepository: RequestRepository,
                          private val confirmationRepository: ConfirmationRepository) {

    fun confirmCode(confirmationCode: String): String {
        if (!requestRepository.existsByCode(confirmationCode))
            throw IncorrectConfirmationCodeException()

        val confirmation = requestRepository.findByCode(confirmationCode)
        requestRepository.delete(confirmation)

        val token = generate()
        confirmationRepository.save(Confirmation(confirmation.id!!, token))

        return token
    }

    fun getUserId(token: String) = confirmationRepository.findByToken(token).id

    fun tokenExists(token: String) = confirmationRepository.existsByToken(token)

    internal fun generate(): String {
        val token = UUID.randomUUID().toString()
        if (tokenExists(token))
            generate()
        return token
    }
}