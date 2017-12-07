package com.example.hoangdung.simplelocation

import android.util.Log
import com.google.android.gms.tasks.Task
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
    val FOOD_CATEGORIES_PATH = "food_categories"
    val FOOD_SHOPS_PATH = "food_shops"
    val FOOD_METADATA_PATH = "food_metadata"

    var dbRef = FirebaseFirestore.getInstance()
    var dbAuth = FirestoreAuth.instance.dbAuth


    private object Holder{
        val instance = FirestoreCenter()
    }
    companion object {
        val instance: FirestoreCenter = FirestoreCenter()
    }


    /**
     * Query Food Categories for auto complete
     */
    interface OnFoodCategoriesListener {
        fun onComplete(task: Task<QuerySnapshot>);
    }
    fun getFoodCategories(listener: OnFoodCategoriesListener){
        dbRef.collection(FOOD_CATEGORIES_PATH)
                .get()
                .addOnCompleteListener { task->
                    listener.onComplete(task)
                }
    }
    /**
     * Add New User to database using Firebase Authentication
     */
    fun addUser(user: FireStoreUser){
        dbRef.collection(DB_USERS_PATH)
                .document(user.id)
                .set(user, SetOptions.merge())
                .addOnSuccessListener{
                    Log.d("MapsActivity","User: " + dbAuth.currentUser?.uid!! + "is added successfully")
                }
    }

    @IgnoreExtraProperties
    abstract class FireStoreUser{
        abstract var id: String
        @Exclude
        open fun parseJSON(json: JSONObject){
        }
    }
    @IgnoreExtraProperties
    class FacebookUser : FireStoreUser(){
        override lateinit var id: String
        lateinit var first_name: String
        lateinit var last_name: String
        lateinit var photo_url: String
        lateinit var email: String
        @Exclude
        override fun parseJSON(json: JSONObject) {
            super.parseJSON(json)
            try {
                id = FirestoreAuth.instance.dbAuth.uid!!
                first_name = json.getString("first_name")
                last_name = json.getString("last_name")
                email = json.getString("email")
                photo_url = json.getJSONObject("picture").getJSONObject("categories").getString("url")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
}