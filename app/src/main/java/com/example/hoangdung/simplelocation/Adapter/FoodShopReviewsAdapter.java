package com.example.hoangdung.simplelocation.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hoangdung.simplelocation.FirestoreCenter;
import com.example.hoangdung.simplelocation.NearestPlacesClient.FoodShopReview;
import com.example.hoangdung.simplelocation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by hoangdung on 12/12/17.
 */

public class FoodShopReviewsAdapter extends RecyclerView.Adapter<FoodShopReviewsAdapter.FoodShopReviewsViewHolder> {

    ArrayList<FoodShopReview> foodShopReviews = new ArrayList<>();
    Context context;
    @Override
    public FoodShopReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.food_shop_reviews_item,parent,false);
        if(context==null)
            context = parent.getContext();
        return new FoodShopReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FoodShopReviewsViewHolder holder, int position) {
        FoodShopReview foodShopReview = foodShopReviews.get(position);

        //Load User Information
        FirestoreCenter.Companion.getInstance().getUser(
                foodShopReview.userID,
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            holder.userName.
                                    setText(task.getResult().get("last_name").toString() + " " + task.getResult().get("first_name").toString());
                            Picasso.with(context)
                                    .load((String) task.getResult().get("photo_url"))
                                    .into(holder.imageProfile);
                        }

                    }
                }
        );
        //Load review content
        holder.reviewContent.setText(foodShopReview.comment);
        holder.reviewRatings.setRating(foodShopReview.ratings);
        holder.date.setText(foodShopReview.toString());
    }

    @Override
    public int getItemCount() {
        if(foodShopReviews == null)
            return 0;
        return foodShopReviews.size();
    }

    public class FoodShopReviewsViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.review_content)
        public TextView reviewContent;

        @BindView(R.id.review_ratings_bar)
        public MaterialRatingBar reviewRatings;

        @BindView(R.id.review_user_name)
        public TextView userName;

        @BindView(R.id.review_date)
        public TextView date;

        @BindView(R.id.profile_image)
        public CircleImageView imageProfile;
        public FoodShopReviewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public ArrayList<FoodShopReview> getFoodShopReviews() {
        return foodShopReviews;
    }

    public void setFoodShopReviews(ArrayList<FoodShopReview> foodShopReviews) {
        this.foodShopReviews = foodShopReviews;
    }
}
