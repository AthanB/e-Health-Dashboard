package com.example.sql;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tab1 extends Fragment {
    final Calendar cal = Calendar.getInstance();
    int day;
    int month;
    int year;
    String TAG = "";
    Date date1 = new Date();
    Date date2 = new Date();
    EditText editTextDate1;


    public Tab1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tab1, container, false);
        //editTextDate1 = root.findViewById(R.id.editTextDate1);
        // Inflate the layout for this fragment
        return root;
    }
    public void datePicker22(View v) {
        DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, monthOfYear);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
                cal.getTimeInMillis();
                date1 = cal.getTime();
                month = month + 1;
                Log.d(TAG, "onDateSet: dd/MM/yyyy: " + day + "/" + month + "/" + year);

                String formattedMonth = "" + month;
                String formattedDay = "" + day;

                if (month < 10) {
                    formattedMonth = "0" + month;
                }

                if (day < 10) {
                    formattedDay = "0" + day;
                }

                //date = formattedDay + "/" + formattedMonth + "/" + year;
                //uniqueDate = formattedDay + formattedMonth + year;
                //editTextDate1 = find(R.id.editTextDate1);
                //editTextDate1.setText(date1.toString());
            }

        };

        new DatePickerDialog(this.getActivity(), mDateSetListener, cal
                .get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();
    }
}
