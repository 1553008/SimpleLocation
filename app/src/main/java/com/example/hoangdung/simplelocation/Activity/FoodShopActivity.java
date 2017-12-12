package com.example.hoangdung.simplelocation.Activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoangdung.simplelocation.Adapter.FoodShopPhotosAdapter;
import com.example.hoangdung.simplelocation.Adapter.FoodShopReviewsAdapter;
import com.example.hoangdung.simplelocation.EndlessRecyclerOnScrollListener;
import com.example.hoangdung.simplelocation.FirestoreCenter;
import com.example.hoangdung.simplelocation.NearestPlacesClient.FoodShopReview;
import com.example.hoangdung.simplelocation.NearestPlacesClient.NearestPlacesPOJO.FoodShop;
import com.example.hoangdung.simplelocation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class FoodShopActivity extends AppCompatActivity {

    @BindView(R.id.food_shop_name)
    public TextView foodShopName;

    @BindView(R.id.food_shop_address)
    public TextView foodShopAdress;

    @BindView(R.id.food_shop_avartar)
    public ImageView foodShopAvartar;


    @BindView(R.id.food_shop_num_reviews)
    public TextView foodShopNumReviews;

    @BindView(R.id.food_shop_num_photos)
    public TextView foodShopNumPhotos;

    @BindView(R.id.food_shop_ratings_text)
    public TextView foodShopRatingText;

    @BindView(R.id.food_shop_photos)
    public RecyclerView foodShopPhotos;

    @BindView(R.id.food_shop_ratings)
    public MaterialRatingBar foodShopEditRatingsBar;

    @BindView(R.id.food_shop_comment_edit_text)
    public EditText foodShopCommentEdit;

    @BindView(R.id.food_shop_reviews_list)
    public RecyclerView foodShopReviewsList;

    @BindView(R.id.foodShopSlidingPanel)
    public SlidingUpPanelLayout foodShopSlidingPanel;

    @BindView(R.id.food_shop_reviews_header)
    public LinearLayout foodShopReviewsHeader;

    @BindView(R.id.food_shop_publish_review)
    public ImageButton foodShopPublishReview;

    FoodShop foodShop;
    boolean isEditRatingBarValid = false;

    //Foodshop Photos data
    private int nextMaxPhotos = 20;
    private int numOfTotalPhotos;
    private DocumentSnapshot lastPhotoSnapshot;

    //Foodshop Reviews data
    private int nextMaxReviews = 20;
    private int numOfTotalsReviews;
    private DocumentSnapshot lastReviewSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_shop);
        ButterKnife.bind(this);

        foodShop = getIntent().getParcelableExtra("shop");
        setupFoodShopInfo();
    }

    //Setup FoodShop Information
    void setupFoodShopInfo() {
        setupShopContent();
        setupPhotosContent();
        setupReviewsContent();
        //Set up Sliding Panel Layout
        foodShopReviewsHeader.getViewTreeObserver()
                .addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                foodShopSlidingPanel.setPanelHeight(foodShopReviewsHeader.getHeight());
                                foodShopReviewsHeader.getViewTreeObserver()
                                        .removeOnGlobalLayoutListener(this);
                            }
                        }
                );

    }

    private void setupPhotosContent() {
        //Init ListView
        foodShopPhotos.setHasFixedSize(true);
        final FoodShopPhotosAdapter adapter = new FoodShopPhotosAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        foodShopPhotos.setLayoutManager(layoutManager);
        foodShopPhotos.setAdapter(adapter);
        //Query the first 20 photos
        //Get Photos Collection Reference of Food Shop
        FirestoreCenter.Companion.getInstance().getPhotos(
                foodShop.shopID,
                nextMaxPhotos,
                null,
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            ArrayList<String> photoUrls = new ArrayList<>();
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                photoUrls.add((String) documentSnapshot.get("photoUrl"));
                            }
                            adapter.setPhotosUrl(photoUrls);
                            adapter.notifyDataSetChanged();
                            lastPhotoSnapshot =  task.getResult().getDocuments().get( task.getResult().getDocuments().size() - 1);
                        }

                    }


                }
        );
        //Implements EndlessScroll For RecyclerView
        foodShopPhotos.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                //Find the number of remaining phots
                int restNumOfPhotos = numOfTotalPhotos - previousTotal;
                Log.d("MapsActivity", "numOfTotalPhotos: " + String.valueOf(numOfTotalPhotos));
                Log.d("MapsActivity", "previousTotal: " + String.valueOf(previousTotal));
                Log.d("MapsActivity", "rest: " + String.valueOf(restNumOfPhotos));
                //If there are no photos to query, return
                if (restNumOfPhotos == 0)
                    return;
                //If it is larger than number of photos we going to load next, use nextMaxPhotos to query
                int numOfNextPhotos = 0;
                if (restNumOfPhotos >= nextMaxPhotos) {
                    numOfNextPhotos = nextMaxPhotos;
                } else //use number of remaining photos to query
                {
                    numOfNextPhotos = restNumOfPhotos;
                }
                final int finalNumOfNextPhotos = numOfNextPhotos;
                FirestoreCenter.Companion.getInstance().getPhotos(
                        foodShop.shopID,
                        numOfNextPhotos,
                        lastPhotoSnapshot,
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    ArrayList<String> photoUrls = adapter.getPhotosUrl();
                                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                        photoUrls.add((String) documentSnapshot.get("photoUrl"));
                                    }
                                    lastPhotoSnapshot = task.getResult().getDocuments().get(task.getResult().getDocuments().size() - 1);
                                    adapter.notifyItemRangeInserted(previousTotal, task.getResult().getDocuments().size() - 1);
                                }

                            }
                        }
                );
            }
        });
    }
    private void setupReviewsContent() {
        //Setup reviews recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        foodShopReviewsList.setLayoutManager(linearLayoutManager);
        final FoodShopReviewsAdapter adapter = new FoodShopReviewsAdapter();
        foodShopReviewsList.setAdapter(adapter);
        //Set EndlessScroll for endless data
        foodShopReviewsList.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                int restNumOfReviews = numOfTotalsReviews - previousTotal;
                Log.d("MapsActivity", "numOfTotalReviews: " + String.valueOf(numOfTotalPhotos));
                Log.d("MapsActivity", "previousTotal: " + String.valueOf(previousTotal));
                Log.d("MapsActivity", "rest: " + String.valueOf(restNumOfReviews));
                //If there are no reviews to query, return
                if (restNumOfReviews == 0)
                    return;
                //If it is larger than number of photos we going to load next, use nextMaxPhotos to query
                int nextNumOfReviews = 0;
                if (restNumOfReviews >= nextMaxReviews) {
                    nextNumOfReviews = nextMaxReviews;
                } else //use number of remaining photos to query
                {
                    nextNumOfReviews = restNumOfReviews;
                }
                FirestoreCenter.Companion.getInstance().getReviews(
                        foodShop.shopID,
                        nextNumOfReviews,
                        lastReviewSnapshot,
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    if (task.getResult().getDocuments().size() == 0)
                                        return;
                                    ArrayList<FoodShopReview> reviewsArrayList = new ArrayList<>();
                                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                        reviewsArrayList.add(documentSnapshot.toObject(FoodShopReview.class));
                                    }
                                    lastReviewSnapshot = task.getResult().getDocuments().get(task.getResult().getDocuments().size()-1);
                                    adapter.setFoodShopReviews(reviewsArrayList);
                                    adapter.notifyItemRangeInserted(previousTotal, task.getResult().getDocuments().size() - 1);
                                }

                            }
                        }
                );
            }
        });
        //Query first 20 reviews
        FirestoreCenter.Companion.getInstance().getReviews(
                foodShop.shopID,
                nextMaxReviews,
                null,
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if (task.getResult().getDocuments().size() == 0)
                                return;
                            ArrayList<FoodShopReview> reviewsArrayList = new ArrayList<>();
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                reviewsArrayList.add(documentSnapshot.toObject(FoodShopReview.class));
                            }
                            lastReviewSnapshot = task.getResult().getDocuments().get(task.getResult().getDocuments().size()-1);
                            adapter.setFoodShopReviews(reviewsArrayList);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
        );
    }
    private void setupShopContent(){
        Log.d("MapsActivity","shopid: " + String.valueOf(foodShop.shopID));
        //Load Image
        Picasso.with(this)
                .load(foodShop.avartar)
                .fit()
                .into(foodShopAvartar);
        //Display Food Shop Name
        foodShopName.setText(foodShop.name);
        //Display Food Shop Adress
        foodShopAdress.setText(foodShop.address);
        //Display Food Shop Num Reviews
        foodShopNumReviews.setText(String.valueOf(foodShop.numOfRatings));
        numOfTotalsReviews = foodShop.numOfRatings;
        //Display Food Shop Ratings
        foodShopRatingText.setText(String.valueOf(foodShop.averageRatings));
        //Display Food Shop Num Photos
        foodShopNumPhotos.setText(String.valueOf(foodShop.numOfPhotos));
        numOfTotalPhotos = foodShop.numOfPhotos;
        //Add listener for Rating bars
        foodShopEditRatingsBar.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                if(rating == 0)
                    isEditRatingBarValid = false;
                else
                    isEditRatingBarValid = true;
                ratingBar.setRating(rating);

            }
        });
        foodShopPublishReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditRatingBarValid && !foodShopCommentEdit.getText().toString().isEmpty()){
                    FoodShopReview review = new FoodShopReview();
                    review.comment = foodShopCommentEdit.getText().toString();
                    review.ratings = foodShopEditRatingsBar.getRating();
                    review.userID = FirestoreCenter.Companion.getInstance().getDbAuth().getUid();
                    FirestoreCenter.Companion.getInstance().publishReview(
                            foodShop.shopID,
                            review,
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(FoodShopActivity.this, "Your comment is added", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    );
                }
                else{

                }
            }
        });
    }
}
