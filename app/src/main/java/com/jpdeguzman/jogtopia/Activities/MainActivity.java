package com.jpdeguzman.jogtopia.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jpdeguzman.jogtopia.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.main_list_view)
    ListView mMainListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ArrayList<String> mainScreenList = new ArrayList<>();
        mainScreenList.add("CREATE JOGS");
        mainScreenList.add("JOIN JOGS");

        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_selectable_list_item,
                mainScreenList
        );
        mMainListView.setAdapter(listAdapter);
        mMainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        Intent intentToFindJog = new Intent(getApplicationContext(), FindJogActivity.class);
                        startActivity(intentToFindJog);
                        break;
                }
            }
        });
    }
}
