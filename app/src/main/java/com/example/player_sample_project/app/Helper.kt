package com.example.player_sample_project.app

import com.google.firebase.auth.FirebaseAuth

object FirebaseAuthInstance {
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
}
