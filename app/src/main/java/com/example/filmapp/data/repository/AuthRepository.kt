package com.example.filmapp.data.repository

import com.example.filmapp.ui.common.FirebaseAuthHelper
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class AuthRepository {
    private val _authResource = MutableStateFlow<Resource>(Resource.ResourceLoading)

    fun firebaseAuthWithGoogle(googleAuthCredential: AuthCredential): Flow<Resource> {
        FirebaseAuthHelper.getFirebaseSignInWithCredential(googleAuthCredential).addOnCompleteListener { task ->
            if (task.isSuccessful && FirebaseAuthHelper.getFirebaseCurrentUser() != null) {
                _authResource.value = Resource.ResourceSuccess(data = task.result)
            } else {
                _authResource.value = Resource.ResourceError(errorMessage = task.exception?.message)
            }
        }

        return _authResource
    }
}