package lt.tlistas.crowbar.test.service

import com.nhaarman.mockito_kotlin.*
import lt.tlistas.crowbar.api.ConfirmationMessageGateway
import lt.tlistas.crowbar.repository.RequestRepository
import lt.tlistas.crowbar.service.ConfirmationCodeSender
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class ConfirmationCodeSenderTest {

    @Mock
    private lateinit var requestRepositoryMock: RequestRepository

    @Mock
    private lateinit var confirmationMessageMock: ConfirmationMessageGateway

    private lateinit var confirmationCodeSender: ConfirmationCodeSender

    @Rule
    @JvmField
    val expectedException = ExpectedException.none()!!

    @Before
    fun `Set up`() {
        confirmationCodeSender = ConfirmationCodeSender(requestRepositoryMock, confirmationMessageMock)
    }

    @Test
    fun `Sends confirmation code to the user's address`() {
        val address = "+3712345678"

        confirmationCodeSender.send("userId", address)

        verify(confirmationMessageMock).send(any(), eq(address))
    }

    @Test
    fun `Generates confirmation code`() {
        doReturn(false).`when`(requestRepositoryMock).existsByCode(any())

        assertTrue(confirmationCodeSender.generate().length == 6)
        verify(requestRepositoryMock).existsByCode(any())
    }

    @Test
    fun `Generates confirmation code until unique one is found`() {
        doReturn(true).doReturn(false).`when`(requestRepositoryMock).existsByCode(any())

        assertTrue(confirmationCodeSender.generate().length == 6)
        verify(requestRepositoryMock, times(2)).existsByCode(any())
    }
}