package lt.tlistas.crowbar.test.acceptance.step

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import cucumber.api.java8.En
import lt.tlistas.crowbar.api.ConfirmationMessageGateway
import lt.tlistas.crowbar.repository.RequestRepository
import lt.tlistas.crowbar.service.ConfirmationService
import lt.tlistas.crowbar.service.RequestService
import lt.tlistas.crowbar.test.acceptance.holder.AuthenticationHolder
import lt.tlistas.crowbar.test.acceptance.holder.UserHolder
import lt.tlistas.crowbar.type.entity.Request
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlin.test.assertNotNull

class ConfirmationStep : En {

    @Mock
    private lateinit var confirmationMessageGatewayMock: ConfirmationMessageGateway

    @Mock
    private lateinit var requestRepositoryMock: RequestRepository

    private lateinit var authenticationHolder: AuthenticationHolder

    private lateinit var userHolder: UserHolder

    private lateinit var requestService: RequestService

    private lateinit var confirmationService: ConfirmationService

    @Before
    fun `Set up`() {
        MockitoAnnotations.initMocks(this)
        authenticationHolder = AuthenticationHolder()
        userHolder = UserHolder()
        requestService = RequestService(mock(), confirmationMessageGatewayMock)
        confirmationService = ConfirmationService(requestRepositoryMock, mock())

    }

    @Given("^user exists$")
    fun `user exists`() {
        userHolder.userId = USER_ID
    }

    @When("^I provide confirmation address$")
    fun `I provide confirmation address`() {
        requestService.sendConfirmation(ADDRESS, userHolder.userId!!)
    }

    @When("^I provide correct confirmation code$")
    fun `I provide correct confirmation code`() {
        doReturn(true).`when`(requestRepositoryMock).existsByCode(CONFIRMATION_CODE)
        doReturn(Request()).`when`(requestRepositoryMock).findByCode(CONFIRMATION_CODE)

        authenticationHolder.apply {
            userId = userHolder.userId
            token = confirmationService.confirmCode(CONFIRMATION_CODE)
        }
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
        const val ADDRESS = "+37012345678"
    }
}