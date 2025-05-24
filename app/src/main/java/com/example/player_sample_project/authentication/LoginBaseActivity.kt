package com.example.player_sample_project.authentication

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.player_sample_project.R
import com.example.player_sample_project.activity.MainActivity
import com.example.player_sample_project.app.AppController
import com.example.player_sample_project.app.FirebaseAuthInstance
import com.example.player_sample_project.app.Utils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private lateinit var inputTextName:TextInputLayout
    private lateinit var inputTextEmail:TextInputLayout
    private lateinit var inputTextPassword:TextInputLayout
    private lateinit var progress: ProgressBar

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        val intent = result.data
        Log.i("Login-", "signInLauncher resultCode-- ${result.resultCode} +\n intent is--$intent")

        if (result.resultCode == RESULT_OK && intent != null) {
            showProgressLoader()
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.i("Login-", "ApiException--${e.message}")
            }
        } else {
            hideProgressLoader()
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
            if (Utils.checkNetworkAndShowDialog(this)) {
                showProgressLoader()
                enableGoogleSignIn()
            }
        }

        // Observes the input texts with validation
        signUpSignInInputValidations()

        // onclick for signup/signIn
        signUpSignInButton.setOnClickListener {
            Log.i("Login-", "${signUpSignInButton.text} clicked")
            if (Utils.checkNetworkAndShowDialog(this)) {
                if (signUpSignInButton.text.equals("Sign Up")) { // SignUp
                    if (inputTextName.editText?.text.toString().isEmpty() || inputTextEmail.editText?.text.toString().isEmpty() ||
                        inputTextPassword.editText?.text.toString().isEmpty()) {
                        Toast.makeText(this, "Please Enter valid inputs to signup here", Toast.LENGTH_LONG).show()
                    } else if (inputTextName.error == null && inputTextEmail.error == null && inputTextPassword.error == null) {
                        lifecycleScope.launch {
                            showProgressLoader()
                            val result = withContext(Dispatchers.Main) {
                                handleSignUpCredentials(
                                    inputTextEmail.editText?.text.toString(),
                                    inputTextPassword.editText?.text.toString()
                                )
                            }
                            if (result == "200" || result == "The email address is already in use by another account.") {
                                updateUiToSignIn(result, signUpSignInButton.text.toString(), inputTextEmail.editText?.text.toString())
                            } else {
                                Utils.showLongMessage(this@LoginBaseActivity, result)
                                hideProgressLoader()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Enter valid inputs", Toast.LENGTH_LONG).show()
                    }
                } else { // SignIn
                    if (inputTextEmail.editText?.text.toString().isEmpty() || inputTextPassword.editText?.text.toString().isEmpty()) {
                        Toast.makeText(this, "Enter credentials to signIn here", Toast.LENGTH_LONG).show()
                    } else if (inputTextEmail.error == null && inputTextPassword.error == null) {
                        lifecycleScope.launch {
                            showProgressLoader()
                            val signInResult = withContext(Dispatchers.Main) {
                                handleSignInCredentials(
                                    inputTextEmail.editText?.text.toString(),
                                    inputTextPassword.editText?.text.toString()
                                )
                            }

                            if (signInResult == "200" && auth.currentUser?.email == inputTextEmail.editText?.text.toString()) {
                                updateUiToSignIn(signInResult, signUpSignInButton.text.toString(), inputTextEmail.editText?.text.toString())
                            } else if (signInResult == "The supplied auth credential is incorrect, malformed or has expired.") {
                                hideProgressLoader()
                                updateUiToSignIn(signInResult, signUpSignInButton.text.toString(), inputTextEmail.editText?.text.toString())
                            } else {
                                Utils.showLongMessage(this@LoginBaseActivity, signInResult)
                                hideProgressLoader()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Enter valid credentials to signIn", Toast.LENGTH_LONG).show()
                    }
                }
            }
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
        hideProgressLoader()
        Log.i("Login-", "LoginPage onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("Login-", "LoginPage onDestroy")
    }

    // Listeners for inputText with validation
    private fun signUpSignInInputValidations() {
        //Name
        inputTextName.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                if (!p0.toString().matches(Regex("^[a-zA-Z ]*\$"))) {
                    inputTextName.error = "Only alphabets supported"
                } else if (inputTextName.editText?.text.toString().length < 3 && inputTextName.editText?.text.toString().isNotEmpty()) {
                    inputTextName.error = "Name length should minimum 3 characters"
                } else {
                    inputTextName.error = null
                }
            }
        })

        //Email
        inputTextEmail.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                if (!p0.toString().matches(Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")) && p0.toString().isNotEmpty()) {
                    inputTextEmail.error = "Enter valid email format"
                } else {
                    inputTextEmail.error = null
                }
            }
        })

        //Password
        inputTextPassword.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().length < 6 && p0.toString().isNotEmpty()) {
                    inputTextPassword.error = "Password should minimum 6 characters"
                } else {
                    inputTextPassword.error = null
                }
            }
        })
    }

    // handles Register/SignUp credentials
    private suspend fun handleSignUpCredentials(email: String, password: String): String {
        val createSignUpUsernamePassword = handleCreateUserWithEmailAndPassword(email, password) //execute with firebaseAuth

        val response = createSignUpUsernamePassword["response"]
        val data = createSignUpUsernamePassword["data"]
        if (response != 200 && data.toString().contains(":")) {
            return data.toString().substringAfter(":").trim()
        } else if (response != 200) {
            return data.toString()
        } else if (response == 200 && email == auth.currentUser?.email) {
            return response.toString()
        }
        return "Register failed $response"
    }

    // handles SignIn credentials
    private suspend fun handleSignInCredentials(email: String, password: String): String {
        val signInWithEmailAndPassword = handleSignInWithEmailAndPassword(email, password) //execute with firebaseAuth to check with credentials
        val response = signInWithEmailAndPassword["response"]
        val data = signInWithEmailAndPassword["data"]

        if (response == 200) {
            return response.toString()
        } else if (response == 401) {
            return data.toString().substringAfter(":").trim()
        }
        return "Authentication failed $response"
    }

    private fun updateUiToSignIn(result: String, type: String, email: String) {
        if (type == "Sign Up") {
            hideProgressLoader()
            if (result == "200") Toast.makeText(this, "Signup successful & account created", Toast.LENGTH_LONG).show()
            else Toast.makeText(this, "The email address is already registered, please signIn here", Toast.LENGTH_LONG).show()

            signInText.performClick()
            inputTextEmail.editText?.setText(email) //auth.currentUser?.email
            inputTextPassword.requestFocus()
        } else {
            if (result != "200") Toast.makeText(this, "Invalid credentials! Check your email and password", Toast.LENGTH_LONG).show()
            else {
               storeLoggedInDataToHomeScreen() // Stores the loggedIn data and navigates to homeScreen
            }
        }
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
                    storeLoggedInDataToHomeScreen()
                } else {
                    Toast.makeText(applicationContext, "Sign-in failed", Toast.LENGTH_LONG).show()
                    Log.i("Login-", "Sign-In Credential failed", task.exception)
                }
        }
    }

    // Stores the auth data and navigates to Homescreen
    private fun storeLoggedInDataToHomeScreen() {
        val loggedInUser = auth.currentUser
        if (loggedInUser != null) {
            Log.i("Login-", "user-->${loggedInUser.uid} \n ${loggedInUser.displayName}" + "\n" + "photoUrl is--${loggedInUser.photoUrl}")
            // Alternative option to usage of hashmap is .put("key", "value") instead of hashMap["key"]
            val hashMap: HashMap<String, String> = HashMap()
            hashMap["userId"] = loggedInUser.uid
            hashMap["userName"] = loggedInUser.displayName ?: loggedInUser.email.toString()
            hashMap["photoUrl"] = loggedInUser.photoUrl.toString()
            appController.storeLoginStatus(hashMap)
            Toast.makeText(this, "Signed in as ${hashMap["userName"]}", Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) // Navigate to MainScreen
            hideProgressLoader()
            finish()
        } else Toast.makeText(this, "Sign In Failed", Toast.LENGTH_LONG).show()
    }

    // View Initialization is handled in this method
    private fun setupViews() {
        progress = findViewById(R.id.login_progress)
        signInText = findViewById(R.id.signIn_text)
        signUpText = findViewById(R.id.signUp_text)
        textviewHaveAcc = findViewById(R.id.txt_view)
        signUpSignInButton = findViewById(R.id.btn_Signup_SignIn)

        inputTextName = findViewById(R.id.nameTextview)
        inputTextEmail = findViewById(R.id.emailTextview)
        inputTextPassword = findViewById(R.id.passwordTextview)

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
                inputTextName.visibility=View.GONE
                signUpSignInButton.setText(R.string.sign_in)
                textviewHaveAcc.setText(R.string.don_t_have_an_account)

                signInText.visibility = View.GONE
                signUpText.visibility = View.VISIBLE

                //initial focus
                inputTextEmail.requestFocus()
                inputTextEmail.editText?.setText("")
                inputTextPassword.editText?.setText("")
            }
        })
    }

    private fun onclickSignUpText() {
        signUpText.setOnClickListener(View.OnClickListener {
            inputTextName.visibility = View.VISIBLE
            signUpSignInButton.setText(R.string.sign_up)
            textviewHaveAcc.setText(R.string.already_have_an_account)

            signInText.visibility = View.VISIBLE
            signUpText.visibility = View.GONE

            //initial focus
            inputTextName.requestFocus()
            inputTextName.editText?.setText("")
            inputTextEmail.editText?.setText("")
            inputTextPassword.editText?.setText("")
        })
    }

    private fun showProgressLoader() {
        progress.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun hideProgressLoader() {
        progress.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

    }

}