package com.example.hoangdung.simplelocation.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

import com.example.hoangdung.simplelocation.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FoodResultActivity extends AppCompatActivity {

    @BindView(R.id.food_result_toolbar)
    Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_result);
        ButterKnife.bind(this);
    }
}
