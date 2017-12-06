package com.example.hoangdung.simplelocation;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.hoangdung.simplelocation.Interface.FirebaseAuthCommand;
import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by USER on 11/17/2017.
 */


// Singleton
public class FirebaseCenter {
    private static FirebaseCenter firebaseCenter = new FirebaseCenter();

    private FirebaseCenter(){}

    public static FirebaseCenter getInstance(){return  firebaseCenter;}


    public ArrayList<Location> getMyPlaces() {
        return myPlaces;
    }

    private ArrayList<Location> myPlaces = new ArrayList<Location>();
    private String userID; // facebook userID

    public void removePlace(List<String> labels, DatabaseReference.CompletionListener completionListener)
    {
        Map<String, Object> map = new HashMap<>();
        for (String label: labels)
            map.put("myplace/" + userID + "/"+ label,null);
       FirebaseDatabase.getInstance().getReference().updateChildren(map, completionListener);
    }
    public String getUserID()
    {
        return userID;
    }

    public void setUserID(String userID)
    {
        this.userID = userID;
    }

    static public class Location implements Parcelable
    {
      public String placeID;
      public transient String label; // will not serialize this field
      public String address;
      public double lat;
      public double lng;
      public Location(String placeID, String label, String address, double lat, double lng)
      {
          this.placeID = placeID;
          this.label = label;
          this.address = address;
          this.lat = lat;
          this.lng = lng;
      }
      public Location(Parcel in)
      {
          placeID = in.readString();
          label = in.readString();
          address = in.readString();
          lat = in.readDouble();
          lng = in.readDouble();
      }

        public Location() {

        }

        @Override
        public String toString() {
            return "Location{" +
                    "placeID='" + placeID + '\'' +
                    ", label='" + label + '\'' +
                    ", address='" + address + '\'' +
                    ", lat=" + lat +
                    ", lng=" + lng +
                    '}';
        }

        public  static final Parcelable.Creator CREATOR = new Parcelable.Creator()
      {
          @Override
          public Object createFromParcel(Parcel source) {
              return new Location(source);
          }

          @Override
          public Location[] newArray(int size) {
              return new Location[size] ;
          }
      };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(placeID);
            dest.writeString(label);
            dest.writeString(address);
            dest.writeDouble(lat);
            dest.writeDouble(lng);
        }
    }

    // add new place to my place list
    public  void addPlace(String userID, FirebaseCenter.Location place, DatabaseReference.CompletionListener completionListener)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("myplace/" + userID);
        ref = ref.child(place.label);
        ref.setValue(place, completionListener);
    }

    // start keep track of my place list on database
    public void listenForMyPlaceDatabase()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("myplace/" + this.userID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myPlaces.clear();
                // for each place
                for (DataSnapshot placeSnapShot: dataSnapshot.getChildren())
                {
                    // add it to list
                    Location place = placeSnapShot.getValue(Location.class);
                    place.label = placeSnapShot.getKey();
                    myPlaces.add(place);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void handleFacebookAccessToken(AccessToken token, final FirebaseAuthCommand cmd)
    {
        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        userID = token.getUserId();
        FirestoreAuth.Companion.getInstance().getDbAuth().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            listenForMyPlaceDatabase();
                            cmd.onSuccess();
                        }
                        else
                            cmd.onFail();
                    }
                });
    }
}
