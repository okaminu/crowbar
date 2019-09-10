package lt.boldadmin.crowbar.test.acceptance.step

import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.mockk.*
import io.mockk.impl.annotations.MockK
import lt.boldadmin.crowbar.IdentityConfirmation
import lt.boldadmin.crowbar.api.ConfirmationMessageGateway
import lt.boldadmin.crowbar.generator.ConfirmationCodeGenerator
import lt.boldadmin.crowbar.generator.TokenGenerator
import lt.boldadmin.crowbar.repository.UserConfirmationCodeRepository
import lt.boldadmin.crowbar.repository.UserTokenRepository
import lt.boldadmin.crowbar.test.acceptance.holder.TokenHolder
import lt.boldadmin.crowbar.test.acceptance.holder.UserHolder
import lt.boldadmin.crowbar.type.entity.UserConfirmationCode
import lt.boldadmin.crowbar.type.entity.UserToken
import org.junit.jupiter.api.Assertions.assertNotNull
import java.util.*

class ConfirmationStep {

    @MockK
    private lateinit var confirmationMessageGatewayMock: ConfirmationMessageGateway

    @MockK
    private lateinit var codeRepositoryMock: UserConfirmationCodeRepository

    @MockK
    private lateinit var userTokenRepositoryMock: UserTokenRepository

    @MockK
    private lateinit var codeGeneratorStub: ConfirmationCodeGenerator

    private lateinit var tokenGenerator: TokenGenerator

    private lateinit var tokenHolder: TokenHolder

    private lateinit var userHolder: UserHolder

    private lateinit var identityConfirmation: IdentityConfirmation

    @Before
    fun `Set up`() {
        MockKAnnotations.init(this)
        tokenHolder = TokenHolder()
        userHolder = UserHolder()
        tokenGenerator = TokenGenerator(userTokenRepositoryMock)
        identityConfirmation =
            IdentityConfirmation(
                codeRepositoryMock,
                confirmationMessageGatewayMock, codeGeneratorStub, tokenGenerator
            )
    }

    @Given("^user exists$")
    fun `user exists`() {
        userHolder.userId = USER_ID
    }

    @When("^I provide confirmation address$")
    fun `I provide confirmation address`() {
        every { codeRepositoryMock.findById(USER_ID) } returns Optional.of(
            UserConfirmationCode(USER_ID, CONFIRMATION_CODE)
        )
        every { codeGeneratorStub.generateAndStore(USER_ID) } just Runs
        every { confirmationMessageGatewayMock.send(any(), ADDRESS) } just Runs
        identityConfirmation.sendConfirmationCode(userHolder.userId!!, ADDRESS)
    }

    @When("^I provide correct confirmation code$")
    fun `I provide correct confirmation code`() {
        every { codeRepositoryMock.findByCode(CONFIRMATION_CODE) } returns UserConfirmationCode(
            USER_ID, CONFIRMATION_CODE
        )
        every { userTokenRepositoryMock.findById(USER_ID) } returns Optional.of(UserToken(USER_ID, "token"))
        every { codeRepositoryMock.deleteByCode(CONFIRMATION_CODE) } just Runs
        every { userTokenRepositoryMock.existsByToken(any()) } returns false
        every { userTokenRepositoryMock.save(any()) } just Runs

        identityConfirmation.confirmCode(CONFIRMATION_CODE)

        tokenHolder.apply { token = identityConfirmation.getTokenById(USER_ID) }
    }

    @Then("^user identity is confirmed$")
    fun `user is confirmed`() {
        assertNotNull(tokenHolder.token)
    }

    @Then("^I receive confirmation code$")
    fun `I receive confirmation code`() {
        verify { confirmationMessageGatewayMock.send(any(), eq(ADDRESS)) }
    }

    companion object {
        const val USER_ID = "46afg4df4g5ds46g5s"
        const val CONFIRMATION_CODE = "123456"
        const val ADDRESS = "+37012345678"
    }
}
