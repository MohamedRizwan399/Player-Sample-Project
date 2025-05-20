package com.example.player_sample_project.authentication

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    private lateinit var signUpSignInButton:MaterialButton
    private lateinit var orView:TextView

    private lateinit var signInText:TextView
    private lateinit var signUpText:TextView

    private lateinit var textview1:TextInputLayout
    private lateinit var textView2:TextInputLayout
    private lateinit var textView3:TextInputLayout
    private lateinit var progress: ProgressBar

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        val intent = result.data
        Log.i("Login-", "signInLauncher resultCode-- ${result.resultCode} +\n intent is--$intent")

        if (result.resultCode == RESULT_OK && intent != null) {
            progress.visibility = View.VISIBLE
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.i("Login-", "ApiException--${e.message}")
            }
        } else {
            progress.visibility = View.GONE
            Log.i("Login-", "GoogleSignInFailed--${result.resultCode}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_base)

        // To change the default to preferred color of app description when goes to recent
        val taskDescription = ActivityManager.TaskDescription(
            getString(R.string.app_name), null, ContextCompat.getColor(this, R.color.dark_white))
        setTaskDescription(taskDescription)

        setupViews()
        onclickSignInText()
        onclickSignUpText()
        appController = AppController(this)

        // Initialize FirebaseAuth
        auth = FirebaseAuthInstance.auth //FirebaseAuth.getInstance()

        Log.i("Login-", "LoginPage onCreate")

        // onClick for GoogleLogin
        googleLoginButton.setOnClickListener {
            progress.visibility = View.VISIBLE
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
        progress.visibility = View.GONE
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
                        // Alternative option to usage of hashmap is .put("key", "value") instead of hashMap["key"]
                        val hashMap: HashMap<String, String> = HashMap()
                        hashMap["userId"] = user.uid
                        hashMap["userName"] = user.displayName.toString()
                        hashMap["photoUrl"] = user.photoUrl.toString()
                        appController.storeLoginStatus(hashMap)
                        Toast.makeText(this, "Signed in as ${user.displayName}", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent) // Navigate to MainScreen
                        progress.visibility = View.GONE
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
        progress = findViewById(R.id.login_progress)
        signInText = findViewById(R.id.signIn_text)
        signUpText = findViewById(R.id.signUp_text)
        textviewHaveAcc = findViewById(R.id.txt_view)
        signUpSignInButton = findViewById(R.id.btn_Signup_SignIn)

        textview1 = findViewById(R.id.nameTextview)
        textView2 = findViewById(R.id.emailTextview)
        textView3 = findViewById(R.id.passwordTextview)

        orView = findViewById(R.id.or_view)
        googleLoginButton = findViewById(R.id.google_signIn_button)
        fbLogin = findViewById(R.id.facebook_signIn)

        fbLogin.setOnClickListener(View.OnClickListener {
            Toast.makeText(applicationContext, "Right now not implemented.Check with GoogleLogin or sign In here", Toast.LENGTH_LONG).show()
        })
    }

    private fun onclickSignInText() {
        signInText.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                textview1.visibility=View.GONE
                signUpSignInButton.setText(R.string.sign_in)
                textviewHaveAcc.setText(R.string.don_t_have_an_account)

                signInText.visibility = View.GONE
                signUpText.visibility = View.VISIBLE

                //initial focus
                textView2.requestFocus()
            }
        })
    }

    private fun onclickSignUpText() {
        signUpText.setOnClickListener(View.OnClickListener {
            textview1.visibility = View.VISIBLE
            signUpSignInButton.setText(R.string.sign_up)
            textviewHaveAcc.setText(R.string.already_have_an_account)

            signInText.visibility = View.VISIBLE
            signUpText.visibility = View.GONE

            //initial focus
            textview1.requestFocus()
        })
    }

}