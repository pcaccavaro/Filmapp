package com.example.filmapp.ui.signin

import android.content.Intent

sealed class SignInAction {
    object StartGoogleSignIn : SignInAction()
    data class ShowGoogleSignInFailedToast(val toastMessage: String) : SignInAction()
    object StartHomeScreen : SignInAction()
}

sealed class SignInEvent {
    object OnSignInButtonClicked : SignInEvent()
    data class OnGoogleSignInIntentCameSuccess(val googleSignInIntent: Intent?) : SignInEvent()
}