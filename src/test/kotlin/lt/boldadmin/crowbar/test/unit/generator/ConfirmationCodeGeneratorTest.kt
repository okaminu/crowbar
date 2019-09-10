package lt.boldadmin.crowbar.test.unit.generator

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import lt.boldadmin.crowbar.generator.ConfirmationCodeGenerator
import lt.boldadmin.crowbar.repository.UserConfirmationCodeRepository
import lt.boldadmin.crowbar.type.entity.UserConfirmationCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ConfirmationCodeGeneratorTest {

    @MockK
    private lateinit var repositoryMock: UserConfirmationCodeRepository

    private lateinit var confirmationCodeGenerator: ConfirmationCodeGenerator

    @BeforeEach
    fun `Set up`() {
        confirmationCodeGenerator = ConfirmationCodeGenerator(repositoryMock)
    }

    @Test
    fun `Stores confirmation code`() {
        every { repositoryMock.existsByCode(any()) } returns false
        every { repositoryMock.save(any()) } just Runs

        confirmationCodeGenerator.generateAndStore(USER_ID)

        verify { repositoryMock.save(any()) }
    }

    @Test
    fun `Generates confirmation code`() {
        val confirmationCode = CapturingSlot<String>()
        every { repositoryMock.existsByCode(capture(confirmationCode)) } returns false
        every { repositoryMock.save(any()) } just Runs

        confirmationCodeGenerator.generateAndStore(USER_ID)

        assertTrue(confirmationCode.captured.isNotEmpty())
    }

    @Test
    fun `Generates confirmation code until unique one is found`() {
        val confirmationCodes = mutableListOf<String>()
        val savedCode = CapturingSlot<UserConfirmationCode>()
        every { repositoryMock.existsByCode(capture(confirmationCodes)) } returns true andThen false
        every { repositoryMock.save(capture(savedCode)) } just Runs

        confirmationCodeGenerator.generateAndStore(USER_ID)

        assertTrue(confirmationCodes[0] != confirmationCodes[1])
        assertEquals(USER_ID, savedCode.captured.id)
        assertEquals(confirmationCodes[1], savedCode.captured.code)
    }

    companion object {
        val USER_ID = "userId"
    }
}
