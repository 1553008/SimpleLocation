package com.example.hoangdung.simplelocation

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Created by hoangdung on 11/25/17.
 * Firestore Authentication using Firebase Authentication
 */
public class FirestoreAuth{
    public constructor(){
        dbAuth = FirebaseAuth.getInstance();

    }

    private object Holder{
        val instance = FirestoreAuth()
    }
    companion object {
        val instance: FirestoreAuth = FirestoreAuth()
    }

    var dbAuth : FirebaseAuth
}