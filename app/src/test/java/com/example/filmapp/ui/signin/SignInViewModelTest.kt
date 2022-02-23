package com.example.filmapp.ui.signin

import android.app.Application
import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.filmapp.ui.common.getOrAwaitValue
import com.example.filmapp.data.repository.AuthRepository
import com.example.filmapp.data.repository.Resource
import com.example.filmapp.ui.common.FirebaseAuthHelper
import com.example.filmapp.ui.common.MainDispatcherRule
import com.example.filmapp.util.logDTag
import com.example.filmapp.util.logETag
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeoutException

class SignInViewModelTest {

    private companion object {
        const val SIGN_IN_ERROR_MESSAGE_TOAST = "SIGN_IN_ERROR_MESSAGE_TOAST"
    }

    private lateinit var signInViewModelForTesting: SignInViewModelForTesting

    private val applicationMocked = mockk<Application>()
    private val authRepositoryMocked = mockk<AuthRepository>()
    private val firebaseUserMocked = mockk<FirebaseUser>()

    private val intentMocked = mockk<Intent>()
    private val googleSignInAccountMocked = mockk<GoogleSignInAccount>(relaxed = true)
    private val authCredentialMocked = mockk<AuthCredential>()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    private fun setUpSignInViewModel(firebaseUser: FirebaseUser? = null) {
        mockkObject(FirebaseAuthHelper)

        every { FirebaseAuthHelper.getFirebaseCurrentUser() } returns firebaseUser

        signInViewModelForTesting = SignInViewModelForTesting(applicationMocked, authRepositoryMocked)
    }

    private fun setUpProcessEvent() {
        mockkStatic("com.example.filmapp.util.LogExtensionsKt")

        setUpSignInViewModel()

        every { signInViewModelForTesting.logDTag(any()) } returns 0
    }

    private fun setUpGoogleSignInAccount() {
        mockkStatic(GoogleSignIn::class)

        setUpProcessEvent()
    }

    private fun setUpGoogleCredentials() {
        mockkStatic(GoogleAuthProvider::class)

        setUpGoogleSignInAccount()

        every {
            GoogleSignIn.getSignedInAccountFromIntent(intentMocked)
                .getResult(ApiException::class.java)
        } returns googleSignInAccountMocked
    }

    @ExperimentalCoroutinesApi
    private fun setUpFirebaseAuthWithGoogle(resource: Resource) {
        setUpGoogleCredentials()

        every {
            GoogleAuthProvider.getCredential(
                googleSignInAccountMocked.idToken,
                null
            )
        } returns authCredentialMocked

        every { authRepositoryMocked.firebaseAuthWithGoogle(authCredentialMocked) } returns flow { emit(resource) }
    }

    private fun setUpGoogleSignInFailedToast() {
        every { signInViewModelForTesting.logETag(any()) } returns 0

        every { applicationMocked.getString(any()) } returns SIGN_IN_ERROR_MESSAGE_TOAST
    }

    private fun verifyGoogleSignInFailedToastBeforeAuthRequest(exception: Exception) {
        verify(exactly = 1) { signInViewModelForTesting.logDTag("processEvent: ${SignInEvent.OnGoogleSignInIntentCameSuccess(intentMocked)}") }
        verify(exactly = 1) { signInViewModelForTesting.logETag("Google sign in failed: ${exception.message}") }
        verify(exactly = 0) { authRepositoryMocked.firebaseAuthWithGoogle(any()) }

        Assert.assertEquals(
            signInViewModelForTesting.action.getOrAwaitValue(),
            SignInAction.ShowGoogleSignInFailedToast(SIGN_IN_ERROR_MESSAGE_TOAST)
        )
    }

    @Test(expected = TimeoutException::class)
    fun `should follow the sign in flow when do not have a registered user in firebase`() {
        setUpSignInViewModel()

        Assert.assertNotEquals(
            signInViewModelForTesting.action.getOrAwaitValue(),
            SignInAction.StartHomeScreen
        )
    }

    @Test
    fun `should start home screen when there is already a registered user in firebase`() {
        setUpSignInViewModel(firebaseUser = firebaseUserMocked)

        Assert.assertEquals(
            signInViewModelForTesting.action.getOrAwaitValue(),
            SignInAction.StartHomeScreen
        )
    }

    @Test
    fun `should log process information and start google sign in flow when google sign in button is clicked`() {
        setUpProcessEvent()

        signInViewModelForTesting.processEventForTesting(SignInEvent.OnSignInButtonClicked)

        verify(exactly = 1) { signInViewModelForTesting.logDTag("processEvent: ${SignInEvent.OnSignInButtonClicked}") }

        Assert.assertEquals(
            signInViewModelForTesting.action.getOrAwaitValue(),
            SignInAction.StartGoogleSignIn
        )
    }

    @Test
    fun `should log process information, log sign in failed and show google sign in failed toast when google sign in intent came success but google sign in account throws an api exception`() {
        setUpGoogleSignInAccount()
        setUpGoogleSignInFailedToast()

        val apiExceptionMocked = mockk<ApiException>(relaxed = true)

        every {
            GoogleSignIn.getSignedInAccountFromIntent(intentMocked)
                .getResult(ApiException::class.java)
        } throws apiExceptionMocked

        signInViewModelForTesting.processEventForTesting(
            SignInEvent.OnGoogleSignInIntentCameSuccess(
                intentMocked
            )
        )

        verifyGoogleSignInFailedToastBeforeAuthRequest(exception = apiExceptionMocked)
    }

    @Test
    fun `should log process information, log sign in failed and show google sign in failed toast when google sign in intent came success but get credentials throws an illegal argument`() {
        setUpGoogleCredentials()
        setUpGoogleSignInFailedToast()

        val illegalArgumentExceptionMocked = mockk<IllegalArgumentException>(relaxed = true)

        every {
            GoogleAuthProvider.getCredential(
                googleSignInAccountMocked.idToken,
                null
            )
        } throws illegalArgumentExceptionMocked

        signInViewModelForTesting.processEventForTesting(
            SignInEvent.OnGoogleSignInIntentCameSuccess(
                intentMocked
            )
        )

        verifyGoogleSignInFailedToastBeforeAuthRequest(exception = illegalArgumentExceptionMocked)
    }

    @ExperimentalCoroutinesApi
    @Test(expected = TimeoutException::class)
    fun `should log process information and resource loading information when google sign in intent came success`() {
        setUpFirebaseAuthWithGoogle(resource = Resource.ResourceLoading)

        runTest {
            signInViewModelForTesting.processEventForTesting(
                SignInEvent.OnGoogleSignInIntentCameSuccess(
                    intentMocked
                )
            )
        }

        verify(exactly = 1) { signInViewModelForTesting.logDTag("processEvent: ${SignInEvent.OnGoogleSignInIntentCameSuccess(intentMocked)}") }
        verify(exactly = 0) { signInViewModelForTesting.logETag(any()) }
        verify(exactly = 1) { authRepositoryMocked.firebaseAuthWithGoogle(authCredentialMocked) }
        verify(exactly = 1) { signInViewModelForTesting.logDTag("firebaseAuthWithGoogle: Loading") }

        Assert.assertNotEquals(
            signInViewModelForTesting.action.getOrAwaitValue(),
            SignInAction.ShowGoogleSignInFailedToast(SIGN_IN_ERROR_MESSAGE_TOAST)
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should log process information, resource success information and start home screen when firebase auth with google returns success`() {
        setUpFirebaseAuthWithGoogle(resource = Resource.ResourceSuccess<AuthResult>(data = mockk()))

        runTest {
            signInViewModelForTesting.processEventForTesting(
                SignInEvent.OnGoogleSignInIntentCameSuccess(
                    intentMocked
                )
            )
        }

        verify(exactly = 1) { signInViewModelForTesting.logDTag("processEvent: ${SignInEvent.OnGoogleSignInIntentCameSuccess(intentMocked)}") }
        verify(exactly = 0) { signInViewModelForTesting.logETag(any()) }
        verify(exactly = 1) { authRepositoryMocked.firebaseAuthWithGoogle(authCredentialMocked) }
        verify(exactly = 1) { signInViewModelForTesting.logDTag("firebaseAuthWithGoogle: Success") }

        Assert.assertEquals(signInViewModelForTesting.action.getOrAwaitValue(), SignInAction.StartHomeScreen)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should log process information, resource error information and show google sign in failed toast when firebase auth with google returns error`() {
        setUpFirebaseAuthWithGoogle(resource = Resource.ResourceError(errorMessage = SIGN_IN_ERROR_MESSAGE_TOAST))
        setUpGoogleSignInFailedToast()

        runTest {
            signInViewModelForTesting.processEventForTesting(
                SignInEvent.OnGoogleSignInIntentCameSuccess(
                    intentMocked
                )
            )
        }

        verify(exactly = 1) { signInViewModelForTesting.logDTag("processEvent: ${SignInEvent.OnGoogleSignInIntentCameSuccess(intentMocked)}") }
        verify(exactly = 1) { signInViewModelForTesting.logETag("firebaseAuthWithGoogle: Fail -> $SIGN_IN_ERROR_MESSAGE_TOAST") }
        verify(exactly = 1) { authRepositoryMocked.firebaseAuthWithGoogle(authCredentialMocked) }

        Assert.assertEquals(
            signInViewModelForTesting.action.getOrAwaitValue(),
            SignInAction.ShowGoogleSignInFailedToast(SIGN_IN_ERROR_MESSAGE_TOAST)
        )
    }
}

private class SignInViewModelForTesting(application: Application, authRepository: AuthRepository) : SignInViewModel(application, authRepository) {
    fun processEventForTesting(event: SignInEvent) {
        processEvent(event)
    }
}