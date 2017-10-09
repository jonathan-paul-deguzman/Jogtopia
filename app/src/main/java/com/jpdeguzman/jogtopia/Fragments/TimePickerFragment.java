package com.jpdeguzman.jogtopia.Fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jpdeguzman.jogtopia.Activities.CreateJogActivity;

import java.util.Calendar;

/**
 * Created by jpdeguzman on 10/8/17.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private static final String TAG = TimePickerFragment.class.getSimpleName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog:defaultTimeIs:" + Calendar.HOUR_OF_DAY + ":" + Calendar.MINUTE);
        // Creating a new instance of TimePickerDialog and returning current time as default values
        return new TimePickerDialog(getActivity(), this, Calendar.HOUR_OF_DAY, Calendar.MINUTE,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        LatLng markerPosition= ((CreateJogActivity) getActivity()).getMarkerPosition();
        String timeSelected = String.valueOf(hour) + ":" + String.valueOf(minute);
        if (markerPosition != null && !timeSelected.isEmpty()) {
            Log.d(TAG, "onTimeSet:" + markerPosition.toString() + ":timeSelected: " + timeSelected);
            DatabaseReference  mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("activeJogs").child("jogTests").child("time").setValue(timeSelected);
            mDatabase.child("activeJogs").child("jogTests").child("position").setValue(markerPosition);
        }
    }
}
