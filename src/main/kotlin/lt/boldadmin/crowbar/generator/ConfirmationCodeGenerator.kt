package lt.boldadmin.crowbar.generator

import lt.boldadmin.crowbar.repository.UserConfirmationCodeRepository
import lt.boldadmin.crowbar.type.entity.UserConfirmationCode
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