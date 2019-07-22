package lt.boldadmin.crowbar.test.unit.generator

import com.nhaarman.mockito_kotlin.*
import lt.boldadmin.crowbar.generator.TokenGenerator
import lt.boldadmin.crowbar.repository.UserTokenRepository
import lt.boldadmin.crowbar.test.unit.IdentityConfirmationTest.Companion.TOKEN
import lt.boldadmin.crowbar.type.entity.UserToken
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class TokenGeneratorTest {

    @Mock
    private lateinit var repositoryMock: UserTokenRepository

    private lateinit var tokenGenerator: TokenGenerator

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

        argumentCaptor<String>().apply {
            verify(repositoryMock).existsByToken(capture())
            assertTrue(firstValue.isNotEmpty())
        }
    }

    @Test
    fun `Generates authentication token until unique one is found`() {
        doReturn(true).doReturn(false).`when`(repositoryMock).existsByToken(any())

        tokenGenerator.generateAndStore(USER_ID)

        argumentCaptor<String>().apply {
            verify(repositoryMock, times(2)).existsByToken(capture())
            assertTrue(firstValue != secondValue)
            verify(repositoryMock).save(check {
                assertEquals(USER_ID, it.id)
                assertEquals(secondValue, it.token)
            })
        }
    }

    @Test
    fun `Checks if token exists`() {
        doReturn(true).`when`(repositoryMock).existsByToken(TOKEN)

        assertTrue(tokenGenerator.doesTokenExist(TOKEN))
        verify(repositoryMock).existsByToken(TOKEN)
    }

    @Test
    fun `Gets token by user id`() {
        doReturn(Optional.of(UserToken(USER_ID, TOKEN))).`when`(repositoryMock).findById(USER_ID)

        val responseToken = tokenGenerator.getTokenById(USER_ID)

        assertSame(TOKEN, responseToken)
        verify(repositoryMock).findById(USER_ID)
    }

    @Test
    fun `Gets user id by token`() {
        doReturn(UserToken(USER_ID, TOKEN)).`when`(repositoryMock).findByToken(TOKEN)

        val responseId = tokenGenerator.getUserIdByToken(TOKEN)

        assertSame(USER_ID, responseId)
        verify(repositoryMock).findByToken(TOKEN)
    }

    companion object {
        val USER_ID = "userId"
    }
}
