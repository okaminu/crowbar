package lt.tlistas.crowbar.test.service

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.crowbar.exception.ConfirmationCodeNotFoundException
import lt.tlistas.crowbar.repository.ConfirmationRepository
import lt.tlistas.crowbar.repository.RequestRepository
import lt.tlistas.crowbar.service.ConfirmationService
import lt.tlistas.crowbar.type.entity.Confirmation
import lt.tlistas.crowbar.type.entity.Request
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
class AuthenticationServiceTest {

    @Mock
    private lateinit var requestRepositoryMock: RequestRepository

    @Mock
    private lateinit var confirmationRepositoryMock: ConfirmationRepository

    private lateinit var confirmationService: ConfirmationService

    @Rule
    @JvmField
    val expectedException = ExpectedException.none()!!

    @Before
    fun `Set up`() {
        confirmationService = ConfirmationService(requestRepositoryMock, confirmationRepositoryMock)
    }

    @Test
    fun `Returns token if confirmation code is correct`() {
        doReturn(Request(USER_ID, CONFIRMATION_CODE))
                .`when`(requestRepositoryMock).findByCode(CONFIRMATION_CODE)
        doReturn(true).`when`(requestRepositoryMock).existsByCode(CONFIRMATION_CODE)

        val token = confirmationService.confirmCode(CONFIRMATION_CODE)

        assertTrue(token.isNotEmpty())
        verify(requestRepositoryMock).existsByCode(any())
        verify(requestRepositoryMock).findByCode(any())
        verify(requestRepositoryMock).delete(any())
    }

    @Test
    fun `Throws exception when confirmation code is incorrect`() {
        expectedException.expect(ConfirmationCodeNotFoundException::class.java)
        doReturn(false).`when`(requestRepositoryMock).existsByCode(CONFIRMATION_CODE)

        confirmationService.confirmCode(CONFIRMATION_CODE)
    }

    @Test
    fun `Generates token`() {
        doReturn(false).`when`(confirmationRepositoryMock).existsByToken(any())

        val token = confirmationService.generate()

        verify(confirmationRepositoryMock).existsByToken(any())
        assertTrue(token.length == 36)
    }

    @Test
    fun `Generates token until unique one is found`() {
        doReturn(true).doReturn(false).`when`(confirmationRepositoryMock).existsByToken(any())

        val token = confirmationService.generate()

        verify(confirmationRepositoryMock, times(2)).existsByToken(any())
        assertTrue(token.length == 36)
    }

    @Test
    fun `Checks if token exists`() {
        doReturn(true).`when`(confirmationRepositoryMock).existsByToken(TOKEN)

        val exists = confirmationService.tokenExists(TOKEN)

        assertTrue(exists)
        verify(confirmationRepositoryMock).existsByToken(TOKEN)
    }

    @Test
    fun `Gets userId by token`() {
        val authentication = Confirmation()
        doReturn(authentication).`when`(confirmationRepositoryMock).findByToken(TOKEN)

        val userId = confirmationService.getUserId(TOKEN)

        assertEquals(authentication.id, userId)
        verify(confirmationRepositoryMock).findByToken(TOKEN)
    }

    companion object {
        private const val USER_ID = "as54sad65f4as6d5f"
        private const val CONFIRMATION_CODE = "123456"
        private const val TOKEN = "5d4fa6sd5f4a6sd5f46"
    }
}