package com.example.sql;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signUp extends AppCompatActivity {
    String DisplayUnits = "metric";
    private static final String DATABASE_NAME="Login.db";
    private static SQLiteDatabase db;
    private DataBaseHelper dbHelper;
    String username;
    String password;
    String confirmPassword;
    String name;
    FirebaseAuth mAuth;
    FirebaseFirestore fdb = FirebaseFirestore.getInstance();
    public String TAG= "";
    boolean gender = true;
    String photoURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        RadioButton radioButton1 = (findViewById(R.id.radioButton1));
        RadioButton radioButton2 = (findViewById(R.id.radioButton2));
        radioButton1.toggle();


        mAuth = FirebaseAuth.getInstance();

    }
    public void SignUP_OK(View view){

         username = ( (EditText)findViewById(R.id.editText_ca_uname)).getText().toString();
         password = ( (EditText)findViewById(R.id.editText_ca_password)).getText().toString();
         confirmPassword = ( (EditText)findViewById(R.id.editText_ca_cpassword)).getText().toString();
         name = ( (EditText)findViewById(R.id.editText_ca_name)).getText().toString();
         if(gender){
             photoURL = "https://firebasestorage.googleapis.com/v0/b/ehealth-dashboard.appspot.com/o/businessman.png?alt=media&token=e74a18b7-f6bd-4233-9490-7ffdd1f5ca48";
         }else{
             photoURL = "https://firebasestorage.googleapis.com/v0/b/ehealth-dashboard.appspot.com/o/woman.png?alt=media&token=2fe0b0c5-95a2-4afb-934d-60a09683f86a";

         }


        if (!password.equals(confirmPassword)) {
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
        }
            else if(!username.matches("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b")){
            Toast.makeText(getApplicationContext(), "Please enter a valid email address", Toast.LENGTH_LONG).show();
        }
                else if(!password.matches(".{3,}")){
            Toast.makeText(getApplicationContext(), "Password must be more than 6 letters and numbers", Toast.LENGTH_LONG).show();
            }

         else {

            mAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(getApplicationContext(), "Account created!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(signUp.this, MainActivity.class);

                                // Create a new user with a first and last name
                                Map<String, Object> user = new HashMap<>();
                                user.put("Gender", gender);
                                user.put("Name", name);

                                // Add a new document with a CUSTOM ID
                                String currentuser = mAuth.getInstance().getCurrentUser().getUid();
                                fdb.collection("users").document(currentuser)
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);
                                            }

                                        });

                                // Add the different collections
                                fdb.collection("users").document(currentuser).collection("Sleep").add(user);
                                fdb.collection("users").document(currentuser).collection("Exercise").add(user);
                                fdb.collection("users").document(currentuser).collection("Diet").add(user);
                                fdb.collection("users").document(currentuser).collection("Steps").add(user);

                                FirebaseUser user2 = mAuth.getInstance().getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse(photoURL)).build();
                                user2.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User profile updated.");
                                                }
                                            }
                                        });


                                startActivity(intent);
                            }
                                if(!task.isSuccessful()){
                                    FirebaseAuthException e = (FirebaseAuthException )task.getException();
                                    Toast.makeText(signUp.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    return;

                            }

                        }

                    });


        }

    };

    public void ToMale(View v){
        gender = true;
        photoURL = "https://firebasestorage.googleapis.com/v0/b/ehealth-dashboard.appspot.com/o/businessman.png?alt=media&token=e74a18b7-f6bd-4233-9490-7ffdd1f5ca48";
    }

    public void ToFemale(View v){
        gender = false;
        photoURL = "https://firebasestorage.googleapis.com/v0/b/ehealth-dashboard.appspot.com/o/woman.png?alt=media&token=2fe0b0c5-95a2-4afb-934d-60a09683f86a";

    }

}
