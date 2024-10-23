package com.example.player_sample_project.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.player_sample_project.R
import com.example.player_sample_project.activity.MainActivity
import com.example.player_sample_project.app.AppController
import com.example.player_sample_project.app.FirebaseAuthInstance
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginBaseActivity : AppCompatActivity() {
    private lateinit var appController: AppController

    private lateinit var auth: FirebaseAuth
    private lateinit var googleLoginButton:ImageButton
    private lateinit var fbLogin:ImageButton

    private lateinit var textviewHaveAcc:TextView
    private lateinit var signUp_SignIn_Button:MaterialButton
    private lateinit var orView:TextView

    private lateinit var signIn_text:TextView
    private lateinit var signUp_text:TextView

    private lateinit var textview1:TextInputLayout
    private lateinit var textView2:TextInputLayout
    private lateinit var textView3:TextInputLayout

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        val intent = result.data
        Log.i("Login-", "signInLauncher resultCode-- ${result.resultCode} +\n intent is--$intent")

        if (result.resultCode == RESULT_OK && intent != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.i("Login-", "ApiException--${e.message}")
            }
        } else {
            Log.i("Login-", "GoogleSignInFailed--${result.resultCode}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_base)
        setupViews()
        onclickSignInText()
        onclickSignUpText()
        appController = AppController(this)

        // Initialize FirebaseAuth
        auth = FirebaseAuthInstance.auth //FirebaseAuth.getInstance()

        Log.i("Login-", "LoginPage onCreate")


        // onClick for GoogleLogin
        googleLoginButton.setOnClickListener {
            enableGoogleSignIn()
        }

    }

    override fun onStart() {
        super.onStart()
        Log.i("Login-", "LoginPage onStart")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i("Login-", "LoginPage onRestart")
    }

    override fun onPause() {
        super.onPause()
        Log.i("Login-", "LoginPage onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.i("Login-", "LoginPage onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("Login-", "LoginPage onDestroy")
    }

    // this method executes the Google SignIn flow
    private fun enableGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("Login-", "onActivityResult requestCode--$requestCode")
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.i("Login-", "GoogleSignInFailed--${e.message}")
            }
        }
    }*/


    // handle this function to authenticate with firebase using idToken
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) {
            task ->
                if (task.isSuccessful) {
                    // SignIn success, store the signed-in user's information in preferences
                    val user = auth.currentUser
                    if (user != null) {
                        Log.i("Login-", "user-->${user.uid} \n ${user.displayName}" + "\n" + "photoUrl is--${user.photoUrl}")
                        appController.storeLoginStatus("userId", user.uid, "userName", user.displayName.toString())
                        Toast.makeText(this, "Signed in as ${user.displayName}", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, MainActivity::class.java)) // Navigate to MainScreen
                        finish()
                    }
                } else {
                    Toast.makeText(applicationContext, "Sign-in failed", Toast.LENGTH_LONG).show()
                    Log.i("Login-", "Sign-In Credential failed", task.exception)
                }
        }
    }

    // View Initialization is handled in this method
    private fun setupViews() {
        signIn_text = findViewById(R.id.signIn_text)
        signUp_text = findViewById(R.id.signUp_text)
        textviewHaveAcc = findViewById(R.id.txt_view)
        signUp_SignIn_Button = findViewById(R.id.btn_Signup_SignIn)

        textview1 = findViewById(R.id.nameTextview)
        textView2 = findViewById(R.id.emailTextview)
        textView3 = findViewById(R.id.passwordTextview)

        orView = findViewById(R.id.or_view)
        googleLoginButton = findViewById(R.id.google_signIn_button)
        fbLogin = findViewById(R.id.facebook_signIn)
    }

    private fun onclickSignInText() {
        signIn_text.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                textview1.visibility=View.GONE
                signUp_SignIn_Button.setText(R.string.sign_in)
                textviewHaveAcc.setText(R.string.don_t_have_an_account)

                signIn_text.visibility = View.GONE
                signUp_text.visibility = View.VISIBLE
            }
        })
    }

    private fun onclickSignUpText() {
        signUp_text.setOnClickListener(View.OnClickListener {
            textview1.visibility = View.VISIBLE
            signUp_SignIn_Button.setText(R.string.sign_up)
            textviewHaveAcc.setText(R.string.already_have_an_account)

            signIn_text.visibility = View.VISIBLE
            signUp_text.visibility = View.GONE
        })
    }

}