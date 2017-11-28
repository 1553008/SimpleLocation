package com.example.hoangdung.simplelocation

import android.util.Log
import com.google.firebase.firestore.*
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
    var dbAuth = FirestoreAuth.instance.dbAuth
    fun addUser(user: FireStoreUser){
        dbRef.collection(DB_USERS_PATH)
                .document(user.ID)
                .set(user, SetOptions.merge())
                .addOnSuccessListener{
                    Log.d("MapsActivity","User: " + dbAuth.currentUser?.uid!! + "is added successfully")
                }
    }

    @IgnoreExtraProperties
    abstract class FireStoreUser{
        abstract var ID: String
        @Exclude
        open fun parseJSON(json: JSONObject){
        }
    }
    @IgnoreExtraProperties
    class FacebookUser : FireStoreUser(){
        override lateinit var ID: String
        lateinit var first_name: String
        lateinit var last_name: String
        lateinit var photo_url: String
        lateinit var email: String
        @Exclude
        override fun parseJSON(json: JSONObject) {
            super.parseJSON(json)
            try {
                ID = FirestoreAuth.instance.dbAuth.uid!!
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