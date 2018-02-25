package lt.tlistas.mobile.number.confirmation.service

import lt.tlistas.mobile.number.confirmation.api.ConfirmationMessageGateway
import lt.tlistas.mobile.number.confirmation.repository.ConfirmationRepository
import lt.tlistas.mobile.number.confirmation.type.entity.Confirmation
import java.util.*

class ConfirmationService(private val confirmationRepository: ConfirmationRepository,
                          private val smsGateway: ConfirmationMessageGateway) {


    fun sendConfirmation(mobileNumber: String, userId: String) {
        val code = generate()
        confirmationRepository.save(Confirmation(userId, code))

        //smsGateway.send(code, mobileNumber)
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

    companion object {
        const val CODE_LENGTH = 6
    }
}