package com.example.sql;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.DashPathEffect;
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
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Summary1 extends AppCompatActivity {

    ToggleButton btn_Exercise;
    ToggleButton btn_Sleep;
    ToggleButton btn_Diet;
    ToggleButton btn_Steps;


    String selectedItem;

    TextView textViewNoData;
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
    String username = Home.username;
    private List<String> namesList = new ArrayList<>();
    SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    Switch switch1;
    Switch switch2;

    TextView textViewFrom;
    TextView textViewTo;
    RadioButton radioButton1;
    RadioButton radioButton2;
    RadioButton radioButton3;
    List<Entry> entries = new ArrayList<Entry>();
    ArrayList<Entry> values = new ArrayList<>();


    List<Double> nums = new ArrayList<>();
    List<DataEntry> data = new ArrayList<>();
    static List<String> SleepDates = new ArrayList<>();
    float limit = 8f;
    String label = "Recommended 8hr sleep";
    List<Long> finalCalories = new ArrayList<>();
    List<Long> TotalCalories = new ArrayList<>();

    ArrayList<String> uniqueDates = new ArrayList<String>();
    int p = 0;
    private int sum = 0;
    ListMultimap<String, Long> foodCals = ArrayListMultimap.create();
    ListMultimap<String, Double> Ex = ArrayListMultimap.create();
    ListMultimap<String, Long> Step = ArrayListMultimap.create();


    String label2 = "Entry";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary1);


        ((RadioGroup) findViewById(R.id.toggleGroup)).setOnCheckedChangeListener(ToggleListener);

        listView = findViewById(R.id.listView300);
        editTextDate1 = findViewById(R.id.editTextDate1);
        editTextDate2 = findViewById(R.id.editTextDate2);
        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        textViewFrom = findViewById(R.id.textViewFrom);
        textViewTo = findViewById(R.id.textViewTo);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);


        btn_Diet = findViewById(R.id.btn_Diet);
        btn_Sleep = findViewById(R.id.btn_Sleep);
        btn_Exercise = findViewById(R.id.btn_Exercise);
        btn_Steps = findViewById(R.id.btn_Steps);

        editTextDate1.setVisibility(View.INVISIBLE);
        editTextDate2.setVisibility(View.INVISIBLE);
        textViewFrom.setVisibility(View.INVISIBLE);
        textViewTo.setVisibility(View.INVISIBLE);

        radioButton3.toggle();
        radioButton3.callOnClick();

        final LineChart mChart = (LineChart) findViewById(R.id.chart);

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switch1.isChecked()) {
                    editTextDate1.setVisibility(View.VISIBLE);
                    editTextDate2.setVisibility(View.VISIBLE);
                    textViewFrom.setVisibility(View.VISIBLE);
                    textViewTo.setVisibility(View.VISIBLE);
                    radioButton1.setEnabled(false);
                    radioButton2.setEnabled(false);
                    radioButton3.setEnabled(false);

                } else {
                    editTextDate1.setVisibility(View.INVISIBLE);
                    editTextDate2.setVisibility(View.INVISIBLE);
                    textViewFrom.setVisibility(View.INVISIBLE);
                    textViewTo.setVisibility(View.INVISIBLE);
                    radioButton1.setEnabled(true);
                    radioButton2.setEnabled(true);
                    radioButton3.setEnabled(true);
                }
            }
        });

        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!checkEmpty()) {
                    if (switch2.isChecked()) {

                        mChart.setVisibility(View.INVISIBLE);
                        listView.setVisibility(View.VISIBLE);


                    } else {
                        mChart.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.INVISIBLE);

                    }
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long arg3) {
                view.setSelected(true);


                selectedItem = listView.getItemAtPosition(position).toString();

                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Summary1.this);
                dialogBuilder.setTitle("Delete this entry?");
                dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (btn_Sleep.isChecked()) {
                            DeleteSleep();
                        } else if (btn_Diet.isChecked()) {
                            DeleteDiet();
                        } else if (btn_Steps.isChecked()) {
                            DeleteSteps();
                        } else if (btn_Exercise.isChecked()) {
                            DeleteExercise();
                        }

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
                Toast.makeText(Summary1.this, "Hold down an item to delete it.", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void Chart() {
        if(!btn_Sleep.isChecked()) {
            Collections.reverse(SleepDates);
        }

        LineChart mChart = (LineChart) findViewById(R.id.chart);
        ;

        mChart.setTouchEnabled(true);
        mChart.setPinchZoom(true);


        LineDataSet set1;

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);


        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "Entry");
            set1.setDrawIcons(false);
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.DKGRAY);
            set1.setCircleColor(Color.DKGRAY);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(15f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            if (Utils.getSDKInt() >= 18) {
            } else {
                set1.setFillColor(Color.DKGRAY);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            mChart.setData(data);
        }
        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "ff");
            set1.setDrawIcons(false);
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.DKGRAY);
            set1.setCircleColor(Color.DKGRAY);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(15f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            if (Utils.getSDKInt() >= 18) {
            } else {
                set1.setFillColor(Color.DKGRAY);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            mChart.setData(data);
        }
/*    xAxis.setGranularityEnabled(true);

    if(SleepDates.size()>1 && SleepDates.size()<=6) {

        xAxis.setValueFormatter(new ValueFormatter() {

            @Override
            public String getFormattedValue(float value) {

                if (((int) value) < SleepDates.size()) {
                    return SleepDates.get((int) value);
                } else {
                    return "0";
                }
            }

        });
    }*/

        LimitLine ll1 = new LimitLine(limit, label);
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);

        //xAxis.setLabelCount(SleepDates.size(),true);
        IMarker marker = new YourMarkerView(this.getApplicationContext(), R.layout.custom_marker_view);
        mChart.setMarker(marker);
        mChart.getXAxis().setDrawLabels(false);
        mChart.getDescription().setText(label2);

    }


    static final RadioGroup.OnCheckedChangeListener ToggleListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final RadioGroup radioGroup, final int i) {
            for (int j = 0; j < radioGroup.getChildCount(); j++) {
                final ToggleButton view = (ToggleButton) radioGroup.getChildAt(j);
                view.setChecked(view.getId() == i);
            }
        }
    };

    public void onToggleSleep(View view) {
        ((RadioGroup) view.getParent()).check(0);
        ((RadioGroup) view.getParent()).check(view.getId());
        btn_Sleep.setClickable(false);
        btn_Diet.setClickable(true);
        btn_Steps.setClickable(true);
        btn_Exercise.setClickable(true);
        GetDataSleep();
    }

    public void onToggleDiet(View view) {
        ((RadioGroup) view.getParent()).check(0);
        ((RadioGroup) view.getParent()).check(view.getId());
        btn_Sleep.setClickable(true);
        btn_Diet.setClickable(false);
        btn_Steps.setClickable(true);
        btn_Exercise.setClickable(true);
        GetDataDiet();
    }

    public void onToggleSteps(View view) {
        ((RadioGroup) view.getParent()).check(0);
        ((RadioGroup) view.getParent()).check(view.getId());
        btn_Sleep.setClickable(true);
        btn_Diet.setClickable(true);
        btn_Steps.setClickable(false);
        btn_Exercise.setClickable(true);
        GetDataSteps();
    }

    public void onToggleExercise(View view) {
        ((RadioGroup) view.getParent()).check(0);
        ((RadioGroup) view.getParent()).check(view.getId());
        btn_Sleep.setClickable(true);
        btn_Diet.setClickable(true);
        btn_Steps.setClickable(true);
        btn_Exercise.setClickable(false);
        GetDataExercise();
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

                if (editTextDate1.getText().toString().trim().length() > 0) {
                    if (btn_Sleep.isChecked()) {
                        GetDataSleep();

                    } else if (btn_Diet.isChecked()) {
                        GetDataDiet();
                    } else if (btn_Steps.isChecked()) {
                        GetDataSteps();
                    } else if (btn_Exercise.isChecked()) {
                        GetDataExercise();
                    }
                }

                editTextDate1.setText(formattedDay + "/" + formattedMonth + "/" + year);
                //uniqueDate = formattedDay + formattedMonth + year;
            }

        };

        new DatePickerDialog(Summary1.this, mDateSetListener, cal
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
                if (btn_Sleep.isChecked()) {
                    GetDataSleep();

                } else if (btn_Diet.isChecked()) {
                    GetDataDiet();
                } else if (btn_Steps.isChecked()) {
                    GetDataSteps();
                } else if (btn_Exercise.isChecked()) {
                    GetDataExercise();
                }

            }

        };

        new DatePickerDialog(Summary1.this, mDateSetListener, cal
                .get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();
    }


    public void GetDataSleep() {

        try {

            label2 = "Hours.Minutes";
            listView = findViewById(R.id.listView300);
            fdb.collection("users").document(username).collection("Sleep").whereGreaterThan("dateSlept", date1).whereLessThan("dateSlept", date2).orderBy("dateSlept", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                    int i = 0;
                    SleepDates.clear();
                    values.clear();
                    namesList.clear();
                    nums.clear();
                    data.clear();
                    for (DocumentSnapshot snapshot : documentSnapshots) {
                        namesList.add(sfd.format(snapshot.getDate("dateSlept")) + " - " + snapshot.getString("timeSlept"));
                        nums.add(snapshot.getDouble("finalDifference"));
                        data.add(new ValueDataEntry(snapshot.getString("simpleDate"), snapshot.getLong("finalDifference")));
                        values.add(new Entry(i, snapshot.getDouble("finalDifference").floatValue()));
                        SleepDates.add(snapshot.getString("simpleDate"));
                        i++;

                    }
                    if(!checkEmpty()) {
                        limit = 8f;
                        label = "Recommended 8hr sleep";
                        LineChart mChart = (LineChart) findViewById(R.id.chart);
                        ;
                        Chart();
                        mChart.invalidate();
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.activity_listview, R.id.label, namesList);
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);
                    }
                }


            });
        } catch (Exception e) {
            Toast.makeText(Summary1.this, "Error fetching data", Toast.LENGTH_LONG).show();
        }


    }

    public void GetDataDiet() {
        try {
            label2 = "kcal";
            listView = findViewById(R.id.listView300);
            fdb.collection("users").document(username).collection("Diet").whereGreaterThan("dateTime", date1).whereLessThan("dateTime", date2).orderBy("dateTime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                    int i = 0;
                    uniqueDates.clear();
                    namesList.clear();
                    SleepDates.clear();
                    values.clear();
                    for (DocumentSnapshot snapshot : documentSnapshots) {
                        namesList.add(snapshot.getString("timeEaten") + " - " + snapshot.getString("foodID") + " - " + snapshot.getLong("foodCalories") + " kCal");
                        SleepDates.add(snapshot.getString("simpleDate"));

                        i++;
                    }
                    if(!checkEmpty()) {

                        if (Home.Gender) {
                            limit = 2500f;
                            label = "Recommended 2500kcal";
                        } else if (!Home.Gender) {
                            limit = 2000f;
                            label = "Recommended 2000kcal";
                        }
                        for (int o = 0; o < SleepDates.size(); o++) {

                            if (!uniqueDates.contains(SleepDates.get(o))) {
                                uniqueDates.add(SleepDates.get(o));
                            }
                        }
                        SleepDates.clear();
                        for (int r = 0; r < uniqueDates.size(); r++) {
                            SleepDates.add(uniqueDates.get(r));
                        }
                        GetDailyCal2();

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.activity_listview, R.id.label, namesList);
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);
                    }
                }


            });
        } catch (Exception e) {
            Toast.makeText(Summary1.this, "Error fetching data", Toast.LENGTH_LONG).show();
        }


    }



    public void GetDailyCal2(){
        foodCals.clear();
            TotalCalories.clear();
            fdb.collection("users").document(username).collection("Diet").whereGreaterThan("dateTime", date1).whereLessThan("dateTime", date2)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                        @Override

                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                    foodCals.put(snapshot.getString("simpleDate"),snapshot.getLong("foodCalories"));
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());

                            }
                            p=0;
                            for (Collection<Long> vals : foodCals.asMap().values()) {
                                long summ = 0L;
                                for (Long val : vals) {
                                    summ += val;
                                }
                                values.add(new Entry(p,summ));
                                p++;
                            }
                            LineChart mChart = (LineChart) findViewById(R.id.chart);
                            Chart();
                            mChart.invalidate();
                        }

                    });

    }


    public void GetDataSteps(){
        try{
            label2 = "Steps";
            listView = findViewById(R.id.listView300);
            fdb.collection("users").document(username).collection("Steps").whereGreaterThan("stepDate", date1).whereLessThan("stepDate", date2).orderBy("stepDate", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                    int i = 0;

                    namesList.clear();
                    SleepDates.clear();
                    values.clear();

                    for (DocumentSnapshot snapshot : documentSnapshots){
                        namesList.add(sfd.format(snapshot.getDate("stepDate"))+" - " + snapshot.getLong("stepCount") + " steps");
                        SleepDates.add(snapshot.getString("simpleDate"));
                        i++;

                    }
                    if(!checkEmpty()) {
                        limit = 10000f;
                        label = "Recommended 10,000 steps";

                        for (int o = 0; o < SleepDates.size(); o++) {

                            if (!uniqueDates.contains(SleepDates.get(o))) {
                                uniqueDates.add(SleepDates.get(o));
                            }
                        }
                        SleepDates.clear();
                        for (int r = 0; r < uniqueDates.size(); r++) {
                            SleepDates.add(uniqueDates.get(r));
                        }
                        GetDataSteps2();

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.activity_listview, R.id.label, namesList);
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);
                    }
                }


            });
        }catch (Exception e){
            Toast.makeText(Summary1.this, "Error fetching data", Toast.LENGTH_LONG).show();
        }
    }

    public void GetDataSteps2(){
        Step.clear();
        fdb.collection("users").document(username).collection("Steps").whereGreaterThan("stepDate", date1).whereLessThan("stepDate", date2)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override

                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                Step.put(snapshot.getString("simpleDate"),snapshot.getLong("stepCount"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());

                        }
                        p=0;
                        for (Collection<Long> vals : Step.asMap().values()) {
                            long summ = 0L;
                            for (Long val : vals) {
                                summ += val;
                            }
                            values.add(new Entry(p,summ));
                            p++;
                        }
                        LineChart mChart = (LineChart) findViewById(R.id.chart);
                        Chart();
                        mChart.invalidate();
                    }

                });
    }

    public void GetDataExercise(){
        try{
            label2 = "Hours.Minutes";
            listView = findViewById(R.id.listView300);
            fdb.collection("users").document(username).collection("Exercise").whereGreaterThan("exerciseStartTime",date1).whereLessThan("exerciseStartTime",date2).orderBy("exerciseStartTime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                    int i = 0;
                    uniqueDates.clear();
                    namesList.clear();
                    values.clear();
                    SleepDates.clear();
                    for (DocumentSnapshot snapshot : documentSnapshots){
                        namesList.add(sfd.format(snapshot.getDate("exerciseStartTime"))+" - " + snapshot.getString("exerciseType") +" for " + snapshot.getString("exerciseMinutes") );
                        SleepDates.add(snapshot.getString("simpleDate"));
                        i++;


                    }
                    if(!checkEmpty()) {
                        for (int o = 0; o < SleepDates.size(); o++) {

                            if (!uniqueDates.contains(SleepDates.get(o))) {
                                uniqueDates.add(SleepDates.get(o));
                            }
                        }
                        limit = 0.2f;
                        label = "Recommended 20 minutes of exercise";

                        SleepDates.clear();

                        for (int r = 0; r < uniqueDates.size(); r++) {
                            SleepDates.add(uniqueDates.get(r));
                        }
                        GetDataExercise2();


                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.activity_listview, R.id.label, namesList);
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);
                    }
                }


            });
        }catch (Exception e){
            Toast.makeText(Summary1.this, "Error fetching data", Toast.LENGTH_LONG).show();
        }


    }

    public void GetDataExercise2(){
        Ex.clear();
        fdb.collection("users").document(username).collection("Exercise").whereGreaterThan("exerciseStartTime", date1).whereLessThan("exerciseStartTime", date2)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override

                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                Ex.put(snapshot.getString("simpleDate"), snapshot.getDouble("finalDifference"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());

                        }
                        if (!checkEmpty()) {
                            p = 0;
                            for (Collection<Double> vals : Ex.asMap().values()) {
                                Double summ = 0.0;
                                for (Double val : vals) {
                                    summ += val;
                                }
                                values.add(new Entry(p, summ.floatValue()));
                                p++;
                            }
                            LineChart mChart = (LineChart) findViewById(R.id.chart);
                            Chart();
                            mChart.invalidate();
                        }
                    }

                });
    }

    public void Last7Days(View v) {
        radioButton1.setClickable(false);
        radioButton2.setClickable(true);
        radioButton3.setClickable(true);

        cal.setTime(new Date());
        cal2.setTime(new Date());

        cal.add(Calendar.DAY_OF_YEAR, -7);

        setToMidnight();

        date1 = cal.getTime();
        date2 = cal2.getTime();

        if (btn_Sleep.isChecked()){
            GetDataSleep();

        }else if(btn_Diet.isChecked()){
            GetDataDiet();
        }else if(btn_Steps.isChecked()){
            GetDataSteps();
        } else if (btn_Exercise.isChecked()) {
            GetDataExercise();
        }


    }

    public void Last14Days(View v) {
        radioButton1.setClickable(true);
        radioButton2.setClickable(false);
        radioButton3.setClickable(true);
        cal.setTime(new Date());
        cal2.setTime(new Date());

        cal.add(Calendar.DAY_OF_YEAR, -14);

        //cal2.set(Calendar.DAY_OF_MONTH, Calendar.DAY_OF_YEAR);

        setToMidnight();

        date1 = cal.getTime();
        date2 = cal2.getTime();

        if (btn_Sleep.isChecked()){
            GetDataSleep();

        }else if(btn_Diet.isChecked()){
            GetDataDiet();
        }else if(btn_Steps.isChecked()){
            GetDataSteps();
        }else if (btn_Exercise.isChecked()) {
            GetDataExercise();
        }


    }

    public void ThisMonth(View v) {
        radioButton1.setClickable(true);
        radioButton2.setClickable(true);
        radioButton3.setClickable(false);
        cal.setTime(new Date());
        cal2.setTime(new Date());



        int minus = cal.get(Calendar.DAY_OF_MONTH);
        int max = cal2.getActualMaximum(Calendar.DAY_OF_MONTH);

        cal.add(Calendar.DAY_OF_YEAR, -minus + 1);

        cal2.set(Calendar.DAY_OF_MONTH, max);

        setToMidnight();

        date1 = cal.getTime();
        date2 = cal2.getTime();

        if (btn_Sleep.isChecked()){
            GetDataSleep();

        }else if(btn_Diet.isChecked()){
            GetDataDiet();
        }else if(btn_Steps.isChecked()){
            GetDataSteps();
        }else if (btn_Exercise.isChecked()) {
            GetDataExercise();
        }


    }


    public void setToMidnight() {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        cal2.set(Calendar.HOUR_OF_DAY, 23);
        cal2.set(Calendar.MINUTE, 59);
        cal2.set(Calendar.SECOND, 59);

    }

    public void DeleteSleep(){
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

    public void DeleteDiet(){
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

    public void DeleteSteps(){
        String[] parts = selectedItem.split(" - ");
        String stepDate = parts[0]; // 004

        String[] splitCount = parts[1].split(" ");
        long stepCount = Integer.valueOf(splitCount[0]); // 034556

        String [] parts2 = stepDate.split(" ");
        String findSimpleDate = parts2[0];
        String findSimpleTime = parts2[1];



        Query itemsRef = fdb.collection("users").document(username).collection("Steps").whereEqualTo("stepCount",stepCount).whereEqualTo("simpleDate",findSimpleDate).whereEqualTo("simpleTime",findSimpleTime);
        itemsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        //fdb.document(document.getId()).delete();
                        document.getReference().delete();
                    }
                } else {
                    Toast.makeText(Summary1.this, "Error! Please check all fields are filled.", Toast.LENGTH_LONG).show();

                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

    }

    public void DeleteExercise(){
        String[] parts = selectedItem.split(" - ");
        String dateSelected = parts[0]; // 004
        String timeSleptSelectedBf = parts[1]; // 034556
        String [] splits = timeSleptSelectedBf.split(" for ");
        String timeSleptSelected = splits[0];

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
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public boolean checkEmpty(){
        final LineChart mChart = (LineChart) findViewById(R.id.chart);
        listView = findViewById(R.id.listView300);
        textViewNoData = findViewById(R.id.textViewNoData);

        if(namesList.isEmpty()){
            mChart.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.INVISIBLE);
            textViewNoData.setVisibility(View.VISIBLE);
            return  true;
        }else{

            if (switch2.isChecked()) {
                mChart.setVisibility(View.INVISIBLE);
                listView.setVisibility(View.VISIBLE);
            }else{
                mChart.setVisibility(View.VISIBLE);
                listView.setVisibility(View.INVISIBLE);
            }
            textViewNoData.setVisibility(View.INVISIBLE);
            return false;
        }
    }

}