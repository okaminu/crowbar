package lt.tlistas.crowbar.service

import lt.tlistas.crowbar.api.ConfirmationMessageGateway
import lt.tlistas.crowbar.repository.ConfirmationRepository
import lt.tlistas.crowbar.type.entity.Confirmation
import java.util.*

class ConfirmationService(private val confirmationRepository: ConfirmationRepository,
                          private val confirmationMessageGateway: ConfirmationMessageGateway) {


    fun sendConfirmation(address: String, userId: String) {
        val code = generate()
        confirmationRepository.save(Confirmation(userId, code))

        confirmationMessageGateway.send(buildConfirmationMessage(code), address)
    }

    internal fun generate(): String {
        var randomCode = ""
        repeat(CODE_LENGTH) {
            randomCode += Random().nextInt(10).toString()
        }
        if (confirmationRepository.existsByCode(randomCode))
            generate()

        return randomCode
    }

    private fun buildConfirmationMessage(code: String) = "Your validation code is $code."

    companion object {
        private const val CODE_LENGTH = 6
    }
}