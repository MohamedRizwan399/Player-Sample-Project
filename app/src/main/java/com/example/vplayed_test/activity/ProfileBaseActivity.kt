package com.example.vplayed_test.activity

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.vplayed_test.R
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.*

class ProfileBaseActivity : AppCompatActivity(),GoogleApiClient.OnConnectionFailedListener {

    private var googlesignInButton: ImageButton? = null
    private var googlesignUpButton1: ImageButton? = null

    private var googleApiClient: GoogleApiClient? = null
    private val RC_SIGN_IN = 1
//    var mGoogleSignInClient: GoogleSignInClient? = null
    private var mAuth: FirebaseAuth? = null
    var dialog: Dialog? = null

    private lateinit var textviewHaveAcc:TextView
    private lateinit var textviewdontHaveAcc:TextView

    private lateinit var or_view:TextView
    private lateinit var or_view1:TextView

    private lateinit var gLogin:ImageButton
    private lateinit var gLogin1:ImageButton
    private lateinit var fbLogin:ImageButton
    private lateinit var fbLogin1:ImageButton

    private lateinit var signIntext:TextView
    private lateinit var signUptext:TextView

    private lateinit var textview1:TextInputLayout
    private lateinit var textView2Signup:TextInputLayout
    private lateinit var textView3Signup:TextInputLayout
    private lateinit var textView2:TextInputLayout
    private lateinit var textView3:TextInputLayout


    private lateinit var signUpButton:MaterialButton
    private lateinit var signInButton: MaterialButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_base)
        setupViews()
        onclickSignInText()
        onclickSignUpText()
        //for google signin
        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken("927650860397-h309mrejjd6cen3d0rf3r2fptsa30bt8.apps.googleusercontent.com")
            .build()
        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        googlesignInButton = findViewById<View>(R.id.sign_in_button) as ImageButton
        googlesignUpButton1=findViewById<View>(R.id.sign_in_button1) as ImageButton
      googlesignInButton!!.setOnClickListener {
          val intent:Intent=Auth.GoogleSignInApi.getSignInIntent(googleApiClient!!)
          startActivityForResult(intent,RC_SIGN_IN)
      }
        googlesignUpButton1?.setOnClickListener {
            val intent:Intent=Auth.GoogleSignInApi.getSignInIntent(googleApiClient!!)
            startActivityForResult(intent,RC_SIGN_IN)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==RC_SIGN_IN) {
            val result = data?.let { Auth.GoogleSignInApi.getSignInResultFromIntent(it) }
            if (result!!.isSuccess) {
                val account = result.signInAccount
                handleSignInResult(result)
                firebaseAuthWithGoogle(account?.getIdToken()!!)
            } else {
                Toast.makeText(applicationContext, "Sign in again", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        if (result.isSuccess()) {
            finish()
            Toast.makeText(applicationContext, "Successful", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(applicationContext, "Sign in cancel", Toast.LENGTH_LONG).show()
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = mAuth!!.currentUser
                } else {
                    dialog!!.dismiss()
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setupViews(){
        signIntext=findViewById(R.id.signIntext)
        signUptext=findViewById(R.id.signUptext)

        textviewHaveAcc=findViewById(R.id.txt_view)
        textviewdontHaveAcc=findViewById(R.id.txt_viewNew)

        textview1=findViewById(R.id.textview1)
        textView2Signup=findViewById(R.id.textview2)
        textView3Signup=findViewById(R.id.textview3)
        textView2=findViewById(R.id.textview2Signinpage)
        textView3=findViewById(R.id.textview3Signinpage)

        signUpButton=findViewById(R.id.btn)
        signInButton=findViewById(R.id.btnn)

        or_view=findViewById(R.id.or_view)
        or_view1=findViewById(R.id.or_view1)
        gLogin=findViewById(R.id.sign_in_button)
        gLogin1=findViewById(R.id.sign_in_button1)
        fbLogin=findViewById(R.id.facebook_signin)
        fbLogin1=findViewById(R.id.facebook_signin1)

    }

    private fun onclickSignInText(){
        signIntext.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                textview1.visibility=View.GONE
                textView2Signup.visibility=View.GONE
                textView3Signup.visibility=View.GONE
                textView2.visibility=View.VISIBLE
                textView3.visibility=View.VISIBLE
                signIntext.visibility=View.GONE
                signUpButton.visibility=View.GONE
                signInButton.visibility=View.VISIBLE
                textviewHaveAcc.visibility=View.GONE
                textviewdontHaveAcc.visibility=View.VISIBLE
                signUptext.visibility=View.VISIBLE

                or_view.visibility=View.GONE
                gLogin.visibility=View.GONE
                fbLogin.visibility=View.GONE
                or_view1.visibility=View.VISIBLE
                gLogin1.visibility=View.VISIBLE
                fbLogin1.visibility=View.VISIBLE
            }
        })
    }

    private fun onclickSignUpText(){
        signUptext.setOnClickListener(View.OnClickListener {
            textview1.visibility=View.VISIBLE
            textView2Signup.visibility=View.VISIBLE
            textView3Signup.visibility=View.VISIBLE
            textView2.visibility=View.GONE

            textView3.visibility=View.GONE
            signIntext.visibility=View.VISIBLE
            signUpButton.visibility=View.VISIBLE
            signInButton.visibility=View.GONE
            textviewHaveAcc.visibility=View.VISIBLE
            textviewdontHaveAcc.visibility=View.GONE
            signUptext.visibility=View.GONE

            or_view.visibility=View.VISIBLE
            gLogin.visibility=View.VISIBLE
            fbLogin.visibility=View.VISIBLE
            or_view1.visibility=View.GONE
            gLogin1.visibility=View.GONE
            fbLogin1.visibility=View.GONE
        })
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }
}