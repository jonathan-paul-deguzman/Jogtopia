package com.jpdeguzman.jogtopia.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jpdeguzman.jogtopia.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jonathan.deguzman on 9/17/17.
 */

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.login_email)
    EditText mLoginEmail;

    @BindView(R.id.login_password)
    EditText mLoginPassword;

    private FirebaseAuth mFirebaseAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    Toast.makeText(
                            getApplicationContext(),
                            R.string.user_signed_in + currentUser.getDisplayName(),
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            R.string.user_signed_out + currentUser.getDisplayName(),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intentToMainScreen = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intentToMainScreen);
        }
    }

    @OnClick({R.id.login_button, R.id.register_link})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                signIn(mLoginEmail.getText().toString(), mLoginPassword.getText().toString());
                break;
            case R.id.register_link:
                Intent intentToRegister = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intentToRegister);
                break;
        }
    }

    public void signIn(String email, String password) {
        if (!isValidForm()) return;
        showProgressDialog();
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Intent intentToMainScreen = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intentToMainScreen);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

    private boolean isValidForm() {
        boolean isEmailValid = checkEmailAddress();
        boolean isPasswordValid = checkPassword();
        return isEmailValid && isPasswordValid;
    }

    private boolean checkEmailAddress() {
        return checkForEmptyField(mLoginEmail);
    }

    private boolean checkPassword() {
        return checkForEmptyField(mLoginPassword);
    }

    private boolean checkForEmptyField(EditText editText) {
        String field = editText.getText().toString();
        if (TextUtils.isEmpty(field)) {
            editText.setError("Required");
            return false;
        } else {
            editText.setError(null);
            return true;
        }
    }
}
