package com.example.sql;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Home extends AppCompatActivity {
    Calendar cal = Calendar.getInstance();

    List<Long> TotalCalories = new ArrayList<>();
    String TodaysDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    List<Long> TotalSteps = new ArrayList<>();
    List<Double> TotalExercise = new ArrayList<>();
    List<Double> TotalSleep = new ArrayList<>();
    String YesterdaysDate;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    public static boolean Gender;

    int sum;

    FirebaseAuth mAuth;
    FirebaseFirestore fdb = FirebaseFirestore.getInstance();

    String TAG = "";
    ImageView imageView3;
    TextView textView;
    TextView textViewCalories;
    TextView textViewSteps;
    TextView textViewExercise;
    TextView textViewSleep;

    Button button8;

    TextView textView14;
    TextView textView15;
    TextView textView16;
    TextView textView17;
    final private int REQUEST_INTERNET = 123;

    public static String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        username = mAuth.getInstance().getCurrentUser().getUid();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    (Manifest.permission.INTERNET)
            }, REQUEST_INTERNET);

        } else {
            getPhoto();
        }


        cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        YesterdaysDate = dateFormat.format(cal.getTime());

        textViewCalories = findViewById(R.id.textViewCalories);
        textViewSteps = findViewById(R.id.textViewSteps);
        textViewExercise = findViewById(R.id.textViewExercise);
        textViewSleep = findViewById(R.id.textViewSleep);

        textView14 = findViewById(R.id.textView14);
        textView15 = findViewById(R.id.textView15);
        textView16 = findViewById(R.id.textView16);
        textView17 = findViewById(R.id.textView17);
        button8 = findViewById(R.id.button8);

        textView14.setVisibility(View.INVISIBLE);
        textView15.setVisibility(View.INVISIBLE);
        textView16.setVisibility(View.INVISIBLE);
        textView17.setVisibility(View.INVISIBLE);

        final ProgressBar progressBar;
        progressBar = findViewById(R.id.progressBar);

        textViewSleep.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                progressBar.setVisibility(View.GONE);

                textView14.setVisibility(View.VISIBLE);
                textView15.setVisibility(View.VISIBLE);
                textView16.setVisibility(View.VISIBLE);
                textView17.setVisibility(View.VISIBLE);
                button8.setVisibility(View.VISIBLE);

            }
        });

        GetUser();
        GetGender();

        GetDataCalories();
        GetDataSteps();
        GetDataExercise();
        GetDataSleep();

    }

    @Override
    public void onResume(){
        super.onResume();
        getPhoto();
        GetUser();
        GetGender();
        GetDataCalories();

    }

    public void openPedometer(View v) {

        Intent intent = new Intent(Home.this, Pedometer.class);
        startActivity(intent);
    }

    public void openExercise(View v) {

        Intent intent = new Intent(Home.this, Exercise.class);
        startActivity(intent);
    }

    public void openSleep(View v) {

        Intent intent = new Intent(Home.this, Sleep.class);
        startActivity(intent);
    }

    public void openDiet(View v) {

        Intent intent = new Intent(Home.this, Diet.class);
        startActivity(intent);
    }

    public void openSummary(View v) {

        Intent intent = new Intent(Home.this, Summary1.class);
        startActivity(intent);
    }

    public void GetUser() {
        DocumentReference docRef = fdb.collection("users").document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        textView = findViewById(R.id.textView7);
                        textView.setText(document.getString("Name"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void GetDataCalories() {
        try {
            fdb.collection("users").document(username).collection("Diet").whereEqualTo("dateEaten", TodaysDate).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (DocumentSnapshot snapshot : documentSnapshots) {

                        TotalCalories.add(snapshot.getLong("foodCalories"));

                    }
                    sum = 0;
                    for (int i = 0; i < TotalCalories.size(); i++) {
                        sum += TotalCalories.get(i);
                    }
                    textViewCalories.setText(String.valueOf(sum) + " kcal");
                    TotalCalories.clear();
                    if (Gender) {
                        textViewCalories.setText(textViewCalories.getText() + " /2500 kcal");
                        if (sum > 2500) {
                            textViewCalories.setTextColor(Color.RED);
                        }
                        if (sum <= 2500) {
                            textViewCalories.setTextColor(Color.BLACK);
                        }
                    }
                    if (!Gender) {
                        textViewCalories.setText(textViewCalories.getText() + " /2000 kcal");

                        if (sum > 2000) {
                            textViewCalories.setTextColor(Color.RED);
                        }
                        if (sum <= 2000) {
                            textViewCalories.setTextColor(Color.BLACK);
                        }
                    }
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GetDataSteps() {
        try {
            fdb.collection("users").document(username).collection("Steps").whereEqualTo("simpleDate", TodaysDate).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (DocumentSnapshot snapshot : documentSnapshots) {
                        TotalSteps.add(snapshot.getLong("stepCount"));
                    }
                    sum = 0;
                    for (int i = 0; i < TotalSteps.size(); i++) {
                        sum += TotalSteps.get(i);
                    }
                    textViewSteps.setText(String.valueOf(sum) + " steps");
                    TotalSteps.clear();

                }


            });
        } catch (Exception e) {
            Toast.makeText(Home.this, "Error fetching data", Toast.LENGTH_LONG).show();
        }
    }

    public void GetDataExercise() {
        try {
            fdb.collection("users").document(username).collection("Exercise").whereEqualTo("simpleDate", TodaysDate).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (DocumentSnapshot snapshot : documentSnapshots) {
                        TotalExercise.add(snapshot.getDouble("finalDifference"));
                    }
                    if(TotalExercise.size()>0) {
                        Double sumE = 0.0;
                        for (int i = 0; i < TotalExercise.size(); i++) {
                            sumE += TotalExercise.get(i);
                        }


                        sumE = Math.round(sumE * 100.0) / 100.0;
                        String sumEx = Double.toString(sumE);
                        String parts[] = String.valueOf(sumE).split("\\.");
                        String hours = parts[0];
                        String mins = parts[1];
                        if(mins.equals("0")) {
                            textViewExercise.setText(hours + " hours " + "and " + mins + " minutes");
                        }else {
                            int mins2 = 0;
                            char checkMins = parts[1].charAt(0);
                            if (String.valueOf(checkMins).equals("0")) {
                                mins = mins.replace("0", "");
                                textViewExercise.setText(hours + " hours " + "and " + mins + " minutes");
                            } else {
                                if (Integer.valueOf(mins) < 10) {
                                    mins2 = Integer.valueOf(mins) * 10;
                                    textViewExercise.setText(hours + " hours " + "and " + mins2 + " minutes");

                                } else {
                                    textViewExercise.setText(hours + " hours " + "and " + mins + " minutes");
                                }

                            }
                        }
                    }else {
                        textViewExercise.setText("No exercise today!");
                    }
                    TotalExercise.clear();

                }


            });
        } catch (Exception e) {
            Toast.makeText(Home.this, "Error fetching data", Toast.LENGTH_LONG).show();
        }
    }

    public void GetDataSleep() {
        try {
            fdb.collection("users").document(username).collection("Sleep").whereEqualTo("simpleDate", YesterdaysDate).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (DocumentSnapshot snapshot : documentSnapshots) {
                        TotalSleep.add(snapshot.getDouble("finalDifference"));
                    }
                    if(TotalSleep.size()>0) {
                        Double sumE = 0.0;
                        for (int i = 0; i < TotalSleep.size(); i++) {
                            sumE += TotalSleep.get(i);
                        }


                        sumE = Math.round(sumE * 100.0) / 100.0;
                        String sumEx = Double.toString(sumE);
                        String parts[] = String.valueOf(sumE).split("\\.");
                        String hours = parts[0];
                        String mins = parts[1];

                        if(mins.equals("0")) {
                            textViewSleep.setText(hours + " hours " + "and " + mins + " minutes");
                        }else {

                            int mins2 = 0;
                            char checkMins = parts[1].charAt(0);
                            if (String.valueOf(checkMins).equals("0")) {
                                mins = mins.replace("0", "");
                                textViewSleep.setText(hours + " hours " + "and " + mins + " minutes");
                            } else {
                                if (Integer.valueOf(mins) < 10) {
                                    mins2 = Integer.valueOf(mins) * 10;
                                    textViewSleep.setText(hours + " hours " + "and " + mins2 + " minutes");

                                } else {
                                    textViewSleep.setText(hours + " hours " + "and " + mins + " minutes");
                                }

                            }
                        }
                    }else {
                        textViewSleep.setText("No sleep last night!");
                    }
                    TotalSleep.clear();

                }


            });
        } catch (Exception e) {
            Toast.makeText(Home.this, "Error fetching data", Toast.LENGTH_LONG).show();
        }
    }

    public void GetGender() {

        try {
            fdb.collection("users").document(username).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    Gender = documentSnapshot.getBoolean("Gender");

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openManage(View view) {
        Intent intent = new Intent(Home.this, manageAccount.class);
        startActivity(intent);

    }
    public void getPhoto(){
        String photoURL  = mAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
        if(!photoURL.equals("")) {
            imageView3 = findViewById(R.id.imageView3);
            Picasso.get().load(photoURL).into(imageView3);
        }
    }
}
