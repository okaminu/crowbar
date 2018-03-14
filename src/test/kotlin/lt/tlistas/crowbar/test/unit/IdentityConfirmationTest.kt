package lt.tlistas.crowbar.test.unit

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.crowbar.IdentityConfirmation
import lt.tlistas.crowbar.api.ConfirmationMessageGateway
import lt.tlistas.crowbar.generator.ConfirmationCodeGenerator
import lt.tlistas.crowbar.generator.TokenGenerator
import lt.tlistas.crowbar.repository.UserConfirmationCodeRepository
import lt.tlistas.crowbar.repository.UserTokenRepository
import lt.tlistas.crowbar.type.entity.UserConfirmationCode
import lt.tlistas.crowbar.type.entity.UserToken
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class IdentityConfirmationTest {

    @Mock
    private lateinit var tokenRepositoryMock: UserTokenRepository

    @Mock
    private lateinit var codeRepositoryMock: UserConfirmationCodeRepository

    @Mock
    private lateinit var tokenGeneratorMock: TokenGenerator

    @Mock
    private lateinit var codeGeneratorMock: ConfirmationCodeGenerator

    @Mock
    private lateinit var messageGateway: ConfirmationMessageGateway

    private lateinit var identityConfirmationService: IdentityConfirmation
    @Rule
    @JvmField
    val expectedException = ExpectedException.none()!!

    @Before
    fun `Set up`() {
        identityConfirmationService = IdentityConfirmation(
            codeRepositoryMock, tokenRepositoryMock,
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
    fun `Checks if token exists`() {
        doReturn(true).`when`(tokenRepositoryMock).existsByToken(TOKEN)

        assertTrue(identityConfirmationService.doesTokenExist(TOKEN))
        verify(tokenRepositoryMock).existsByToken(TOKEN)
    }

    @Test
    fun `Checks if user exists`() {
        doReturn(true).`when`(codeRepositoryMock).existsByCode(CODE)

        assertTrue(identityConfirmationService.doesUserByCodeExist(CODE))
        verify(codeRepositoryMock).existsByCode(CODE)
    }

    @Test
    fun `Gets token by user id`() {
        doReturn(Optional.of(UserToken(USER_ID, TOKEN))).`when`(tokenRepositoryMock).findById(USER_ID)

        val responseToken = identityConfirmationService.getTokenById(USER_ID)

        assertSame(TOKEN, responseToken)
        verify(tokenRepositoryMock).findById(USER_ID)
    }

    @Test
    fun `Gets user id by token`() {
        doReturn(UserToken(USER_ID, TOKEN)).`when`(tokenRepositoryMock).findByToken(TOKEN)

        val responseId = identityConfirmationService.getUserIdByToken(TOKEN)

        assertSame(USER_ID, responseId)
        verify(tokenRepositoryMock).findByToken(TOKEN)
    }

    @Test
    fun `Gets user id by confirmation code`() {
        doReturn(UserConfirmationCode(USER_ID, CODE)).`when`(codeRepositoryMock).findByCode(CODE)

        val responseId = identityConfirmationService.getUserIdByCode(CODE)

        assertSame(USER_ID, responseId)
        verify(codeRepositoryMock).findByCode(CODE)
    }


    companion object {
        val USER_ID = "userId"
        val CODE = "123456"
        val TOKEN = "4a5sd4f-a654a65d4fa65d"
    }
}