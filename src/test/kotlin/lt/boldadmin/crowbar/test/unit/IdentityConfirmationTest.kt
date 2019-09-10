package lt.boldadmin.crowbar.test.unit

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import lt.boldadmin.crowbar.IdentityConfirmation
import lt.boldadmin.crowbar.api.ConfirmationMessageGateway
import lt.boldadmin.crowbar.generator.ConfirmationCodeGenerator
import lt.boldadmin.crowbar.generator.TokenGenerator
import lt.boldadmin.crowbar.repository.UserConfirmationCodeRepository
import lt.boldadmin.crowbar.type.entity.UserConfirmationCode
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class IdentityConfirmationTest {

    @MockK
    private lateinit var codeRepositoryMock: UserConfirmationCodeRepository

    @MockK
    private lateinit var tokenGeneratorMock: TokenGenerator

    @MockK
    private lateinit var codeGeneratorMock: ConfirmationCodeGenerator

    @MockK
    private lateinit var messageGateway: ConfirmationMessageGateway

    private lateinit var identityConfirmationService: IdentityConfirmation

    @BeforeEach
    fun `Set up`() {
        identityConfirmationService = IdentityConfirmation(
            codeRepositoryMock,
            messageGateway, codeGeneratorMock, tokenGeneratorMock
        )
    }

    @Test
    fun `Sends confirmation code`() {
        every { codeRepositoryMock.findById(USER_ID) } returns Optional.of(UserConfirmationCode(USER_ID, CODE))
        every { codeGeneratorMock.generateAndStore(USER_ID) } just Runs
        every { messageGateway.send(any(), eq("mobileNumber")) } just Runs

        identityConfirmationService.sendConfirmationCode(USER_ID, "mobileNumber")

        verify { codeGeneratorMock.generateAndStore("userId") }
        verify { codeRepositoryMock.findById(USER_ID) }
        verify { messageGateway.send(any(), eq("mobileNumber")) }
    }

    @Test
    fun `Confirms code`() {
        every { codeRepositoryMock.findByCode(CODE) } returns UserConfirmationCode(USER_ID, CODE)
        every { codeRepositoryMock.deleteByCode(CODE) } just Runs
        every { tokenGeneratorMock.generateAndStore(USER_ID) } just Runs

        identityConfirmationService.confirmCode(CODE)

        verify { tokenGeneratorMock.generateAndStore(USER_ID) }
    }

    @Test
    fun `Deletes confirmed code`() {
        every { codeRepositoryMock.findByCode(CODE) } returns UserConfirmationCode(USER_ID, CODE)
        every { codeRepositoryMock.deleteByCode(CODE) } just Runs
        every { tokenGeneratorMock.generateAndStore(USER_ID) } just Runs

        identityConfirmationService.confirmCode(CODE)

        verify { codeRepositoryMock.deleteByCode(CODE) }
    }

    @Test
    fun `Checks if user exists`() {
        every { codeRepositoryMock.existsByCode(CODE) } returns true

        assertTrue(identityConfirmationService.doesUserByCodeExist(CODE))
    }

    @Test
    fun `Gets user id by confirmation code`() {
        every { codeRepositoryMock.findByCode(CODE) } returns UserConfirmationCode(USER_ID, CODE)

        val responseId = identityConfirmationService.getUserIdByCode(CODE)

        assertSame(USER_ID, responseId)
    }

    @Test
    fun `Checks if token exists`() {
        every { tokenGeneratorMock.doesTokenExist(TOKEN) } returns true

        assertTrue(identityConfirmationService.doesTokenExist(TOKEN))
    }

    @Test
    fun `Gets token by user id`() {
        every { tokenGeneratorMock.getTokenById(USER_ID) } returns TOKEN

        val responseToken = identityConfirmationService.getTokenById(USER_ID)

        assertSame(TOKEN, responseToken)
    }

    @Test
    fun `Gets user id by token`() {
        every { tokenGeneratorMock.getUserIdByToken(TOKEN) } returns USER_ID

        val responseId = identityConfirmationService.getUserIdByToken(TOKEN)

        assertSame(USER_ID, responseId)
    }

    companion object {
        const val USER_ID = "userId"
        const val CODE = "123456"
        const val TOKEN = "4a5sd4f-a654a65d4fa65d"
    }
}
