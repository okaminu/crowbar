package lt.tlistas.crowbar.test.acceptance.step

import com.nhaarman.mockito_kotlin.*
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import lt.tlistas.crowbar.IdentityConfirmation
import lt.tlistas.crowbar.api.ConfirmationMessageGateway
import lt.tlistas.crowbar.repository.UserConfirmationCodeRepository
import lt.tlistas.crowbar.repository.UserTokenRepository
import lt.tlistas.crowbar.test.acceptance.holder.TokenHolder
import lt.tlistas.crowbar.test.acceptance.holder.UserHolder
import lt.tlistas.crowbar.type.entity.UserConfirmationCode
import lt.tlistas.crowbar.type.entity.UserToken
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.*
import kotlin.test.assertNotNull

class ConfirmationStep {

    @Mock
    private lateinit var confirmationMessageGatewayMock: ConfirmationMessageGateway

    @Mock
    private lateinit var codeRepositoryMock: UserConfirmationCodeRepository

    @Mock
    private lateinit var tokenRepositoryMock: UserTokenRepository

    private lateinit var tokenHolder: TokenHolder

    private lateinit var userHolder: UserHolder

    private lateinit var identityConfirmation: IdentityConfirmation

    @Before
    fun `Set up`() {
        MockitoAnnotations.initMocks(this)
        tokenHolder = TokenHolder()
        userHolder = UserHolder()
        identityConfirmation =
                IdentityConfirmation(
                    codeRepositoryMock, tokenRepositoryMock,
                    confirmationMessageGatewayMock, mock(), mock()
                )
    }

    @Given("^user exists$")
    fun `user exists`() {
        userHolder.userId = USER_ID
    }

    @When("^I provide confirmation address$")
    fun `I provide confirmation address`() {
        doReturn(Optional.of(UserConfirmationCode(USER_ID, CONFIRMATION_CODE)))
            .`when`(codeRepositoryMock).findById(USER_ID)
        identityConfirmation.sendConfirmationCode(userHolder.userId!!, ADDRESS)
    }

    @When("^I provide correct confirmation code$")
    fun `I provide correct confirmation code`() {
        doReturn(UserConfirmationCode(USER_ID, CONFIRMATION_CODE))
            .`when`(codeRepositoryMock).findByCode(CONFIRMATION_CODE)
        doReturn(Optional.of(UserToken(USER_ID, "token"))).`when`(tokenRepositoryMock).findById(USER_ID)

        identityConfirmation.confirmCode(CONFIRMATION_CODE)

        tokenHolder.apply {
            token = identityConfirmation.getTokenById(USER_ID)
        }
    }

    @Then("^user identity is confirmed$")
    fun `user is confirmed`() {
        assertNotNull(tokenHolder.token)
    }

    @Then("^I receive confirmation code$")
    fun `I receive confirmation code`() {
        verify(confirmationMessageGatewayMock).send(any(), eq(ADDRESS))
    }

    companion object {
        const val USER_ID = "46afg4df4g5ds46g5s"
        const val CONFIRMATION_CODE = "123456"
        const val ADDRESS = "+37012345678"
    }
}