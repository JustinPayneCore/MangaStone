package com.bcit.manga_stone.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class AnonymousAuth {

    /**
     * Signs a user in Anonymously to Firebase. The user is specific to the device.
     */
    public static void SignInAnonymously() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null) {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("AnonymousAuth", "signInAnonymously:success");
                                FirebaseUser account = FirebaseAuth.getInstance().getCurrentUser();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("AnonymousAuth", "signInAnonymously:failure", task.getException());

                            }
                        }
                    });
        }
    }
}
