package com.example.sql;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Sleep extends AppCompatActivity {

    Double FinalDifference;

    final Calendar cal = Calendar.getInstance();
    SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    String isEmpty;
    Button button;
    TextView textView2;
    String selectedItem;
    EditText editTextTime1;

    EditText editTextTime2;
    TextView textViewTimeSlept;
    EditText editTextDate;
    FirebaseFirestore fdb = FirebaseFirestore.getInstance();
    Date startDate = new Date();
    Date endDate = new Date();
    long difference = 0;
    ListView listView;
    String TAG = "";

    Date todaysDate = new Date();
    TextView textView7Days;

    Date date1 = new Date();
    final Calendar cal100 = Calendar.getInstance();
    final Calendar cal200 = Calendar.getInstance();

    int day;
    int month;
    int year;
    String TodaysDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

    String uniqueDate = new SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(new Date());
    String date = TodaysDate;

    private RecyclerView mFirestoreList;
    String username = Home.username;

    private List<String> namesList = new ArrayList<>();

     SimpleDateFormat dfs = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    Date dateSlept = new Date();

    int days;
    int hours;
    int mins;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
        listView = findViewById(R.id.listView300);
        editTextDate = findViewById(R.id.editTextDate1);
        editTextDate.setText(TodaysDate);
        textViewTimeSlept = findViewById(R.id.textViewTimeSleptList);

        cal100.setTime(new Date());
        cal200.add(Calendar.DAY_OF_YEAR, -8);
        date1 = cal200.getTime();
        todaysDate = cal100.getTime();

        GetData();
        MakeInvisible();




        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,final int position, long arg3) {
                view.setSelected(true);




                selectedItem = listView.getItemAtPosition(position).toString();

                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Sleep.this);
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Sleep.this, "Hold down an item to delete it.", Toast.LENGTH_LONG).show();
            }
        });

    }
    public static String getFormattedDateTime(Date date) {
        // Get date string for today to get today's jobs
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(date);
    }


    public void timePicker(View v) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(Sleep.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                editTextTime1 = findViewById(R.id.editTextTime1);
                editTextTime1.setText(String.format("%02d:%02d", hourOfDay, minutes));

                textView2 = findViewById(R.id.textView2);
                editTextTime2 = findViewById(R.id.editTextTime2);

                textView2.setVisibility(View.VISIBLE);
                editTextTime2.setVisibility(View.VISIBLE);
                if(editTextTime1.getText().toString().trim().length()>0){
                    TimeDifference();
                }

                cal.set(Calendar.HOUR_OF_DAY, hourOfDay );
                cal.set(Calendar.MINUTE,minutes);
                dateSlept = cal.getTime();
            }


        },
                0, 0, false);
        timePickerDialog.show();




    }

    public void timePicker2(View v) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(Sleep.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                editTextTime2 = findViewById(R.id.editTextTime2);
                editTextTime2.setText(String.format("%02d:%02d", hourOfDay, minutes));

                editTextDate = findViewById(R.id.editTextDate1);
                button = findViewById(R.id.button);

                editTextDate.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);

                TimeDifference();
            }
        },
                0, 0, false);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        timePickerDialog.updateTime(hour,minutes);
        timePickerDialog.show();

    }

    public void TimeDifference() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        try {
            startDate = simpleDateFormat.parse(editTextTime1.getText().toString());
            endDate = simpleDateFormat.parse(editTextTime2.getText().toString());
            difference = endDate.getTime() - startDate.getTime();

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        if (difference < 0) {
            try {
                Date dateMax = simpleDateFormat.parse("24:00");
                Date dateMin = simpleDateFormat.parse("00:00");
                difference = (dateMax.getTime() - startDate.getTime()) + (endDate.getTime() - dateMin.getTime());

            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

        }
         days = (int) (difference / (1000 * 60 * 60 * 24));
         hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
         mins = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
        Log.i("log_tag", "Hours: " + hours + ", Mins: " + mins);

        editTextTime2 = findViewById(R.id.editTextTime2);
        if(editTextTime2.getText().toString().trim().length()>0) {


            textViewTimeSlept.setText(hours + " hours and " + mins + " minutes");
        }
    }

    public void AddData() {
        if(startDate.equals("")){
            Toast.makeText(Sleep.this, "Please check the time you went to bed.", Toast.LENGTH_LONG).show();
        }
        else if(endDate.equals("")){
            Toast.makeText(Sleep.this, "Please check the time you woke up.", Toast.LENGTH_LONG).show();
        }
        else if(textViewTimeSlept.getText().equals("")) {
            Toast.makeText(Sleep.this, "Please check your times.", Toast.LENGTH_LONG).show();
        }
        else if(date.equals("")) {
            Toast.makeText(Sleep.this, "Please check the date selected.", Toast.LENGTH_LONG).show();
        }
        else if(dateSlept.equals("")) {
            Toast.makeText(Sleep.this, "Please check the date selected.", Toast.LENGTH_LONG).show();

        }else {

            GetIntTime();

            Map<String, Object> data = new HashMap<>();
            editTextDate = findViewById(R.id.editTextDate1);
            data.put("dateSlept", dateSlept);
            data.put("wokeUp", endDate);
            data.put("timeSlept", textViewTimeSlept.getText().toString());
            data.put("simpleDate", editTextDate.getText().toString());
            data.put("finalDifference",FinalDifference);


            fdb.collection("users").document(username).collection("Sleep").document()
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            Toast.makeText(Sleep.this, "Added!", Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                            Toast.makeText(Sleep.this, "Error! Please check all fields are filled.", Toast.LENGTH_LONG).show();

                        }

                    });

            editTextTime1.setText("");
            editTextTime2.setText("");
            textViewTimeSlept.setText("");
            MakeInvisible();

        }
    }

    public void Add(View v) {
        AddData();
        GetData();
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
                dateSlept = cal.getTime();
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

                date = formattedDay + "/" + formattedMonth + "/" + year;
                uniqueDate = formattedDay + formattedMonth + year;
                editTextDate = findViewById(R.id.editTextDate1);
                editTextDate.setText(date);
            }

        };

        new DatePickerDialog(Sleep.this, mDateSetListener, cal
                .get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();
}



public void GetData(){
        try{
textView7Days = findViewById(R.id.textView7Days);

        listView = findViewById(R.id.listView300);
        fdb.collection("users").document(username).collection("Sleep").whereLessThan("dateSlept",todaysDate).whereGreaterThan("dateSlept",date1).orderBy("dateSlept", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                namesList.clear();
                for (DocumentSnapshot snapshot : documentSnapshots){
                    namesList.add(sfd.format(snapshot.getDate("dateSlept"))+" - " + snapshot.getString("timeSlept"));

                }
                if(namesList.isEmpty()){
                    textView7Days.setText("No data for last 7 days available");
                }else{
                    textView7Days.setText("Last 7 days:");

                }

                ArrayAdapter<String>adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.activity_listview,R.id.label,namesList);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
            }


        });
        }catch (Exception e){
            Toast.makeText(Sleep.this, "Error fetching data", Toast.LENGTH_LONG).show();
        }
}

public void Delete(){
    String[] parts = selectedItem.split(" - ");
    String dateSelected = parts[0]; // 004
    String timeSleptSelected = parts[1]; // 034556

    String [] parts2 = dateSelected.split(" ");
    String findSimpleDate = parts2[0];

    Query itemsRef = fdb.collection("users").document(username).collection("Sleep").whereEqualTo("timeSlept",timeSleptSelected).whereEqualTo("simpleDate",findSimpleDate);
    itemsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    //fdb.document(document.getId()).delete();
                    document.getReference().delete();
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        }
    });

}

public void MakeInvisible(){
    textView2 = findViewById(R.id.textView2);
    editTextTime2 = findViewById(R.id.editTextTime2);
    editTextDate = findViewById(R.id.editTextDate1);
    button = findViewById(R.id.button);

    textView2.setVisibility(View.INVISIBLE);
    editTextTime2.setVisibility(View.INVISIBLE);
    editTextDate.setVisibility(View.INVISIBLE);
    button.setVisibility(View.INVISIBLE);



}

    public void GetIntTime(){
        String fd1 ="";
        String fd2 = "";
        Double minutes = 0.0;
        if(mins>0 && mins<10){
            minutes = Double.valueOf(mins);
            minutes = (minutes/10);
            String removeDot = String.valueOf(minutes);
            String removed = removeDot.replace(".","");

            fd1 = String.valueOf(hours)+"."+removed;
            FinalDifference = Double.valueOf(fd1);
        }else {
            fd2 = String.valueOf(hours) + "." + String.valueOf(mins);
            FinalDifference = Double.valueOf(fd2);
        }


    }
}
