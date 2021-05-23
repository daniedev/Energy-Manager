package daniedev.github.energymanager.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import daniedev.github.energymanager.R
import daniedev.github.energymanager.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var signInClient: GoogleSignInClient

    // Firebase instance variables
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signInButton.setOnClickListener { signIn() }

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        signInClient = GoogleSignIn.getClient(this, gso)

        // Initialize FirebaseAuth
        auth = Firebase.auth
    }

    private fun signIn() {
        // TODO: implement
        val signInIntent = signInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // TODO: implement

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                firebaseAuthWithGoogle(task.getResult(ApiException::class.java))
            } catch (e: ApiException) {
                Log.w(TAG, "Google SignIn Failed", e)
            }
        }

    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        // TODO: implement
        Log.d(TAG, "firebaseAuthWithGoogle" + acct?.id)
        val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener(this) {
                Log.d(TAG, "signInWithCredential:success")
                startActivity(Intent(this@SignInActivity, EnergyManagerActivity::class.java))
                finish()
            }
            .addOnFailureListener(this) { e ->
                Log.w(TAG, "signInWithCredential", e)
                Toast.makeText(
                    this@SignInActivity, "Authentication Failed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    companion object {
        private const val TAG = "SignInActivity"
        private const val RC_SIGN_IN = 9001
    }
}
