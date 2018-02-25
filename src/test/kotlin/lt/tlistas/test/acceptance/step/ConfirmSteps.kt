package lt.tlistas.test.acceptance.step

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import cucumber.api.java8.En
import lt.tlistas.mobile.number.confirmation.api.ConfirmationMessageGateway
import lt.tlistas.mobile.number.confirmation.repository.ConfirmationRepository
import lt.tlistas.mobile.number.confirmation.service.AuthenticationService
import lt.tlistas.mobile.number.confirmation.service.ConfirmationService
import lt.tlistas.mobile.number.confirmation.type.entity.Confirmation
import lt.tlistas.test.acceptance.holder.AuthenticationHolder
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlin.test.assertNotNull

class ConfirmSteps : En {

    @Mock
    private lateinit var confirmationMessageGatewayMock: ConfirmationMessageGateway

    @Mock
    private lateinit var confirmationRepositoryMock: ConfirmationRepository

    private lateinit var authenticationHolder: AuthenticationHolder

    private lateinit var confirmationService: ConfirmationService

    private lateinit var authenticationService: AuthenticationService

    @Before
    fun `Set up`() {
        MockitoAnnotations.initMocks(this)
        authenticationHolder = AuthenticationHolder()
        confirmationService = ConfirmationService(mock(), confirmationMessageGatewayMock)
        authenticationService = AuthenticationService(confirmationRepositoryMock, mock())

    }

    @Given("^user exists$")
    fun `user exists`() {
        authenticationHolder.userId = USER_ID
    }

    @When("^I provide confirmation address$")
    fun `I provide confirmation address`() {
        confirmationService.sendConfirmation(MOBILE_NUMBER, authenticationHolder.userId!!)
    }

    @When("^I provide correct confirmation code$")
    fun `I provide correct confirmation code`() {
        doReturn(true).`when`(confirmationRepositoryMock).existsByCode(CONFIRMATION_CODE)
        doReturn(Confirmation()).`when`(confirmationRepositoryMock).findByCode(CONFIRMATION_CODE)

        authenticationHolder.token = authenticationService.getAuthenticationToken(CONFIRMATION_CODE)
    }

    @Then("^user is confirmed$")
    fun `user is confirmed`() {
        assertNotNull(authenticationHolder.token)
    }

    @Then("^I receive confirmation code$")
    fun `I receive confirmation code`() {
        verify(confirmationMessageGatewayMock).send(any(), any())
    }

    companion object {
        const val USER_ID = "46afg4df4g5ds46g5s"
        const val CONFIRMATION_CODE = "123456"
        const val MOBILE_NUMBER = "+370000000"
    }
}