package com.example.hoangdung.simplelocation

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by hoangdung on 11/25/17.
 * Singleton class
 * DAO for managing Firestore database
 */

public class FirestoreCenter {

    val DB_USERS_PATH = "users"
    private object Holder{
        val instance = FirestoreCenter()
    }
    companion object {
        val instance: FirestoreCenter by lazy { Holder.instance }
    }

    var dbRef = FirebaseFirestore.getInstance()
    var dbAuth = FirebaseAuth.getInstance()
    fun addUser(user: FireStoreUser){
        dbRef.collection(DB_USERS_PATH).document(user.ID).set(user, SetOptions.merge())
    }

    abstract class FireStoreUser{
        lateinit var ID: String
        open fun parseJSON(ID:String, json: JSONObject){
            this.ID = ID
        }
    }
    class FacebookUser : FireStoreUser(){
        lateinit var first_name: String
        lateinit var last_name: String
        lateinit var photo_url: String
        lateinit var email: String
        override fun parseJSON(ID: String, json: JSONObject) {
            super.parseJSON(ID, json)
            try {
                first_name = json.getString("first_name")
                last_name = json.getString("last_name")
                email = json.getString("email")
                photo_url = json.getJSONObject("picture").getJSONObject("data").getString("url")
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
    }
}