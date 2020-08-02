package com.example.sql;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Summary2 extends AppCompatActivity {



    String selectedItem;

    Date date1 = new Date();
    Date date2 = new Date();
    final Calendar cal = Calendar.getInstance();
    final Calendar cal2 = Calendar.getInstance();

    FirebaseFirestore fdb = FirebaseFirestore.getInstance();
    int day;
    int month;
    int year;
    String TAG = "";
    EditText editTextDate1;
    EditText editTextDate2;
    Button button;
    ListView listView;
    String username = MainActivity.username;
    private List<String> namesList = new ArrayList<>();
    SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    Switch switch1;
    TextView textViewFrom;
    TextView textViewTo;
    RadioButton radioButton1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary1);
        listView = findViewById(R.id.listView300);
        editTextDate1 = findViewById(R.id.editTextDate1);
        editTextDate2 = findViewById(R.id.editTextDate2);
        switch1 = findViewById(R.id.switch1);
        textViewFrom = findViewById(R.id.textViewFrom);
        textViewTo = findViewById(R.id.textViewTo);
        radioButton1 = findViewById(R.id.radioButton1);

        editTextDate1.setVisibility(View.INVISIBLE);
        editTextDate2.setVisibility(View.INVISIBLE);
        textViewFrom.setVisibility(View.INVISIBLE);
        textViewTo.setVisibility(View.INVISIBLE);
        radioButton1.callOnClick();
        radioButton1.toggle();


        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(switch1.isChecked()){
                    editTextDate1.setVisibility(View.VISIBLE);
                    editTextDate2.setVisibility(View.VISIBLE);
                    textViewFrom.setVisibility(View.VISIBLE);
                    textViewTo.setVisibility(View.VISIBLE);
                }else{
                    editTextDate1.setVisibility(View.INVISIBLE);
                    editTextDate2.setVisibility(View.INVISIBLE);
                    textViewFrom.setVisibility(View.INVISIBLE);
                    textViewTo.setVisibility(View.INVISIBLE);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,final int position, long arg3) {
                view.setSelected(true);




                selectedItem = listView.getItemAtPosition(position).toString();

                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Summary2.this);
                dialogBuilder.setTitle("Delete this entry?");
                dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Delete();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                dialogBuilder.create().show();

                return true;
            }
        });

    }

    public void datePicker(View v) {
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

                if(editTextDate1.getText().toString().trim().length()>0){
                    GetData();
                }

                editTextDate1.setText(formattedDay + "/" + formattedMonth + "/" + year);
                //uniqueDate = formattedDay + formattedMonth + year;
            }

        };

        new DatePickerDialog(Summary2.this, mDateSetListener, cal
                .get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void datePicker2(View v) {
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
                date2 = cal.getTime();
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

                editTextDate2.setText(formattedDay + "/" + formattedMonth + "/" + year);
                //uniqueDate = formattedDay + formattedMonth + year;
                GetData();

            }

        };

        new DatePickerDialog(Summary2.this, mDateSetListener, cal
                .get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();
    }



    public void GetData() {
        try {


            listView = findViewById(R.id.listView300);
            fdb.collection("users").document(username).collection("Exercise").whereGreaterThan("exerciseTime", date1).whereLessThan("exerciseTime", date2).orderBy("exerciseTime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                    namesList.clear();
                    for (DocumentSnapshot snapshot : documentSnapshots) {
                        namesList.add(sfd.format(snapshot.getDate("exerciseTime")) + " - " + snapshot.getString("exerciseType") + " - " + snapshot.getString("exerciseMinutes"));

                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.activity_listview, R.id.label, namesList);
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
                }


            });
        } catch (Exception e) {
            Toast.makeText(Summary2.this, "Error fetching data", Toast.LENGTH_LONG).show();
        }
    }

    public void Last7Days(View v) {

        cal.setTime(new Date());
        cal2.setTime(new Date());

        cal.add(Calendar.DAY_OF_YEAR, -7);

        //cal2.set(Calendar.DAY_OF_MONTH, Calendar.DAY_OF_YEAR);

        setToMidnight();

        date1 = cal.getTime();
        date2 = cal2.getTime();

        GetData();


    }

    public void Last14Days(View v) {
        cal.setTime(new Date());
        cal2.setTime(new Date());

        cal.add(Calendar.DAY_OF_YEAR, -14);

        //cal2.set(Calendar.DAY_OF_MONTH, Calendar.DAY_OF_YEAR);

        setToMidnight();

        date1 = cal.getTime();
        date2 = cal2.getTime();

        GetData();


    }

    public void ThisMonth(View v) {
        cal.setTime(new Date());
        cal2.setTime(new Date());



        int minus = cal.get(Calendar.DAY_OF_MONTH);
        int max = cal2.getActualMaximum(Calendar.DAY_OF_MONTH);

        cal.add(Calendar.DAY_OF_YEAR, -minus + 1);

        cal2.set(Calendar.DAY_OF_MONTH, max);

        setToMidnight();

        date1 = cal.getTime();
        date2 = cal2.getTime();

        GetData();


    }


    public void setToMidnight() {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        cal2.set(Calendar.HOUR_OF_DAY, 23);
        cal2.set(Calendar.MINUTE, 59);
        cal2.set(Calendar.SECOND, 59);

    }

    public void Delete(){
        String[] parts = selectedItem.split(" - ");
        String dateSelected = parts[0]; // 004
        String timeSleptSelected = parts[1]; // 034556

        String [] parts2 = dateSelected.split(" ");
        String findSimpleDate = parts2[0];

        Query itemsRef = fdb.collection("users").document(username).collection("Exercise").whereEqualTo("exerciseType",timeSleptSelected).whereEqualTo("simpleDate",findSimpleDate);
        itemsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        //fdb.document(document.getId()).delete();
                        document.getReference().delete();
                        Toast.makeText(Summary2.this, "Record deleted!", Toast.LENGTH_LONG).show();

                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });



    }
}