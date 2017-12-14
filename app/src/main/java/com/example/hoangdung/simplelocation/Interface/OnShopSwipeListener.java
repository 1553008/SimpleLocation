package com.example.hoangdung.simplelocation.Interface;

import com.example.hoangdung.simplelocation.NearestPlacesClient.NearestPlacesPOJO.FoodShop;

/**
 * Created by hoangdung on 12/11/17.
 */

public interface OnShopSwipeListener {
    public void onSwiped(FoodShop foodShop, int position, int mode);
}
