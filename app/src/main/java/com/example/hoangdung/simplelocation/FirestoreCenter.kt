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
import java.util.EventListener
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
    public fun getPhotos(shopID: Long,numOfPhotos: Long, lastEndSnapshot: DocumentSnapshot?, callback: OnCompleteListener<QuerySnapshot>){
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

    public fun getReviews(shopID: Long, numOfReviews: Long, lastEndSnapshot: DocumentSnapshot?, callback: OnCompleteListener<QuerySnapshot>){
        val foodReviewsRef = dbRef.collection(FirestoreCenter.FOOD_SHOPS_PATH)
                .document(shopID.toString())
                .collection("food_reviews")
        if(lastEndSnapshot != null){
            foodReviewsRef.limit(numOfReviews)
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
    fun listenToReviewChanges(shopID: Long, callback: com.google.firebase.firestore.EventListener<QuerySnapshot>): ListenerRegistration{
        return dbRef.collection(FOOD_SHOPS_PATH)
                .document(shopID.toString())
                .collection("food_reviews")
                .addSnapshotListener(callback)
    }
    fun getUser(userID: String, callback: OnCompleteListener<DocumentSnapshot>){
        val userRef = dbRef.collection(DB_USERS_PATH)
                .document(userID)
                .get()
                .addOnCompleteListener(callback)
    }
    fun publishReview(shopID: Long,review: FoodShopReview, callback: OnCompleteListener<Nothing>){
        val docRef =  dbRef.collection(FirestoreCenter.FOOD_SHOPS_PATH)
                .document(shopID.toString())
                .collection("food_reviews")
                .document()
        dbRef.runTransaction { transaction ->
            val snapshot = transaction.get(
                    dbRef.collection(FOOD_SHOPS_PATH).document(shopID.toString())
            )
            //Get old ratings and calculate new ratings
            var oldNumOfRatings = snapshot.get("numOfRatings") as Long
            var newNumRatings = oldNumOfRatings + 1;
            var oldTotalRatings = snapshot.get("averageRatings") as Double  * oldNumOfRatings.toDouble()
            var newTotalRatings = (oldTotalRatings + review.ratings)/newNumRatings

            //Insert new comment
            transaction.set(docRef,review.toMap())

            //Update ratings
            transaction.update(dbRef.collection(FOOD_SHOPS_PATH).document(shopID.toString()),"averageRatings",newTotalRatings)
            transaction.update(dbRef.collection(FOOD_SHOPS_PATH).document(shopID.toString()),"numOfRatings",newNumRatings)
            Log.d("MapsActivity","Success publish review")
            return@runTransaction null
        }.addOnCompleteListener(callback)
    }
    fun listenToShop(shopID: Long, callback: com.google.firebase.firestore.EventListener<DocumentSnapshot>): ListenerRegistration{
        return dbRef.collection(FOOD_SHOPS_PATH)
                .document(shopID.toString())
                .addSnapshotListener(callback)
    }
}