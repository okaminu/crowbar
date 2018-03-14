package lt.tlistas.crowbar.test.unit.generator

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.crowbar.generator.ConfirmationCodeGenerator
import lt.tlistas.crowbar.repository.UserConfirmationCodeRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ConfirmationCodeGeneratorTest {

    @Mock
    private lateinit var repositoryMock: UserConfirmationCodeRepository

    private lateinit var confirmationCodeGenerator: ConfirmationCodeGenerator

    @Rule
    @JvmField
    val expectedException = ExpectedException.none()!!

    @Before
    fun `Set up`() {
        confirmationCodeGenerator = ConfirmationCodeGenerator(repositoryMock)
    }

    @Test
    fun `Stores confirmation code`() {
        doReturn(false).`when`(repositoryMock).existsByCode(any())

        confirmationCodeGenerator.generateAndStore(USER_ID)

        verify(repositoryMock).save(any())
    }

    @Test
    fun `Generates confirmation code`() {
        doReturn(false).`when`(repositoryMock).existsByCode(any())

        confirmationCodeGenerator.generateAndStore(USER_ID)

        verify(repositoryMock).existsByCode(any())
    }

    @Test
    fun `Generates confirmation code until unique one is found`() {
        doReturn(true).doReturn(false).`when`(repositoryMock).existsByCode(any())

        confirmationCodeGenerator.generateAndStore(USER_ID)

        verify(repositoryMock, times(2)).existsByCode(any())
    }

    companion object {
        val USER_ID = "userId"
    }
}