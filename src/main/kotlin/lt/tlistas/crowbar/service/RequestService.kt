package lt.tlistas.crowbar.service

import lt.tlistas.crowbar.api.ConfirmationMessageGateway
import lt.tlistas.crowbar.repository.RequestRepository
import lt.tlistas.crowbar.type.entity.Request
import java.util.*

class RequestService(private val requestRepository: RequestRepository,
                     private val confirmationMessageGateway: ConfirmationMessageGateway) {

    fun sendConfirmation(userId: String, address: String) {
        val code = generate()
        requestRepository.save(Request(userId, code))

        confirmationMessageGateway.send(buildConfirmationMessage(code), address)
    }

    internal fun generate(): String {
        var randomCode = ""
        repeat(CODE_LENGTH) {
            randomCode += Random().nextInt(10).toString()
        }
        if (requestRepository.existsByCode(randomCode))
            generate()

        return randomCode
    }

    private fun buildConfirmationMessage(code: String) = "Your validation code is $code."

    companion object {
        private const val CODE_LENGTH = 6
    }
}