package com.example.hoangdung.simplelocation.NearestPlacesClient;

import java.util.ArrayList;

/**
 * Created by hoangdung on 12/8/17.
 */

public class NearestPlacesParamsBuilder {
    public static String getFoodCategoriesParams(ArrayList<String> list){
        StringBuilder builder = new StringBuilder();
        for(String item : list){
            item.replace(' ','+');
            builder.append(item);
            builder.append('|');
        }
        return builder.toString();
    }
}
