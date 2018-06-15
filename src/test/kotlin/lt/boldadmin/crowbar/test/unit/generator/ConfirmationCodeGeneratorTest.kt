package lt.boldadmin.crowbar.test.unit.generator

import com.nhaarman.mockito_kotlin.*
import lt.boldadmin.crowbar.generator.ConfirmationCodeGenerator
import lt.boldadmin.crowbar.repository.UserConfirmationCodeRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class ConfirmationCodeGeneratorTest {

    @Mock
    private lateinit var repositoryMock: UserConfirmationCodeRepository

    private lateinit var confirmationCodeGenerator: ConfirmationCodeGenerator

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

        argumentCaptor<String>().apply {
            verify(repositoryMock).existsByCode(capture())
            assertTrue(firstValue.isNotEmpty())
        }
    }

    @Test
    fun `Generates confirmation code until unique one is found`() {
        doReturn(true).doReturn(false).`when`(repositoryMock).existsByCode(any())

        confirmationCodeGenerator.generateAndStore(USER_ID)

        argumentCaptor<String>().apply {
            verify(repositoryMock, times(2)).existsByCode(capture())
            assertTrue(firstValue != secondValue)
            verify(repositoryMock).save(check {
                assertEquals(USER_ID, it.id)
                assertEquals(secondValue, it.code)
            })
        }
    }

    companion object {
        val USER_ID = "userId"
    }
}