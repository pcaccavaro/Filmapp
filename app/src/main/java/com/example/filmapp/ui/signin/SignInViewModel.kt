package com.example.filmapp.ui.signin

import android.app.Application
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.filmapp.R
import com.example.filmapp.data.repository.AuthRepository
import com.example.filmapp.data.repository.Resource
import com.example.filmapp.ui.common.BaseViewModel
import com.example.filmapp.ui.common.FirebaseAuthHelper
import com.example.filmapp.util.logDTag
import com.example.filmapp.util.logETag
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

open class SignInViewModel(private val app: Application, private val authRepository: AuthRepository) : BaseViewModel<SignInEvent, SignInAction>(app) {

    init {
        FirebaseAuthHelper.getFirebaseCurrentUser()?.let { setAction(SignInAction.StartHomeScreen) }
    }

    override fun processEvent(event: SignInEvent) {
        logDTag("processEvent: $event")

        when (event) {
            SignInEvent.OnSignInButtonClicked -> setAction(SignInAction.StartGoogleSignIn)
            is SignInEvent.OnGoogleSignInIntentCameSuccess -> authCredential(event.googleSignInIntent)
        }
    }

    private fun authCredential(googleSignInIntent: Intent?) {
        val googleAuthCredential: AuthCredential

        try {
            // Google Sign In was successful, authenticate with Firebase
            val signInAccount = GoogleSignIn.getSignedInAccountFromIntent(googleSignInIntent).getResult(ApiException::class.java)!!
            googleAuthCredential = GoogleAuthProvider.getCredential(signInAccount.idToken, null)
        } catch (exception: Exception) {
            // Google Sign In failed
            logETag("Google sign in failed: ${exception.message}")
            setAction(SignInAction.ShowGoogleSignInFailedToast(toastMessage = app.getString(R.string.sign_in_something_went_wrong_in_google_sign_in)))
            return
        }

        viewModelScope.launch {
            authRepository.firebaseAuthWithGoogle(googleAuthCredential).collect { authResource ->
                when(authResource) {
                    is Resource.Error -> {
                        // If sign in fails, display a message to the user
                        this@SignInViewModel.logETag("firebaseAuthWithGoogle: Fail -> ${authResource.errorMessage}")
                        setAction(SignInAction.ShowGoogleSignInFailedToast(toastMessage = app.getString(R.string.sign_in_authentication_failed)))
                    }
                    is Resource.Loading -> this@SignInViewModel.logDTag("firebaseAuthWithGoogle: Loading")
                    is Resource.Success -> {
                        // Sign in success, update UI with user's information
                        this@SignInViewModel.logDTag("firebaseAuthWithGoogle: Success")
                        setAction(SignInAction.StartHomeScreen)
                    }
                }
            }
        }
    }
}

class SignInViewModelFactory(private val application: Application, private val authRepository: AuthRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = modelClass.cast(SignInViewModel(application, authRepository))!!
}