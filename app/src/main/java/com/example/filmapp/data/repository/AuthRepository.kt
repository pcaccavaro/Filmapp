package com.example.filmapp.data.repository

import com.example.filmapp.ui.common.FirebaseAuthHelper
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class AuthRepository {
    private val _authResource = MutableStateFlow<Resource<AuthResult>>(Resource.Loading())

    fun firebaseAuthWithGoogle(googleAuthCredential: AuthCredential): Flow<Resource<AuthResult>> {
        FirebaseAuthHelper.getFirebaseSignInWithCredential(googleAuthCredential).addOnCompleteListener { task ->
            if (task.isSuccessful && FirebaseAuthHelper.getFirebaseCurrentUser() != null) {
                _authResource.value = Resource.Success(data = task.result)
            } else {
                _authResource.value = Resource.Error(errorMessage = task.exception?.message)
            }
        }

        return _authResource
    }
}