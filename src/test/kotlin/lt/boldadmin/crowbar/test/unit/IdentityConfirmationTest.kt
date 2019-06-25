package lt.boldadmin.crowbar.test.unit

import com.nhaarman.mockitokotlin2.*
import lt.boldadmin.crowbar.IdentityConfirmation
import lt.boldadmin.crowbar.api.ConfirmationMessageGateway
import lt.boldadmin.crowbar.generator.ConfirmationCodeGenerator
import lt.boldadmin.crowbar.generator.TokenGenerator
import lt.boldadmin.crowbar.repository.UserConfirmationCodeRepository
import lt.boldadmin.crowbar.test.unit.generator.TokenGeneratorTest
import lt.boldadmin.crowbar.type.entity.UserConfirmationCode
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class IdentityConfirmationTest {

    @Mock private lateinit var codeRepositoryMock: UserConfirmationCodeRepository

    @Mock private lateinit var tokenGeneratorMock: TokenGenerator

    @Mock private lateinit var codeGeneratorMock: ConfirmationCodeGenerator

    @Mock private lateinit var messageGateway: ConfirmationMessageGateway

    private lateinit var identityConfirmationService: IdentityConfirmation

    @Before
    fun `Set up`() {
        identityConfirmationService = IdentityConfirmation(
                codeRepositoryMock,
                messageGateway, codeGeneratorMock, tokenGeneratorMock
        )
    }

    @Test
    fun `Sends confirmation code`() {
        doReturn(Optional.of(UserConfirmationCode(USER_ID, CODE))).`when`(codeRepositoryMock).findById(USER_ID)

        identityConfirmationService.sendConfirmationCode(USER_ID, "mobileNumber")

        verify(codeGeneratorMock).generateAndStore("userId")
        verify(codeRepositoryMock).findById(USER_ID)
        verify(messageGateway).send(any(), eq("mobileNumber"))
    }

    @Test
    fun `Confirms code`() {
        doReturn(UserConfirmationCode(USER_ID, CODE)).`when`(codeRepositoryMock).findByCode(CODE)

        identityConfirmationService.confirmCode(CODE)

        verify(tokenGeneratorMock).generateAndStore(USER_ID)
    }

    @Test
    fun `Deletes confirmed code`() {
        doReturn(UserConfirmationCode(USER_ID, CODE)).`when`(codeRepositoryMock).findByCode(CODE)

        identityConfirmationService.confirmCode(CODE)

        verify(codeRepositoryMock).deleteByCode(CODE)
    }

    @Test
    fun `Checks if user exists`() {
        doReturn(true).`when`(codeRepositoryMock).existsByCode(CODE)

        assertTrue(identityConfirmationService.doesUserByCodeExist(CODE))
        verify(codeRepositoryMock).existsByCode(CODE)
    }

    @Test
    fun `Gets user id by confirmation code`() {
        doReturn(UserConfirmationCode(TokenGeneratorTest.USER_ID, CODE)).`when`(codeRepositoryMock).findByCode(CODE)

        val responseId = identityConfirmationService.getUserIdByCode(CODE)

        assertSame(TokenGeneratorTest.USER_ID, responseId)
        verify(codeRepositoryMock).findByCode(CODE)
    }

    @Test
    fun `Checks if token exists`() {
        doReturn(true).`when`(tokenGeneratorMock).doesTokenExist(TOKEN)

        assertTrue(identityConfirmationService.doesTokenExist(TOKEN))
        verify(tokenGeneratorMock).doesTokenExist(TOKEN)
    }

    @Test
    fun `Gets token by user id`() {
        doReturn(TOKEN).`when`(tokenGeneratorMock).getTokenById(TokenGeneratorTest.USER_ID)

        val responseToken = identityConfirmationService.getTokenById(TokenGeneratorTest.USER_ID)

        assertSame(TOKEN, responseToken)
        verify(tokenGeneratorMock).getTokenById(TokenGeneratorTest.USER_ID)
    }

    @Test
    fun `Gets user id by token`() {
        doReturn(TokenGeneratorTest.USER_ID).`when`(tokenGeneratorMock).getUserIdByToken(TOKEN)

        val responseId = identityConfirmationService.getUserIdByToken(TOKEN)

        assertSame(TokenGeneratorTest.USER_ID, responseId)
        verify(tokenGeneratorMock).getUserIdByToken(TOKEN)
    }

    companion object {
        const val USER_ID = "userId"
        const val CODE = "123456"
        const val TOKEN = "4a5sd4f-a654a65d4fa65d"
    }
}