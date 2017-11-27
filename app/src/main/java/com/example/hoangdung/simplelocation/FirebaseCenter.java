package com.example.hoangdung.simplelocation;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

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

    static public class Location implements Parcelable
    {
      public String placeID;
      public String label;
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

    // start keep track of my place list on database
    public void listenForMyPlaceDatabase()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("myplace/user1");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
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
