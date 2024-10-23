package com.example.player_sample_project.activity

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

        FirebaseDynamicLinks.getInstance().getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Getting deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }

                // Display deep link in the UI
                if (deepLink != null) {
                    Toast.makeText(getApplicationContext(),"FoundDeepLink!", Toast.LENGTH_LONG).show()
                } else {
                    Log.d("error", "getDynamicLink: no link found")
                }
            }
            .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }
    }

    fun buildDeepLink(deepLink: Uri): Uri {
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse(deepLink.toString()))
            .setDomainUriPrefix("https://playersampleproject.page.link/dlinkshare0704")
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build()) // Open links with this app on Android
            //.setIosParameters(DynamicLink.IosParameters.Builder("com.example.ios").build()) // Open links with com.example.ios on iOS
            .buildDynamicLink()

        val dynamicLinkUri = dynamicLink.uri
        return dynamicLinkUri;
    }

    companion object {
        const val DEEP_LINK_URL = "https://playersampleproject/"
    }

}
