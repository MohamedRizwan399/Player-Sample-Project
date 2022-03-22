package com.example.vplayed_test.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

class DynamicLinkShare:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Creating a deep link and display it in the UI
//        val newDeepLink = buildDeepLink(Uri.parse(DEEP_LINK_URL))
//        linkViewSend.text = newDeepLink.toString()

        // Share button click listener


        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Getting deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }

                // Handle the deep link. For example, opening the linked
                // content, or applying promotional credit to the user's
                // account.
                // ...

                // Display deep link in the UI
                if (deepLink != null) {
                    Toast.makeText(getApplicationContext(),"FoundDeepLink!", Toast.LENGTH_LONG).show()
//                    linkViewReceive.text = deepLink.toString()
                } else {
                    Log.d(TAG, "getDynamicLink: no link found")
                }
            }
            .addOnFailureListener(this) { e -> Log.w(TAG, "getDynamicLink:onFailure", e) }
    }

    fun buildDeepLink(deepLink: Uri): Uri {
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse(deepLink.toString()))
            .setDomainUriPrefix("https://vplayedtestapp.page.link/share0704")
            // Open links with this app on Android
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())


            // Open links with com.example.ios on iOS
//            .setIosParameters(DynamicLink.IosParameters.Builder("com.example.ios").build())
            .buildDynamicLink()

        val dynamicLinkUri = dynamicLink.uri

        return dynamicLinkUri;
    }

    fun shareDeepLink(deepLink: String,context: Context) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "FirebaseDeepLink")
        intent.putExtra(Intent.EXTRA_TEXT, deepLink)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(Intent.createChooser(intent,"Share Via"))
    }

    companion object {

        const val TAG = "MainActivity"
        const val DEEP_LINK_URL = "https://vplayedtestapp/"
    }

    }
