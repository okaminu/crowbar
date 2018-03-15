package lt.tlistas.crowbar.generator

import lt.tlistas.crowbar.repository.UserConfirmationCodeRepository
import lt.tlistas.crowbar.type.entity.UserConfirmationCode
import java.util.*

class ConfirmationCodeGenerator(private val userConfirmationCodeRepository: UserConfirmationCodeRepository) {

     fun generateAndStore(userId: String) {
        val uniqueCode = generateUnique()
        userConfirmationCodeRepository.save(UserConfirmationCode(userId, uniqueCode))
    }

     private fun generateUnique(): String {
        var randomCode = generate()
        if (userConfirmationCodeRepository.existsByCode(randomCode))
           randomCode = generateUnique()

        return randomCode
    }

    private fun generate(): String {
        var randomCode = ""
        repeat(CODE_LENGTH) {
            randomCode += Random().nextInt(10).toString()
        }
        return randomCode
    }

    companion object {
        private const val CODE_LENGTH = 6
    }
}