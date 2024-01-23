import com.example.cs308_mobileapplication.pages.RegisterAct
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterActTest {

    private val registerAct = RegisterAct()

    @Test
    fun `isValidEmail returns true for valid email`() {
        val validEmail = "test@example.com"
        assertTrue(registerAct.isValidEmail(validEmail))
    }

    @Test
    fun `isValidEmail returns false for invalid email`() {
        val invalidEmail = "test@example"
        assertFalse(registerAct.isValidEmail(invalidEmail))
    }

    @Test
    fun `isValidPassword returns true for valid password`() {
        val validPassword = "Password123!"
        assertTrue(registerAct.isValidPassword(validPassword))
    }

    @Test
    fun `isValidPassword returns false for password without special characters`() {
        val invalidPassword = "Password123"
        assertFalse(registerAct.isValidPassword(invalidPassword))
    }

    @Test
    fun `isValidPassword returns false for password without uppercase letters`() {
        val invalidPassword = "password123!"
        assertFalse(registerAct.isValidPassword(invalidPassword))
    }

    @Test
    fun `isValidPassword returns false for password without lowercase letters`() {
        val invalidPassword = "PASSWORD123!"
        assertFalse(registerAct.isValidPassword(invalidPassword))
    }

    @Test
    fun `isValidPassword returns false for password without digits`() {
        val invalidPassword = "Password!"
        assertFalse(registerAct.isValidPassword(invalidPassword))
    }

    @Test
    fun `isValidPassword returns false for too short password`() {
        val invalidPassword = "Pw1!"
        assertFalse(registerAct.isValidPassword(invalidPassword))
    }

    @Test
    fun `isValidPassword returns false for too long password`() {
        val invalidPassword = "P1!${"a".repeat(50)}"
        assertFalse(registerAct.isValidPassword(invalidPassword))
    }
}
