package lt.tlistas.mobile.number.confirmation.service

import lt.tlistas.mobile.number.confirmation.SmsGateway
import lt.tlistas.mobile.number.confirmation.exception.InvalidConfirmationCodeException
import lt.tlistas.mobile.number.confirmation.repository.ConfirmationRepository
import lt.tlistas.core.service.CollaboratorService
import lt.tlistas.mobile.number.confirmation.type.entity.Confirmation
import java.util.*

class ConfirmationService(private val confirmationRepository: ConfirmationRepository,
                          private val collaboratorService: CollaboratorService,
                          private val smsGateway: SmsGateway) {


    fun sendConfirmation(mobileNumber: String) {
        val code = generate()
        confirmationRepository.save(Confirmation(collaboratorService.getByMobileNumber(mobileNumber), code))

        smsGateway.send(code, mobileNumber)
    }

    fun findByCode(code: String) = confirmationRepository.findByCode(code)

    fun confirmationCodeExists(code: String) = confirmationRepository.existsByCode(code)

    internal fun removeValidConfirmation(code: String) {
        if (!confirmationCodeExists(code))
            throw InvalidConfirmationCodeException()

        confirmationRepository.deleteByCode(code)
    }

    internal fun generate(): String {
        var randomCode = ""
        repeat(CODE_LENGTH) {
            randomCode += Random().nextInt(10).toString()
        }
        if (confirmationCodeExists(randomCode))
            generate()

        return randomCode
    }

    companion object {
        const val CODE_LENGTH = 6
    }
}