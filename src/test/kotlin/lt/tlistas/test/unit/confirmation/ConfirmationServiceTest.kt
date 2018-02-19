package lt.tlistas.test.unit.confirmation

import com.nhaarman.mockito_kotlin.*
import lt.tlistas.mobile.number.confirmation.SmsGateway
import lt.tlistas.mobile.number.confirmation.exception.InvalidConfirmationCodeException
import lt.tlistas.mobile.number.confirmation.repository.ConfirmationRepository
import lt.tlistas.core.service.CollaboratorService
import lt.tlistas.mobile.number.confirmation.service.ConfirmationService
import lt.tlistas.core.type.entity.Collaborator
import lt.tlistas.mobile.number.confirmation.type.entity.Confirmation
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
class ConfirmationServiceTest {

    @Mock
    private lateinit var confirmationRepositoryMock: ConfirmationRepository

    @Mock
    private lateinit var collaboratorServiceMock: CollaboratorService

    @Mock
    private lateinit var smsGatewayMock: SmsGateway

    private lateinit var confirmationService: ConfirmationService

    @Rule
    @JvmField
    val expectedException = ExpectedException.none()!!

    @Before
    fun `Set up`() {
        confirmationService = ConfirmationService(confirmationRepositoryMock,
                collaboratorServiceMock, smsGatewayMock)
    }

    @Test
    fun `Sends confirmation code to the collaborator's number`() {
        val mobileNumber = "+3712345678"
        doReturn(Collaborator()).`when`(collaboratorServiceMock).getByMobileNumber(mobileNumber)
        confirmationService.sendConfirmation(mobileNumber)

        verify(smsGatewayMock).send(any(), eq(mobileNumber))
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

    @Test
    fun `Removes confirmation if code is valid`() {
        val code = "123456"
        doReturn(true).`when`(confirmationRepositoryMock).existsByCode(code)

        confirmationService.removeValidConfirmation(code)

        verify(confirmationRepositoryMock).existsByCode(code)
        verify(confirmationRepositoryMock).deleteByCode(code)

    }

    @Test
    fun `Throws exception when confirmation code is invalid`() {
        val code = "123456"
        expectedException.expect(InvalidConfirmationCodeException::class.java)
        doReturn(false).`when`(confirmationRepositoryMock).existsByCode(code)

        confirmationService.removeValidConfirmation(code)
    }

    @Test
    fun `Finds confirmation by code`() {
        val code = "123456"
        val confirmation = Confirmation()
        doReturn(confirmation).`when`(confirmationRepositoryMock).findByCode(code)

        val result = confirmationService.findByCode(code)

        assertEquals(confirmation, result)
        verify(confirmationRepositoryMock).findByCode(code)
    }

    @Test
    fun `Checks if confirmation code exists`() {
        val code = "123456"
        doReturn(true).`when`(confirmationRepositoryMock).existsByCode(code)

        val exists = confirmationService.confirmationCodeExists(code)

        assertTrue(exists)
        verify(confirmationRepositoryMock).existsByCode(code)
    }
}