package lt.tlistas.crowbar.test.service

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.crowbar.exception.IncorrectConfirmationCodeException
import lt.tlistas.crowbar.repository.UserTokenRepository
import lt.tlistas.crowbar.repository.ConfirmationCodeRepository
import lt.tlistas.crowbar.service.TokenService
import lt.tlistas.crowbar.type.entity.UserToken
import lt.tlistas.crowbar.type.entity.ConfirmationCode
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class TokenServiceTest {

    @Mock
    private lateinit var confirmationCodeRepositoryMock: ConfirmationCodeRepository

    @Mock
    private lateinit var userTokenRepositoryMock: UserTokenRepository

    private lateinit var tokenService: TokenService

    @Rule
    @JvmField
    val expectedException = ExpectedException.none()!!

    @Before
    fun `Set up`() {
        tokenService = TokenService(confirmationCodeRepositoryMock, userTokenRepositoryMock)
    }

    @Test
    fun `Returns token if confirmation code is correct`() {
        doReturn(ConfirmationCode(USER_ID, CONFIRMATION_CODE))
                .`when`(confirmationCodeRepositoryMock).findByCode(CONFIRMATION_CODE)
        doReturn(true).`when`(confirmationCodeRepositoryMock).existsByCode(CONFIRMATION_CODE)

        val token = tokenService.confirmCode(CONFIRMATION_CODE)

        assertTrue(token.isNotEmpty())
        verify(confirmationCodeRepositoryMock).existsByCode(any())
        verify(confirmationCodeRepositoryMock).findByCode(any())
        verify(confirmationCodeRepositoryMock).delete(any())
    }

    @Test
    fun `Throws exception when confirmation code is incorrect`() {
        expectedException.expect(IncorrectConfirmationCodeException::class.java)
        doReturn(false).`when`(confirmationCodeRepositoryMock).existsByCode(CONFIRMATION_CODE)

        tokenService.confirmCode(CONFIRMATION_CODE)
    }

    @Test
    fun `Generates token`() {
        doReturn(false).`when`(userTokenRepositoryMock).existsByToken(any())

        val token = tokenService.generate()

        verify(userTokenRepositoryMock).existsByToken(any())
        assertTrue(token.length == 36)
    }

    @Test
    fun `Generates token until unique one is found`() {
        doReturn(true).doReturn(false).`when`(userTokenRepositoryMock).existsByToken(any())

        val token = tokenService.generate()

        verify(userTokenRepositoryMock, times(2)).existsByToken(any())
        assertTrue(token.length == 36)
    }

    @Test
    fun `Checks if token exists`() {
        doReturn(true).`when`(userTokenRepositoryMock).existsByToken(TOKEN)

        val exists = tokenService.tokenExists(TOKEN)

        assertTrue(exists)
        verify(userTokenRepositoryMock).existsByToken(TOKEN)
    }

    @Test
    fun `Gets userId by token`() {
        val authentication = UserToken()
        doReturn(authentication).`when`(userTokenRepositoryMock).findByToken(TOKEN)

        val userId = tokenService.getUserId(TOKEN)

        assertEquals(authentication.id, userId)
        verify(userTokenRepositoryMock).findByToken(TOKEN)
    }

    companion object {
        private const val USER_ID = "as54sad65f4as6d5f"
        private const val CONFIRMATION_CODE = "123456"
        private const val TOKEN = "5d4fa6sd5f4a6sd5f46"
    }
}