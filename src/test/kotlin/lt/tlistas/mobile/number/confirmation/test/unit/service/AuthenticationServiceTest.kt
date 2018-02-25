package lt.tlistas.mobile.number.confirmation.test.unit.service

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.mobile.number.confirmation.api.exception.ConfirmationCodeNotFoundException
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
    fun `Returns token if confirmation code is correct`() {
        doReturn(Confirmation(USER_ID, CONFIRMATION_CODE))
                .`when`(confirmationRepositoryMock).findByCode(CONFIRMATION_CODE)
        doReturn(true).`when`(confirmationRepositoryMock).existsByCode(CONFIRMATION_CODE)

        val token = authenticationService.getAuthenticationToken(CONFIRMATION_CODE)

        assertFalse(token.isEmpty())
        verify(confirmationRepositoryMock).existsByCode(any())
        verify(confirmationRepositoryMock).findByCode(any())
        verify(confirmationRepositoryMock).delete(any())
    }

    @Test
    fun `Throws exception when code is incorrect`() {
        expectedException.expect(ConfirmationCodeNotFoundException::class.java)
        doReturn(false).`when`(confirmationRepositoryMock).existsByCode(CONFIRMATION_CODE)

        authenticationService.getAuthenticationToken(CONFIRMATION_CODE)
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
        doReturn(true).`when`(authenticationRepositoryMock).existsByToken(TOKEN)

        val exists = authenticationService.tokenExists(TOKEN)

        assertTrue(exists)
        verify(authenticationRepositoryMock).existsByToken(TOKEN)
    }

    @Test
    fun `Gets userId by token`() {
        val authentication = Authentication()
        doReturn(authentication).`when`(authenticationRepositoryMock).findByToken(TOKEN)

        val userId = authenticationService.getUserId(TOKEN)

        assertEquals(authentication.id, userId)
        verify(authenticationRepositoryMock).findByToken(TOKEN)
    }

    companion object {
        private const val USER_ID = "as54sad65f4as6d5f"
        private const val CONFIRMATION_CODE = "123456"
        private const val TOKEN = "5d4fa6sd5f4a6sd5f46"
    }
}