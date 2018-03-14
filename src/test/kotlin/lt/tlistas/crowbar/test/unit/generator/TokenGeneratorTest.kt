package lt.tlistas.crowbar.test.unit.generator

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import junit.framework.Assert.assertSame
import lt.tlistas.crowbar.generator.TokenGenerator
import lt.tlistas.crowbar.repository.UserTokenRepository
import lt.tlistas.crowbar.test.unit.IdentityConfirmationTest.Companion.TOKEN
import lt.tlistas.crowbar.type.entity.UserToken
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.test.assertTrue

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