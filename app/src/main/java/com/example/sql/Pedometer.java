package com.example.sql;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Pedometer extends AppCompatActivity implements SensorEventListener {
    private List<String> namesList = new ArrayList<>();

    ListView listView;
    TextView tv_steps;
    TextView textView;
    ImageView imageView;
    String selectedItem;
    TextView textView7Days;

    SensorManager sensorManager;

    boolean running = false;
    int steps = 0;
    final private int REQUEST_INTERNET = 123;
    FirebaseFirestore fdb = FirebaseFirestore.getInstance();
    String username = Home.username;
    String TodaysDate2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    Date TodaysDate = new Date();
    String TAG = "";
    SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    SimpleDateFormat sfd2 = new SimpleDateFormat("HH:mm");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);
        listView = findViewById(R.id.listView300);

        GetData();

        tv_steps = findViewById(R.id.tv_steps);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    (Manifest.permission.INTERNET)
            },REQUEST_INTERNET);

        }

        else{
            new Pedometer.DownloadImageTask().execute("https://i.imgur.com/kkeNVEl.png");
        }

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view,final int position, long id) {
                view.setSelected(true);
                selectedItem = listView.getItemAtPosition(position).toString();

                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Pedometer.this);
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
                Toast.makeText(Pedometer.this, "Hold down an item to delete it.", Toast.LENGTH_LONG).show();
            }
        });
    }


    private InputStream OpenHttpConnection(String urlString) throws IOException {
        InputStream in =null;
        int response = -1;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if(!(conn instanceof HttpURLConnection))
            throw new IOException("Not a HTTP connection");
        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if(response==HttpURLConnection.HTTP_OK){
                in = httpConn.getInputStream();
            }
        }
        catch (Exception ex){
            Log.d("Networking", ex.getLocalizedMessage());
            throw new IOException("Couldn't connect");
        }
        return in;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch(requestCode){
            case REQUEST_INTERNET:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    new Pedometer.DownloadImageTask().execute("");
                }
                else{
                    Toast.makeText(Pedometer.this,"Permission Denied",Toast.LENGTH_LONG).show();
                }
                break;
            default:super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls){
            return DownloadImage(urls[0]);
        }

        protected void onPostExecute(Bitmap result){
            ImageView img= findViewById(R.id.imageView2);
            img.setImageBitmap(result);
        }
    }
    private Bitmap DownloadImage(String URL){
        Bitmap bitmap = null;
        InputStream in = null;
        try{
            in = OpenHttpConnection(URL);
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        }
        catch (IOException el){
            Log.d("Networking", el.getLocalizedMessage());
        }
        return bitmap;
    }


    public void startCounting(View view) {
        super.onResume();
        running = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        tv_steps.setText("Walking");
        tv_steps.setTextColor(Color.GREEN);
        new Pedometer.DownloadImageTask().execute("https://i.imgur.com/APNpWtX.png");

        if(countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else{
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void onStop(View view){
        super.onPause();
        tv_steps.setText("Off");
        tv_steps.setTextColor(Color.RED);
        running = false;
        sensorManager.unregisterListener(this);
        new Pedometer.DownloadImageTask().execute("https://i.imgur.com/kkeNVEl.png");
        if (steps>0) {
            AddData();
        }else{
            Toast.makeText(this, "No steps recorded", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (running)
            steps = steps  +1;
            //tv_steps.setText(String.valueOf(event.values[0]));
            textView = findViewById(R.id.textViewStart);
            textView.setText(steps+ "");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void logOutOK (View view){
        Intent intent = new Intent(Pedometer.this, MainActivity.class);
        startActivity(intent);
    }


    public void AddData() {

        Map<String, Object> data = new HashMap<>();
        data.put("stepCount", steps);
        data.put("stepDate", TodaysDate);
        data.put("simpleDate",TodaysDate2);
        data.put("simpleTime",sfd2.format(TodaysDate));


        fdb.collection("users").document(username).collection("Steps").document()
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(Pedometer.this, "Added!", Toast.LENGTH_LONG).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(Pedometer.this, "Error! Please check all fields are filled.", Toast.LENGTH_LONG).show();

                    }

                });
        steps=0;


    }

    public void GetData(){
        try{
            textView7Days = findViewById(R.id.textView7Days);

            listView = findViewById(R.id.listView300);
            fdb.collection("users").document(username).collection("Steps").orderBy("stepDate", Query.Direction.DESCENDING).whereEqualTo("simpleDate",TodaysDate2).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {

                    namesList.clear();
                    for (DocumentSnapshot snapshot : documentSnapshots){

                        namesList.add(sfd.format(snapshot.getDate("stepDate"))+" - " + snapshot.getLong("stepCount") + " steps");

                    }
                    if(namesList.isEmpty()){
                        textView7Days.setText("No data for today available");
                    }else{
                        textView7Days.setText("Today:");

                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.activity_listview,R.id.label,namesList);
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
                }


            });
        }catch (Exception e){
            Toast.makeText(Pedometer.this, "Error fetching data", Toast.LENGTH_LONG).show();
        }
    }

    public void Delete(){
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
                    Toast.makeText(Pedometer.this, "Error! Please check all fields are filled.", Toast.LENGTH_LONG).show();

                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

    }
}
