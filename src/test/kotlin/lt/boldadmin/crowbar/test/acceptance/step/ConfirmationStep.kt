package lt.boldadmin.crowbar.test.acceptance.step

import com.nhaarman.mockitokotlin2.*
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import lt.boldadmin.crowbar.IdentityConfirmation
import lt.boldadmin.crowbar.api.ConfirmationMessageGateway
import lt.boldadmin.crowbar.generator.TokenGenerator
import lt.boldadmin.crowbar.repository.UserConfirmationCodeRepository
import lt.boldadmin.crowbar.repository.UserTokenRepository
import lt.boldadmin.crowbar.test.acceptance.holder.TokenHolder
import lt.boldadmin.crowbar.test.acceptance.holder.UserHolder
import lt.boldadmin.crowbar.type.entity.UserConfirmationCode
import lt.boldadmin.crowbar.type.entity.UserToken
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
    private lateinit var userTokenRepositoryMock: UserTokenRepository

    private lateinit var tokenGenerator: TokenGenerator

    private lateinit var tokenHolder: TokenHolder

    private lateinit var userHolder: UserHolder

    private lateinit var identityConfirmation: IdentityConfirmation

    @Before
    fun `Set up`() {
        MockitoAnnotations.initMocks(this)
        tokenHolder = TokenHolder()
        userHolder = UserHolder()
        tokenGenerator = TokenGenerator(userTokenRepositoryMock)
        identityConfirmation =
                IdentityConfirmation(
                        codeRepositoryMock,
                        confirmationMessageGatewayMock, mock(), tokenGenerator
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
        doReturn(Optional.of(UserToken(USER_ID, "token"))).`when`(userTokenRepositoryMock).findById(USER_ID)

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