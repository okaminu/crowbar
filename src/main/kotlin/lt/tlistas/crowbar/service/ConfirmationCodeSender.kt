package lt.tlistas.crowbar.service

import lt.tlistas.crowbar.api.ConfirmationMessageGateway
import lt.tlistas.crowbar.repository.ConfirmationCodeRepository
import lt.tlistas.crowbar.type.entity.ConfirmationCode
import java.util.*

class ConfirmationCodeSender(private val confirmationCodeRepository: ConfirmationCodeRepository,
                             private val confirmationMessageGateway: ConfirmationMessageGateway) {

    fun send(userId: String, address: String) {
        val code = generate()
        confirmationCodeRepository.save(ConfirmationCode(userId, code))

        confirmationMessageGateway.send(buildConfirmationMessage(code), address)
    }

    internal fun generate(): String {
        var randomCode = ""
        repeat(CODE_LENGTH) {
            randomCode += Random().nextInt(10).toString()
        }
        if (confirmationCodeRepository.existsByCode(randomCode))
            generate()

        return randomCode
    }

    private fun buildConfirmationMessage(code: String) = "Your validation code is $code."

    companion object {
        private const val CODE_LENGTH = 6
    }
}