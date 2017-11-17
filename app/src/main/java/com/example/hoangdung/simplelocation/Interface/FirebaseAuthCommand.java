package com.example.hoangdung.simplelocation.Interface;

/**
 * Created by USER on 11/17/2017.
 */

// interface contains functions called after Firebase authentication complete.
public interface FirebaseAuthCommand {
    void onSuccess();
    void onFail();
}
