package lt.tlistas.crowbar

import lt.tlistas.crowbar.api.ConfirmationMessageGateway
import lt.tlistas.crowbar.generator.ConfirmationCodeGenerator
import lt.tlistas.crowbar.generator.TokenGenerator
import lt.tlistas.crowbar.repository.UserConfirmationCodeRepository
import lt.tlistas.crowbar.repository.UserTokenRepository

class IdentityConfirmation(
    private val userConfirmationCodeRepository: UserConfirmationCodeRepository,
    private val userTokenRepository: UserTokenRepository,
    private val confirmationMessageGateway: ConfirmationMessageGateway,
    private val confirmationCodeGenerator: ConfirmationCodeGenerator,
    private val tokenGenerator: TokenGenerator
) {

    fun sendConfirmationCode(userId: String, address: String) {
        confirmationCodeGenerator.generateAndStore(userId)

        val code = userConfirmationCodeRepository.findById(userId).get().id

        confirmationMessageGateway.send(buildConfirmationMessage(code), address)
    }


    fun confirmCode(code: String) {
        val userId = userConfirmationCodeRepository.findByCode(code).id

        userConfirmationCodeRepository.deleteByCode(code)

        tokenGenerator.generateAndStore(userId)
    }

    fun getTokenById(userId: String) = userTokenRepository.findById(userId).get().token

    fun getUserIdByToken(userId: String) = userTokenRepository.findByToken(userId).id

    fun doesTokenExist(token: String) = userTokenRepository.existsByToken(token)

    fun getUserIdByCode(code: String) = userConfirmationCodeRepository.findByCode(code).id

    fun doesUserByCodeExist(code: String) = userConfirmationCodeRepository.existsByCode(code)

    private fun buildConfirmationMessage(code: String) = "Your validation code is $code."
}