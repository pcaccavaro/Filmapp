package com.example.filmapp.data.repository

import com.example.filmapp.ui.common.FirebaseAuthHelper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import io.mockk.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AuthRepositoryTest {

    companion object {
        const val EXCEPTION_MESSAGE = "EXCEPTION_MESSAGE"
    }

    private lateinit var authRepository: AuthRepository
    private lateinit var authResource: Flow<Resource>

    private val credentialMocked = mockk<AuthCredential>()
    private val taskAuthResultMocked = mockk<Task<AuthResult>>(relaxed = true)
    private val onCompleteTaskAuthResultMocked = mockk<Task<AuthResult>>()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkObject(FirebaseAuthHelper)

        every { FirebaseAuthHelper.getFirebaseSignInWithCredential(credential = credentialMocked) } returns taskAuthResultMocked

        authRepository = AuthRepository()
    }

    private fun setUpAddOnCompleteListener(isOnCompleteTaskSuccessful: Boolean = false, firebaseUser: FirebaseUser? = null) {
        val onCompleteListenerSlot = slot<OnCompleteListener<AuthResult>>()

        every { taskAuthResultMocked.addOnCompleteListener(capture(onCompleteListenerSlot)) } returns taskAuthResultMocked

        every { onCompleteTaskAuthResultMocked.isSuccessful } returns isOnCompleteTaskSuccessful

        every { FirebaseAuthHelper.getFirebaseCurrentUser() } returns firebaseUser

        authResource = authRepository.firebaseAuthWithGoogle(googleAuthCredential = credentialMocked)

        onCompleteListenerSlot.captured.onComplete(onCompleteTaskAuthResultMocked)
    }

    @Test
    fun `should firebase auth with google return the resource loading state when firebase sign in with credential not come`() {
        authResource = authRepository.firebaseAuthWithGoogle(googleAuthCredential = credentialMocked)

        runBlocking { Assert.assertEquals(authResource.first(), Resource.ResourceLoading) }
    }

    @Test
    fun `should firebase auth with google return the resource success state when sign in with credential come success and firebase user has been registered`() {
        val authResultMocked = mockk<AuthResult>()

        every { onCompleteTaskAuthResultMocked.result } returns authResultMocked

        setUpAddOnCompleteListener(isOnCompleteTaskSuccessful = true, firebaseUser = mockk())

        runBlocking { Assert.assertEquals(authResource.first(), Resource.ResourceSuccess(authResultMocked)) }
    }

    @Test
    fun `should firebase auth with google return the resource error state when sign in with credential come error`() {
        every { onCompleteTaskAuthResultMocked.exception?.message } returns EXCEPTION_MESSAGE

        setUpAddOnCompleteListener()

        runBlocking { Assert.assertEquals(authResource.first(), Resource.ResourceError(errorMessage = EXCEPTION_MESSAGE)) }
    }

    @Test
    fun `should firebase auth with google return the resource error state when sign in with credential come success but firebase user not registered`() {
        every { onCompleteTaskAuthResultMocked.exception?.message } returns null

        setUpAddOnCompleteListener(isOnCompleteTaskSuccessful = true)

        runBlocking { Assert.assertEquals(authResource.first(), Resource.ResourceError(errorMessage = null)) }
    }
}