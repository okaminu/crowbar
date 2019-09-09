package lt.boldadmin.crowbar

import lt.boldadmin.crowbar.api.ConfirmationMessageGateway
import lt.boldadmin.crowbar.generator.ConfirmationCodeGenerator
import lt.boldadmin.crowbar.generator.TokenGenerator
import lt.boldadmin.crowbar.repository.UserConfirmationCodeRepository

class IdentityConfirmation(
    private val userConfirmationCodeRepository: UserConfirmationCodeRepository,
    private val confirmationMessageGateway: ConfirmationMessageGateway,
    private val confirmationCodeGenerator: ConfirmationCodeGenerator,
    private val tokenGenerator: TokenGenerator
) {

    fun sendConfirmationCode(userId: String, address: String) {
        confirmationCodeGenerator.generateAndStore(userId)

        val code = userConfirmationCodeRepository.findById(userId).get().code

        confirmationMessageGateway.send(buildConfirmationMessage(code), address)
    }

    fun confirmCode(code: String) {
        val userId = userConfirmationCodeRepository.findByCode(code).id

        userConfirmationCodeRepository.deleteByCode(code)

        tokenGenerator.generateAndStore(userId)
    }

    fun getTokenById(userId: String) = tokenGenerator.getTokenById(userId)

    fun getUserIdByToken(token: String) = tokenGenerator.getUserIdByToken(token)

    fun getUserIdByCode(code: String) = userConfirmationCodeRepository.findByCode(code).id

    fun doesTokenExist(token: String) = tokenGenerator.doesTokenExist(token)

    fun doesUserByCodeExist(code: String) = userConfirmationCodeRepository.existsByCode(code)

    private fun buildConfirmationMessage(code: String) = "Your validation code is $code."
}
