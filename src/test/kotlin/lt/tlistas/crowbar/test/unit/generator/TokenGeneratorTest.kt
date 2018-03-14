package lt.tlistas.crowbar.test.unit.generator

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.crowbar.generator.TokenGenerator
import lt.tlistas.crowbar.repository.UserTokenRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TokenGeneratorTest {

    @Mock
    private lateinit var repositoryMock: UserTokenRepository

    private lateinit var tokenGenerator: TokenGenerator
    @Rule
    @JvmField
    val expectedException = ExpectedException.none()!!

    @Before
    fun `Set up`() {
        tokenGenerator = TokenGenerator(repositoryMock)
    }

    @Test
    fun `Stores authentication token`() {
        doReturn(false).`when`(repositoryMock).existsByToken(any())

        tokenGenerator.generateAndStore(USER_ID)

        verify(repositoryMock).save(any())
    }

    @Test
    fun `Generates authentication token`() {
        doReturn(false).`when`(repositoryMock).existsByToken(any())

        tokenGenerator.generateAndStore(USER_ID)

        verify(repositoryMock).existsByToken(any())
    }

    @Test
    fun `Generates authentication token until unique one is found`() {
        doReturn(true).doReturn(false).`when`(repositoryMock).existsByToken(any())

        tokenGenerator.generateAndStore(USER_ID)

        verify(repositoryMock, times(2)).existsByToken(any())
    }

    companion object {
        val USER_ID = "userId"
    }
}