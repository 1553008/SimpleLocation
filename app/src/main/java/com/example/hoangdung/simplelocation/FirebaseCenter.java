package com.example.hoangdung.simplelocation;

import android.support.annotation.NonNull;

import com.example.hoangdung.simplelocation.Interface.FirebaseAuthCommand;
import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
/**
 * Created by USER on 11/17/2017.
 */


public class FirebaseCenter {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public void handleFacebookAccessToken(AccessToken token, final FirebaseAuthCommand cmd)
    {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                            cmd.onSuccess();
                        else
                            cmd.onFail();
                    }
                });
    }


}
