package com.jpdeguzman.jogtopia.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.login_email)
    EditText mLoginEmail;

    @BindView(R.id.login_password)
    EditText mLoginPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUi(currentUser);
    }

    @OnClick({R.id.login_button, R.id.login_switch_to_register})
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.login_button:
                signIn(mLoginEmail.getText().toString(), mLoginPassword.getText().toString());
                break;
            case R.id.login_switch_to_register:
                Intent intentToRegister = new Intent(this, RegisterActivity.class);
                startActivity(intentToRegister);
                break;
        }
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUi(user);
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUi(FirebaseUser user) {

    }
}
