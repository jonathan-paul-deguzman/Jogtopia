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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jpdeguzman.jogtopia.Models.User;
import com.jpdeguzman.jogtopia.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *  RegisterActivity serves the purpose of allowing users to create a Jogtopia account by providing
 *  their first name, last name, email, and password. If the account was successfully authenticated
 *  on Firebase, the user will be sent to {@link LoginActivity} and the user's information will be
 *  saved to the database. Otherwise, a failed authorization will be shown.
 */

public class RegisterActivity extends BaseActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    @BindView(R.id.register_first_name) EditText mRegisterFirstName;
    @BindView(R.id.register_last_name) EditText mRegisterLastName;
    @BindView(R.id.register_email) EditText mRegisterEmail;
    @BindView(R.id.register_password) EditText mRegisterPassword;

    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @OnClick({R.id.register_button, R.id.login_link})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_button:
                createAccount(mRegisterEmail.getText().toString(), mRegisterPassword.getText().toString());
                break;
            case R.id.login_link:
                Intent intentToLogin= new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intentToLogin);
                break;
        }
    }

    public void createAccount(String email, String password) {
        if (!isValidForm()) {
            return;
        }
        showProgressDialog();
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            onAuthSuccess(mFirebaseAuth.getCurrentUser());
                            Toast.makeText(RegisterActivity.this,
                                    "Account created. Try logging in.", Toast.LENGTH_SHORT).show();
                            Intent intentToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intentToLogin);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(),
                                    "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = mRegisterFirstName.getText().toString() + " " + mRegisterLastName.getText().toString();
        writeNewUser(user.getUid(), username, user.getEmail());
    }

    private void writeNewUser(String userId, String name, String email) {
        User user = new User();
        user.setFirstName(mRegisterFirstName.getText().toString());
        user.setLastName(mRegisterLastName.getText().toString());
        user.setEmail(email);
        user.setUsername(name);
        mDatabase.child("users").child(userId).setValue(user);
    }

    private boolean isValidForm() {
        boolean isFirstNameValid = checkFirstName();
        boolean isLastNameValid = checkLastName();
        boolean isEmailValid = checkEmailAddress();
        boolean isPasswordValid = checkPassword();
        return isFirstNameValid && isLastNameValid && isEmailValid && isPasswordValid;
    }

    private boolean checkFirstName() {
        return checkForEmptyField(mRegisterFirstName);
    }

    private boolean checkLastName() {
        return checkForEmptyField(mRegisterLastName);
    }

    private boolean checkEmailAddress() {
        return checkForEmptyField(mRegisterEmail);
    }

    private boolean checkPassword() {
        return checkForEmptyField(mRegisterPassword);
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
