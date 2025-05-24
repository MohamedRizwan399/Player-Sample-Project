package com.example.player_sample_project.authentication

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

val auth: FirebaseAuth = FirebaseAuth.getInstance()

suspend fun handleCreateUserWithEmailAndPassword(email: String, password: String): Map<String, Any> {
    return try {
        val userCredential = auth.createUserWithEmailAndPassword(email, password).await()
        mapOf("response" to 200, "data" to userCredential)
    } catch (e: Exception) {
        mapOf("response" to 401, "data" to e)
    }
}

suspend fun handleSignInWithEmailAndPassword(email: String, password: String): Map<String, Any> {
    return try {
        val userCredential = auth.signInWithEmailAndPassword(email, password).await()
        mapOf("response" to 200, "data" to userCredential)
    } catch (e: Exception) {
        mapOf("response" to 401, "data" to e)
    }
}

