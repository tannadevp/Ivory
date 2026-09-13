package com.example.ivory.data.firebase

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Defers Firebase initialization until an authentication action is requested.
 * This keeps the app navigable when a development build has no google-services.json yet.
 */
@Singleton
class FirebaseAuthProvider @Inject constructor() {
    fun getOrNull(): FirebaseAuth? = runCatching { FirebaseAuth.getInstance() }.getOrNull()
}
