package com.example.hoangdung.simplelocation

import com.google.firebase.auth.FirebaseAuth

/**
 * Created by hoangdung on 11/25/17.
 * Firestore Authentication using Firebase Authentication
 */
public class FirestoreAuth{

    private object Holder{
        val instance = FirestoreAuth()
    }
    companion object {
        val instance: FirestoreAuth by lazy { Holder.instance }
    }

    public val dbAuth = FirebaseAuth.getInstance()

}