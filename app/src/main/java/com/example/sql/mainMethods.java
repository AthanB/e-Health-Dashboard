package com.example.sql;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class mainMethods extends AppCompatActivity {
    String selectedItem;
    FirebaseFirestore fdb = FirebaseFirestore.getInstance();
    String username = MainActivity.username;
    String TAG = "";
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
}
