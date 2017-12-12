package com.example.hoangdung.simplelocation

import android.util.Log
import com.example.hoangdung.simplelocation.NearestPlacesClient.FoodShopReview
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by hoangdung on 11/25/17.
 * Singleton class
 * DAO for managing Firestore database
 */

public class FirestoreCenter {


    var dbRef = FirebaseFirestore.getInstance()
    var dbAuth = FirestoreAuth.instance.dbAuth


    private object Holder{
        val instance = FirestoreCenter()
    }
    companion object {
        val instance: FirestoreCenter = FirestoreCenter()
        val DB_USERS_PATH = "users"
        val FOOD_CATEGORIES_PATH = "food_categories"
        val FOOD_SHOPS_PATH = "food_shops"
        val FOOD_METADATA_PATH = "food_metadata"
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
                .set(user.toMap(), SetOptions.merge())
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
        @Exclude
        abstract fun toMap(): Map<String,Any>
    }
    @IgnoreExtraProperties
    class FacebookUser : FireStoreUser(){
        override lateinit var id: String
        public lateinit var first_name: String
        public lateinit var last_name: String
        public lateinit var photo_url: String
        public lateinit var email: String
        @Exclude
        override fun parseJSON(json: JSONObject) {
            super.parseJSON(json)
            try {
                id = FirestoreAuth.instance.dbAuth.uid!!
                first_name = json.getString("first_name")
                last_name = json.getString("last_name")
                email = json.getString("email")
                photo_url = json.getJSONObject("picture").getJSONObject("data").getString("url")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        @Exclude
        override fun toMap(): Map<String, Any> {
            val map = HashMap<String,Any>()
            map.put("id",id)
            map.put("first_name",first_name)
            map.put("last_name",last_name);
            map.put("email",email)
            map.put("photo_url",photo_url)
            return map
        }
    }
    public fun getPhotos(shopID: Int,numOfPhotos: Int, lastEndSnapshot: DocumentSnapshot?, callback: OnCompleteListener<QuerySnapshot>){
        val photosRef = dbRef.collection(FirestoreCenter.FOOD_SHOPS_PATH)
                .document(shopID.toString())
                .collection("photos")
        if(lastEndSnapshot != null)
        {
            photosRef.limit(numOfPhotos.toLong())
                    .startAfter(lastEndSnapshot!!)
                    .get()
                    .addOnCompleteListener(callback)
        }
        else{
            photosRef.limit(numOfPhotos.toLong())
                    .get()
                    .addOnCompleteListener(callback)
        }

    }

    public fun getReviews(shopID: Int, numOfReviews: Int, lastEndSnapshot: DocumentSnapshot?, callback: OnCompleteListener<QuerySnapshot>){
        val foodReviewsRef = dbRef.collection(FirestoreCenter.FOOD_SHOPS_PATH)
                .document(shopID.toString())
                .collection("food_reviews")
        if(lastEndSnapshot != null){
            foodReviewsRef.limit(numOfReviews.toLong())
                    .orderBy("timestamp",Query.Direction.DESCENDING)
                    .startAfter(lastEndSnapshot!!)
                    .get()
                    .addOnCompleteListener(callback)
        }
        else{
            foodReviewsRef.limit(numOfReviews.toLong())
                    .orderBy("timestamp",Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(callback)
        }
    }
    fun getUser(userID: String, callback: OnCompleteListener<DocumentSnapshot>){
        val userRef = dbRef.collection(DB_USERS_PATH)
                .document(userID)
                .get()
                .addOnCompleteListener(callback)
    }
    public fun publishReview(shopID: Int,review: FoodShopReview, callback: OnCompleteListener<Void>){
        FieldValue.serverTimestamp()
        val docRef =  dbRef.collection(FirestoreCenter.FOOD_SHOPS_PATH)
                .document(shopID.toString())
                .collection("food_reviews")
                .document()
        docRef.set(review.toMap())
                .addOnCompleteListener(callback)
    }
}