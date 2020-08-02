package com.example.sql;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.entity.StringEntity;


public class Diet extends AppCompatActivity {
    List<Long> TotalCalories = new ArrayList<>();
    String selectedItem;

    String username = Home.username;
    ListView listView;
    private List<String> namesList = new ArrayList<>();

    FirebaseFirestore fdb = FirebaseFirestore.getInstance();
    Long editTextCalories;
    Date dateEatenTime = new Date();
    String TAG = "";
    String TodaysDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    String date = TodaysDate;
    final Calendar cal = Calendar.getInstance();
    int year;
    int month;
    int day;
    Button button;
    TextView textView;
    TextView textViewCal;
    EditText editText;
    EditText editTextCal;
    EditText editTextDate;
    EditText editTextTime;
    TextView textViewLimit;
    TextView textViewWhen;

    List<String> responseList;
    JSONObject json;
    String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    Long TotalCals=0L;
    double sum;
    Boolean Gender = Home.Gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        listView = findViewById(R.id.listView300);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);
        textViewCal = findViewById(R.id.textViewCal);
        editTextDate = findViewById(R.id.editTextDate1);
        editTextDate.setText(TodaysDate);
        GetData();
        editTextTime = findViewById(R.id.editTextTime);
        editTextTime.setText(currentTime);

        MakeInvisible();



        editText=(findViewById(R.id.editTextDiet));

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    editText = findViewById(R.id.editTextDiet);
                    GetDietData(editText.getText().toString());

                    editTextCal.setVisibility(View.VISIBLE);
                    textViewCal.setVisibility(View.VISIBLE);

                }
                return false;
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view,final int position, long id) {
                view.setSelected(true);
                selectedItem = listView.getItemAtPosition(position).toString();

                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Diet.this);
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
                Toast.makeText(Diet.this, "Hold down an item to delete it.", Toast.LENGTH_LONG).show();
            }
        });


        editText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(editText.getText().toString().trim().length()>0){

                    editTextCal.setVisibility(View.VISIBLE);
                    textViewCal.setVisibility(View.VISIBLE);

                }else{
                    editTextCal.setVisibility(View.INVISIBLE);
                    textViewCal.setVisibility(View.INVISIBLE);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

        });

        editTextCal = findViewById(R.id.editTextCal);
        editTextCal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (editTextCal.getText().toString().trim().length()>0){
                    textViewWhen.setVisibility(View.VISIBLE);
                    editTextTime.setVisibility(View.VISIBLE);
                    editTextDate.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    public void MakeInvisible(){
        editTextCal = findViewById(R.id.editTextCal);
        textViewCal = findViewById(R.id.textViewCal);
        textViewWhen = findViewById(R.id.textViewWhen);
        editTextDate = findViewById(R.id.editTextDate1);
        editTextTime = findViewById(R.id.editTextTime);

        editTextCal.setVisibility(View.INVISIBLE);
        textViewCal.setVisibility(View.INVISIBLE);
        textViewWhen.setVisibility(View.INVISIBLE);
        editTextDate.setVisibility(View.INVISIBLE);
        editTextTime.setVisibility(View.INVISIBLE);
    }

    public void LetsGo(View v){
        editText = findViewById(R.id.editTextDiet);
GetDietData(editText.getText().toString());
    }



public void GetDietData(String foodName) {
        StringEntity stringEntity = null;

        String foodQuery = editText.getText().toString();

            //String weather[] = new String[6];
            AsyncHttpClient client = new AsyncHttpClient();

            String calories = "UNDEFINED";

            client.addHeader("x-app-key", "48a843fa795aff892e6c40d23fba76a5");
            client.addHeader("x-app-id", "1cd48eee");
            JSONObject jsonObject = new JSONObject();

            try {
                editText = findViewById(R.id.editTextDiet);
                jsonObject.put("query", foodQuery);

            }catch (JSONException exc){
            }

            try {
                stringEntity = new StringEntity(jsonObject.toString());
            } catch (UnsupportedEncodingException except) {
                    Log.d("Error getting data.", except.toString());
            }

            client.post(Diet.this, "https://trackapi.nutritionix.com/v2/natural/nutrients", stringEntity, "application/json",
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int stat, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                            Log.d("Downloaded", "response: " + response.toString());

                            DisplayCal(response);

                        }

                        @Override
                        public void onFailure(int stat, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("Error", "cannot download" + errorResponse.toString());
                            Toast.makeText(Diet.this, "No foods found. Please check it is correct!",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

}


protected void DisplayCal(final JSONObject jsonResult) {
        StringBuilder Name = new StringBuilder();
        StringBuilder Calories = new StringBuilder();
        Integer totalCal = 0;

        try {
            JSONArray foods = jsonResult.getJSONArray("foods");
            int i = 0;
            while (!foods.isNull(i)) {

                JSONObject eachFood = foods.getJSONObject(i);
                Name.append(eachFood.getString("food_name") + "\n");
                Calories.append(eachFood.getString("nf_calories")+ " Cal"+ '\n');
                totalCal += eachFood.getInt("nf_calories");
                i++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        editTextCal = findViewById(R.id.editTextCal);
        editTextCal.setText(totalCal.toString());

}

    public void datePicker(View v){
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
                dateEatenTime = cal.getTime();


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
                editTextDate = findViewById(R.id.editTextDate1);
                editTextDate.setText(date);
            }

        };

        new DatePickerDialog(Diet.this, mDateSetListener, cal
                .get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void timePicker(View v) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(Diet.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                editTextTime = findViewById(R.id.editTextTime);
                editTextTime.setText(String.format("%02d:%02d", hourOfDay, minutes));



                cal.set(Calendar.HOUR_OF_DAY, hourOfDay );
                cal.set(Calendar.MINUTE,minutes);
                dateEatenTime = cal.getTime();

            }
        }, 0, 0, false);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        timePickerDialog.updateTime(hour,minutes);
        timePickerDialog.show();

    }

    public void AddData(View v) {
        editText = findViewById(R.id.editTextDiet);
        editTextCal = findViewById(R.id.editTextCal);
        editTextTime = findViewById(R.id.editTextTime);
        editTextDate = findViewById(R.id.editTextDate1);



        String editTextDiet = editText.getText().toString();
        String editTextTime_ = editTextTime.getText().toString();
        String editTextDate_ = editTextDate.getText().toString();

        if (editTextCal.getText().toString().trim().length()>0) {
            editTextCalories = Long.valueOf(editTextCal.getText().toString());
        }

        if(editTextDiet.equals("")){
            Toast.makeText(Diet.this, "Please enter some food/drink.", Toast.LENGTH_LONG).show();
        }

        else if(editTextCalories==null){
            Toast.makeText(Diet.this, "Please input your calories.", Toast.LENGTH_LONG).show();
        }

        else if(editTextTime_.equals("")){
            Toast.makeText(Diet.this, "Please enter a time.", Toast.LENGTH_LONG).show();
        }

        else if(editTextDate_.equals("")) {
            Toast.makeText(Diet.this, "Please enter a date.", Toast.LENGTH_LONG).show();
        }else if(editTextCalories>10000){
            Toast.makeText(Diet.this, "Please keep calories below 10,000 for one item.", Toast.LENGTH_LONG).show();

        }else {

            Map<String, Object> data = new HashMap<>();
            data.put("foodID", editTextDiet);
            data.put("foodCalories", editTextCalories);
            data.put("dateEaten", editTextDate_);
            data.put("timeEaten", editTextTime_);
            data.put("dateTime", dateEatenTime);
            data.put("simpleDate",date);


            fdb.collection("users").document(username).collection("Diet").document()
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            Toast.makeText(Diet.this, "Added!", Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                            Toast.makeText(Diet.this, "Error! Please check all fields are filled.", Toast.LENGTH_LONG).show();

                        }

                    });

            editText.setText("");
            editTextCal.setText("");
        }

    }

    public void GetData(){
        try{

            listView = findViewById(R.id.listView300);
            fdb.collection("users").document(username).collection("Diet").whereEqualTo("dateEaten",TodaysDate).orderBy("dateTime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                    namesList.clear();
                    for (DocumentSnapshot snapshot : documentSnapshots){
                        namesList.add(snapshot.getString("timeEaten")+" - " + snapshot.getString("foodID") + " - " + snapshot.getLong("foodCalories" )+" kCal");


                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.activity_listview,R.id.label,namesList);
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
                }


            });
        }catch (Exception e){
            Toast.makeText(Diet.this, "Error fetching data", Toast.LENGTH_LONG).show();
        }

        try{
        fdb.collection("users").document(username).collection("Diet").whereEqualTo("dateEaten",TodaysDate).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot snapshot : documentSnapshots){

                    TotalCalories.add(snapshot.getLong("foodCalories"));

                }
                sum=0;
                for(int i = 0; i < TotalCalories.size(); i++) {
                    sum += TotalCalories.get(i);
                }
                textViewLimit = findViewById(R.id.textViewLimit);

                textViewLimit.setText("Today's total: "+String.valueOf(sum)+" kcal");
                TotalCalories.clear();

                if(Gender){
                    textViewLimit.setText(textViewLimit.getText()+" /2500 kcal");
                    if(sum>2500){
                        textViewLimit.setTextColor(Color.RED);
                    }if(sum<=2500){
                        textViewLimit.setTextColor(Color.BLACK);
                    }
                }
                if(!Gender){
                    textViewLimit.setText(textViewLimit.getText()+" /2000 kcal");

                    if(sum>2000){
                        textViewLimit.setTextColor(Color.RED);
                    }if(sum<=2000){
                        textViewLimit.setTextColor(Color.BLACK);
                    }
                }


            }

        });
    }catch (Exception e){
        Toast.makeText(Diet.this, "Error fetching data", Toast.LENGTH_LONG).show();
    }




    }

    public void Delete(){
        String[] parts = selectedItem.split(" - ");
        String timeSelected = parts[0]; // 004
        String idSelected = parts[1]; // 034556
        String calSelected = parts[2];

        String[] parts2 = calSelected.split(" ");
        Long calSelectedFinal = Long.valueOf(parts2[0]);

        Query itemsRef = fdb.collection("users").document(username).collection("Diet").whereEqualTo("timeEaten",timeSelected).whereEqualTo("foodID",idSelected).whereEqualTo("foodCalories",calSelectedFinal);
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
}

