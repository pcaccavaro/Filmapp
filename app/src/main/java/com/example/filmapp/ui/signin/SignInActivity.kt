package com.example.filmapp.ui.signin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.filmapp.R
import com.example.filmapp.data.repository.AuthRepository
import com.example.filmapp.ui.common.BaseActivity
import com.example.filmapp.ui.home.HomeActivity
import com.example.filmapp.util.logDTag
import com.example.filmapp.util.logETag
import com.example.filmapp.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class SignInActivity : BaseActivity<SignInEvent, SignInAction>() {
    private lateinit var binding: ActivitySignInBinding

    private lateinit var googleSignInClient: GoogleSignInClient

    override val viewModel by viewModels<SignInViewModel> { SignInViewModelFactory(application, AuthRepository()) }

    private val googleSignInActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.startEvent(SignInEvent.OnGoogleSignInIntentCameSuccess(result.data))
            } else {
                logETag("Failed to open Google sign in screen: ${result.resultCode}")
                showGoogleSignInFailedToast(toastMessage = getString(R.string.sign_in_failed_to_open_google_sign_in))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)

        setUpClickListener()
        setUpGoogleSignInOptions()
    }

    private fun setUpClickListener() {
        binding.activityGoogleSignInButton.setOnClickListener { viewModel.startEvent(SignInEvent.OnSignInButtonClicked) }
    }

    private fun setUpGoogleSignInOptions() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    override fun processAction(action: SignInAction) {
        logDTag("processAction: $action")

        when (action) {
            SignInAction.StartGoogleSignIn ->
                googleSignInActivityResult.launch(googleSignInClient.signInIntent)
            is SignInAction.ShowGoogleSignInFailedToast ->
                showGoogleSignInFailedToast(action.toastMessage)
            SignInAction.StartHomeScreen -> {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
    }

    private fun showGoogleSignInFailedToast(toastMessage: String) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
    }
}