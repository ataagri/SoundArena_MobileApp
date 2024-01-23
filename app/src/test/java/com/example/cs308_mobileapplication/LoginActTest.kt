import com.example.cs308_mobileapplication.pages.LoginAct
import com.example.cs308_mobileapplication.data.LoginResponse
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import retrofit2.Response


class LoginActTest {
    private lateinit var loginAct: LoginAct

    @Before
    fun setUp() {
        loginAct = mock(LoginAct::class.java)
    }

    @Test
    fun `processLoginResponse returns NoUser when email not found`() {
        val response = Response.error<LoginResponse>(400,
            ResponseBody.create("application/json".toMediaTypeOrNull(), "{\"message\":\"email could not be found\"}"))
        assertEquals(LoginAct.LoginResult.NoUser, loginAct.processLoginResponse(response))
    }

    @Test
    fun `processLoginResponse returns Success on successful response`() {
        val mockResponse = Response.success(LoginResponse("token", "userId"))
        assertEquals(LoginAct.LoginResult.Success, loginAct.processLoginResponse(mockResponse))
    }

    @Test
    fun `processLoginResponse returns WrongPassword when wrong password is provided`() {
        val response = Response.error<LoginResponse>(
            400,
            ResponseBody.create(
                "application/json".toMediaTypeOrNull(),
                "{\"message\":\"Wrong password\"}"
            )
        )
        assertEquals(LoginAct.LoginResult.WrongPassword, loginAct.processLoginResponse(response))
    }

    @Test
    fun `processLoginResponse returns Failure for other errors`() {
        val response = Response.error<LoginResponse>(
            500,
            ResponseBody.create(
                "application/json".toMediaTypeOrNull(),
                "{\"message\":\"Internal server error\"}"
            )
        )
        assertEquals(LoginAct.LoginResult.Failure, loginAct.processLoginResponse(response))
    }

}
