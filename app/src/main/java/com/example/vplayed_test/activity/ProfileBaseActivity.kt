package com.example.vplayed_test.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.example.vplayed_test.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class ProfileBaseActivity : AppCompatActivity() {

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
//    private lateinit var textView2Signup:TextInputLayout
//    private lateinit var textView3Signup:TextInputLayout
//    private lateinit var textView2:TextInputLayout
//    private lateinit var textView3:TextInputLayout


    private lateinit var signUpButton:MaterialButton
    private lateinit var signInButton: MaterialButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_base)
        signIntext=findViewById(R.id.signIntext)
        signUptext=findViewById(R.id.signUptext)

        textviewHaveAcc=findViewById(R.id.txt_view)
        textviewdontHaveAcc=findViewById(R.id.txt_viewNew)

        textview1=findViewById(R.id.textview1)
//        textView2Signup=findViewById(R.id.textview2)
//        textView3Signup=findViewById(R.id.textview3)
//        textView2=findViewById(R.id.textview2Signinpage)
//        textView3=findViewById(R.id.textview3Signinpage)

        signUpButton=findViewById(R.id.btn)
        signInButton=findViewById(R.id.btnn)

        or_view=findViewById(R.id.or_view)
        or_view1=findViewById(R.id.or_view1)
        gLogin=findViewById(R.id.sign_in_button)
        gLogin1=findViewById(R.id.sign_in_button1)
        fbLogin=findViewById(R.id.facebook_signin)
        fbLogin1=findViewById(R.id.facebook_signin1)

        signIntext.setOnClickListener {
            textview1.visibility=View.GONE
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
        signUptext.setOnClickListener {
            textview1.visibility=View.VISIBLE
//            textView2Signup.visibility=View.GONE
//            textView2.visibility=View.VISIBLE
//            textView3Signup.visibility=View.GONE
//            textView3.visibility=View.VISIBLE

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
        }

    }
}