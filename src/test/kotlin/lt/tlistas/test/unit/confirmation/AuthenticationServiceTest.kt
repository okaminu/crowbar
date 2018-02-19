package lt.tlistas.test.unit.confirmation

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.mobile.number.confirmation.exception.AuthenticationException
import lt.tlistas.mobile.number.confirmation.repository.AuthenticationRepository
import lt.tlistas.mobile.number.confirmation.service.AuthenticationService
import lt.tlistas.mobile.number.confirmation.service.ConfirmationService
import lt.tlistas.mobile.number.confirmation.type.entity.Authentication
import lt.tlistas.core.type.entity.Collaborator
import lt.tlistas.mobile.number.confirmation.exception.InvalidConfirmationCodeException
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
    private lateinit var confirmationServiceMock: ConfirmationService

    @Mock
    private lateinit var repositoryMock: AuthenticationRepository

    private lateinit var authenticationService: AuthenticationService

    @Rule
    @JvmField
    val expectedException = ExpectedException.none()!!

    @Before
    fun `Set up`() {
        authenticationService = AuthenticationService(confirmationServiceMock, repositoryMock)
    }

    @Test
    fun `Returns token on confirmation code validation`() {
        val code = "123456"
        doReturn(Confirmation(Collaborator().apply { id = "0asd5as" }, code))
                .`when`(confirmationServiceMock).findByCode(code)
        doReturn(true).`when`(confirmationServiceMock).confirmationCodeExists(code)

        val token = authenticationService.getAuthenticationToken(code)

        assertFalse(token.isEmpty())
        verify(confirmationServiceMock).removeValidConfirmation(any())
        verify(confirmationServiceMock).findByCode(any())
        verify(confirmationServiceMock).confirmationCodeExists(any())
    }

    @Test
    fun `Throws exception when code is invalid`() {
        expectedException.expect(InvalidConfirmationCodeException::class.java)
        val code = "123456"
        doReturn(false).`when`(confirmationServiceMock).confirmationCodeExists(code)

        authenticationService.getAuthenticationToken(code)
    }

    @Test
    fun `Generates confirmation token`() {
        doReturn(false).`when`(repositoryMock).existsByToken(any())

        val token = authenticationService.generate()

        verify(repositoryMock).existsByToken(any())
        assertTrue(token.length == 36)
    }

    @Test
    fun `Generates confirmation token until unique one is found`() {
        doReturn(true).doReturn(false).`when`(repositoryMock).existsByToken(any())

        val token = authenticationService.generate()

        verify(repositoryMock, times(2)).existsByToken(any())
        assertTrue(token.length == 36)
    }

    @Test
    fun `Gets collaborator if user is authenticated`() {
        val token = "456f4ads6f5a4a5sd46f5"
        val authentication = Authentication()
        doReturn(true).`when`(repositoryMock).existsByToken(token)
        doReturn(authentication).`when`(repositoryMock).findByToken(token)

        val collaborator = authenticationService.getCollaboratorByToken(token)

        assertEquals(authentication.collaborator, collaborator)
        verify(repositoryMock).existsByToken(token)
        verify(repositoryMock).findByToken(token)
    }

    @Test
    fun `Throws exception if collaborator is not authenticated`() {
        expectedException.expect(AuthenticationException::class.java)
        doReturn(false).`when`(repositoryMock).existsByToken(any())

        authenticationService.getCollaboratorByToken("token")
    }
}