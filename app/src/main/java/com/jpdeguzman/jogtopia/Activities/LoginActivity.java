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
 *  LoginActivity serves the purpose of allowing users to login to their Jogtopia account by
 *  providing their email address and password. For users that do not have an account, clicking
 *  the "No Account Yet? Register here" button will take them to {@link RegisterActivity}. Upon
 *  entering the user's valid credentials, the user will be taken to {@link MainActivity}.
 *  Otherwise, a failed authentication will be shown.
 */

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.login_email) EditText mLoginEmail;
    @BindView(R.id.login_password) EditText mLoginPassword;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "onStart:userIsAlreadySignedIn:" + currentUser.getEmail());
            Intent intentToMainScreen = new Intent(LoginActivity.this, MainActivity.class);
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
                Intent intentToRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intentToRegister);
                break;
        }
    }

    public void signIn(String email, String password) {
        if (!isValidForm()) {
            return;
        }
        showProgressDialog();
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Intent intentToMainScreen = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intentToMainScreen);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
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
