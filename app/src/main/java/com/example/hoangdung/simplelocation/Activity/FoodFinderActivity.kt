package com.example.hoangdung.simplelocation.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*

import com.example.hoangdung.simplelocation.FirestoreCenter
import com.example.hoangdung.simplelocation.R

import butterknife.BindView
import butterknife.ButterKnife
import com.bcgdv.asia.lib.dots.DotsProgressIndicator
import com.cunoraz.tagview.Tag
import com.example.hoangdung.simplelocation.MyApplication
import com.example.hoangdung.simplelocation.NearestPlacesClient.NearestPlacesPOJO.NearestPlacesResponse
import com.example.hoangdung.simplelocation.NearestPlacesClient.NearestPlacesQuery
import com.example.hoangdung.simplelocation.ProgressWindowAnim
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.otaliastudios.autocomplete.Autocomplete
import kotlinx.android.synthetic.main.activity_food_finder.*
import me.xdrop.fuzzywuzzy.FuzzySearch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FoodFinderActivity : AppCompatActivity() {

    var Color = arrayOf("#89a6d4",
            "#9289d4",
            "#89ccd4",
            "#fbbc05",
            "#a6d489",
            "#4285f4",
            "#d489a6",
            "#ea4335",
            "#34a853"
            )

    var autocomplete: Autocomplete<String>? = null
    @BindView(R.id.food_edit_text)
    var editText: EditText? = null
    var chosenTags: ArrayList<Tag> =  ArrayList()
    var categories: ArrayList<String> = ArrayList()
    val categoriesChosen: HashMap<String,Boolean> = HashMap()
    lateinit var mLocationProvider: FusedLocationProviderClient
    private var progressWindow: ProgressWindowAnim<DotsProgressIndicator>? = null

    val MAX_SUGGESTION = 15;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_finder)
        ButterKnife.bind(this)
        progress_bar.visibility = View.VISIBLE
        mLocationProvider = LocationServices.getFusedLocationProviderClient(this@FoodFinderActivity)
        progressWindowConfig()
        //Initilize Autocomplete Food Categories
        //Get List of Food Categories from database
        FirestoreCenter.instance.getFoodCategories(object : FirestoreCenter.OnFoodCategoriesListener {
            override fun onComplete(task: Task<QuerySnapshot>) {
                if (task.isSuccessful) {
                    for (documentSnapshot in task.result) {
                       categories.add(documentSnapshot.data["tag"].toString())
                        categoriesChosen.put(documentSnapshot.data["tag"].toString(),false)
                    }
                    food_edit_text.requestFocus()
                } else {
                    Toast.makeText(this@FoodFinderActivity, "Something wrong! Try again later", Toast.LENGTH_LONG)
                    finish()
                }
                progress_bar.visibility = View.INVISIBLE
            }

        })

        //Set action bar
        setSupportActionBar(food_search_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //Set configuration layout for taview
        val layoutParams =  food_tag_view.layoutParams as FrameLayout.LayoutParams
        if(MyApplication.hasSoftNavBar(applicationContext))
        {
            layoutParams.bottomMargin+= MyApplication.getNavigationBarHeight(applicationContext,applicationContext.resources.configuration.orientation)
        }
        //Food Categories Suggestion
        food_edit_text.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //if user hasn't input anything text, display current added chosenTags
                if(s?.length == 0){
                    showTags(chosenTags)
                }
                else //if user input some text, display suggestion
                {
                    val matchingResults = FuzzySearch.extractSorted(s.toString(), categories,MAX_SUGGESTION);
                    val suggestTags = ArrayList<Tag>()
                    for(result in matchingResults){
                        //if that tags is already added into chosenTags, hide it
                        //else add it to the chosen string
                        if(categoriesChosen[result.string] == false){
                            val tag = Tag(result.string)
                            val generator = Random()
                            tag.isDeletable = false
                            tag.layoutColor = android.graphics.Color.parseColor(Color[generator.nextInt(Color.size)])
                            suggestTags.add(tag)
                        }

                    }
                    showTags(suggestTags)
                }
            }

        })
        //When user click on suggestion chosenTags
        food_tag_view.setOnTagClickListener{tag, i ->
            //Check if tag is suggestion one
            //Add it to chosen tag
            if(!food_edit_text.text.isEmpty()){
                tag.isDeletable = true
                chosenTags.add(tag)
                food_edit_text.setText("")
                showTags(chosenTags)
                categoriesChosen[tag.text] = true
            }
        }
        food_tag_view.setOnTagDeleteListener { tagView, tag, i ->
            chosenTags.removeAt(i)
            tagView.remove(i)
        }

        //
    }



    private fun showTags(tags: List<Tag>){
        food_tag_view.removeAll()
        food_tag_view.addTags(tags)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.food_finder_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.confirm)
        {
            //Query Nearest Shops
            //Get Current Location
            try {
                progressWindow?.showProgress()
                MyApplication.hideKeyboardFrom(this,food_edit_text)
                val task = mLocationProvider.lastLocation
                task.addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val location = task.result
                        val tags = ArrayList<String>()
                        for(item in chosenTags)
                        {
                            tags.add(item.text)
                        }
                        val query = NearestPlacesQuery.Builder()
                                .with(LatLng(location.latitude,location.longitude))
                                .with(tags)
                                .build()
                        query.query{nearestPlacesResponse: NearestPlacesResponse?, resultCode: Int ->
                            //If failure happens, display errors for users
                            if(resultCode==NearestPlacesQuery.RESPONSE_FAILURE)
                            {
                                Toast.makeText(this@FoodFinderActivity,"There are no shops near you!",Toast.LENGTH_LONG)
                                progressWindow?.hideProgress()
                                return@query
                            }
                            //Start FoodShopActivity
                            val intent = Intent(this@FoodFinderActivity,FoodResultActivity::class.java)
                            intent.putParcelableArrayListExtra("shops",nearestPlacesResponse?.shops)
                            startActivity(intent)
                            progressWindow?.hideProgress()
                        }
                    }
                    else{
                        Toast.makeText(this@FoodFinderActivity,"Something wrong try again",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            catch (security: SecurityException){
                Log.d("MapsActivity",security.message);
            }



        }
        else if(item?.itemId == android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
    private fun progressWindowConfig(){
        progressWindow = ProgressWindowAnim(this,R.layout.progress_window_layout)
    }

    override fun onBackPressed() {
        if(progressWindow?.isShowing!!)
            progressWindow?.hideProgress()
        else super.onBackPressed()

    }

}
