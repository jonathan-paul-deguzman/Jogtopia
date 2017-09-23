package com.jpdeguzman.jogtopia.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jpdeguzman.jogtopia.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.main_list_view)
    ListView mMainListView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        ArrayList<String> mainScreenList = new ArrayList<>();
        mainScreenList.add("CREATE JOGS");
        mainScreenList.add("JOIN JOGS");
        mainScreenList.add("RECENT JOGS");
        mainScreenList.add("JOGGER ACCOUNT");

        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_selectable_list_item,
                mainScreenList
        );
        mMainListView.setAdapter(listAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intentToLogin = new Intent(this, LoginActivity.class);
            startActivity(intentToLogin);
        }
    }
}
