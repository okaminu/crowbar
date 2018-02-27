package lt.tlistas.crowbar.test.service

import com.nhaarman.mockito_kotlin.*
import lt.tlistas.crowbar.api.ConfirmationMessageGateway
import lt.tlistas.crowbar.repository.ConfirmationRepository
import lt.tlistas.crowbar.service.ConfirmationService
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class ConfirmationServiceTest {

    @Mock
    private lateinit var confirmationRepositoryMock: ConfirmationRepository

    @Mock
    private lateinit var confirmationMessageMock: ConfirmationMessageGateway

    private lateinit var confirmationService: ConfirmationService

    @Rule
    @JvmField
    val expectedException = ExpectedException.none()!!

    @Before
    fun `Set up`() {
        confirmationService = ConfirmationService(confirmationRepositoryMock, confirmationMessageMock)
    }

    @Test
    fun `Sends confirmation code to the collaborator's number`() {
        val address = "+3712345678"

        confirmationService.sendConfirmation(address, "userId")

        verify(confirmationMessageMock).send(any(), eq(address))
    }

    @Test
    fun `Generates confirmation code`() {
        doReturn(false).`when`(confirmationRepositoryMock).existsByCode(any())

        assertTrue(confirmationService.generate().length == 6)
        verify(confirmationRepositoryMock).existsByCode(any())
    }

    @Test
    fun `Generates confirmation code until unique one is found`() {
        doReturn(true).doReturn(false).`when`(confirmationRepositoryMock).existsByCode(any())

        assertTrue(confirmationService.generate().length == 6)
        verify(confirmationRepositoryMock, times(2)).existsByCode(any())
    }
}