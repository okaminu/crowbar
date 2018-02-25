package lt.tlistas.mobile.number.confirmation.test.confirmation

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.mobile.number.confirmation.api.exception.InvalidConfirmationCodeException
import lt.tlistas.mobile.number.confirmation.repository.AuthenticationRepository
import lt.tlistas.mobile.number.confirmation.repository.ConfirmationRepository
import lt.tlistas.mobile.number.confirmation.service.AuthenticationService
import lt.tlistas.mobile.number.confirmation.type.entity.Authentication
import lt.tlistas.mobile.number.confirmation.type.entity.Confirmation
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class AuthenticationServiceTest {

    @Mock
    private lateinit var confirmationRepositoryMock: ConfirmationRepository

    @Mock
    private lateinit var authenticationRepositoryMock: AuthenticationRepository

    private lateinit var authenticationService: AuthenticationService

    @Rule
    @JvmField
    val expectedException = ExpectedException.none()!!

    @Before
    fun `Set up`() {
        authenticationService = AuthenticationService(confirmationRepositoryMock, authenticationRepositoryMock)
    }

    @Test
    fun `Returns token on confirmation code validation`() {
        val code = "123456"
        doReturn(Confirmation("0asd5as", code))
                .`when`(confirmationRepositoryMock).findByCode(code)
        doReturn(true).`when`(confirmationRepositoryMock).existsByCode(code)

        val token = authenticationService.getAuthenticationToken(code)

        assertFalse(token.isEmpty())
        verify(confirmationRepositoryMock).delete(any())
        verify(confirmationRepositoryMock).findByCode(any())
        verify(confirmationRepositoryMock).existsByCode(any())
    }

    @Test
    fun `Throws exception when code is invalid`() {
        expectedException.expect(InvalidConfirmationCodeException::class.java)
        val code = "123456"
        doReturn(false).`when`(confirmationRepositoryMock).existsByCode(code)

        authenticationService.getAuthenticationToken(code)
    }

    @Test
    fun `Generates confirmation token`() {
        doReturn(false).`when`(authenticationRepositoryMock).existsByToken(any())

        val token = authenticationService.generate()

        verify(authenticationRepositoryMock).existsByToken(any())
        assertTrue(token.length == 36)
    }

    @Test
    fun `Generates confirmation token until unique one is found`() {
        doReturn(true).doReturn(false).`when`(authenticationRepositoryMock).existsByToken(any())

        val token = authenticationService.generate()

        verify(authenticationRepositoryMock, times(2)).existsByToken(any())
        assertTrue(token.length == 36)
    }

    @Test
    fun `Checks if token exists`() {
        val token = "465af4s6d5f4a5"
        doReturn(true).`when`(authenticationRepositoryMock).existsByToken(token)

        val exists = authenticationService.tokenExists(token)

        assertTrue(exists)
        verify(authenticationRepositoryMock).existsByToken(token)
    }

    @Test
    fun `Gets userId by token`() {
        val token = "sdfsdf55a4sd6af54d"
        val authentication = Authentication()
        doReturn(authentication).`when`(authenticationRepositoryMock).findByToken(token)

        val userId = authenticationService.getUserId(token)

        assertEquals(authentication.id, userId)
        verify(authenticationRepositoryMock).findByToken(token)
    }
}