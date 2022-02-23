package com.example.filmapp.ui.common

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object FirebaseAuthHelper {
    fun getFirebaseCurrentUser() = Firebase.auth.currentUser

    fun getFirebaseSignInWithCredential(credential: AuthCredential) = Firebase.auth.signInWithCredential(credential)
}