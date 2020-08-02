package com.example.sql;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class manageAccount extends AppCompatActivity {
    final private int REQUEST_INTERNET = 123;
    EditText editTextEmail;
    EditText editTextName;
    EditText editTextURL;
    RadioButton radioButton1;
    RadioButton radioButton2;
    EditText editTextPassword1;
    Chip chipEmail;
    Chip chipName;
    Chip chipGender;
    Chip chipPic;

    TextView textView21;

    FirebaseAuth mAuth;
    String username = mAuth.getInstance().getCurrentUser().getUid();
    String email = mAuth.getInstance().getCurrentUser().getEmail();
    FirebaseFirestore fdb = FirebaseFirestore.getInstance();

    String updateMessage = " succesfully updated!";

    ImageView imageView300;
    String TAG = "";
    String name = "";
    Boolean maleOrFemale = true;
    Boolean changeEmail = false;
    Boolean changeName = false;
    Boolean changeGender = false;
    Boolean changePic = false;
    Boolean newGender = false;
    ProgressBar progressBar;
    Button button;
    Button buttonChange;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    (Manifest.permission.INTERNET)
            },REQUEST_INTERNET);

        }

        else{
            imageView300 = findViewById(R.id.imageView300);
            Picasso.get().load(mAuth.getInstance().getCurrentUser().getPhotoUrl().toString()).into(imageView300, new com.squareup.picasso.Callback(){
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(manageAccount.this, "Image not found", Toast.LENGTH_LONG).show();
                }

            });

        }
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextName = findViewById(R.id.editTextName);
        editTextURL = findViewById(R.id.editTextURL);
        editTextPassword1 = findViewById(R.id.editTextPass1);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        button = findViewById(R.id.button7);
        getUser();
        editTextEmail.setText(email);
        editTextURL.setText(mAuth.getInstance().getCurrentUser().getPhotoUrl().toString());

        editTextPassword1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(editTextPassword1.getText().toString().trim().length()>3){
                    button.setVisibility(View.VISIBLE);
                }else{
                    button.setVisibility(View.INVISIBLE);

                }
            }
        });

}

public void radioCheck(View view){
        changeGender = true;
        newGender = true;
}
    public void radioCheck2(View view){
        changeGender = true;
        newGender = false;
    }

    public void getUser() {

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextName = findViewById(R.id.editTextName);
        editTextURL = findViewById(R.id.editTextURL);

        DocumentReference docRef = fdb.collection("users").document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        name = document.getString("Name");
                        maleOrFemale = document.getBoolean("Gender");
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
                editTextName = findViewById(R.id.editTextName);
                editTextName.setText(name);
                radioButton1 = findViewById(R.id.radioButton1);
                radioButton2 = findViewById(R.id.radioButton2);
                if(maleOrFemale){
                    radioButton1.toggle();
                }else{
                    radioButton2.toggle();
                }

                editTextEmail.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override
                    public void afterTextChanged(Editable s) {
                        editTextEmail = findViewById(R.id.editTextEmail);
                        textView21 = findViewById(R.id.textView21);
                        editTextPassword1 = findViewById(R.id.editTextPass1);
                        if(!editTextEmail.getText().toString().trim().equals(email)) {
                            changeEmail = true;
                        }else{
                            changeEmail = false;
                        }
                    }
                });
                editTextName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override
                    public void afterTextChanged(Editable s) {
                        changeName = true;
                    }
                });


                editTextURL.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override
                    public void afterTextChanged(Editable s) {
                        changePic = true;
                    }
                });
            }
        });
    }

public void update(View view){
    editTextEmail = findViewById(R.id.editTextEmail);
    editTextName = findViewById(R.id.editTextName);
    editTextURL = findViewById(R.id.editTextURL);
    editTextPassword1 = findViewById(R.id.editTextPass1);
    textView21 = findViewById(R.id.textView21);

    chipEmail = findViewById(R.id.chipEmail);
    chipName = findViewById(R.id.chipName);
    chipGender = findViewById(R.id.chipGender);
    chipPic = findViewById(R.id.chipPic);
    progressBar = findViewById(R.id.progressBar3);

    if(changeEmail) {
        progressBar.setVisibility(View.VISIBLE);
        if(editTextPassword1.getText().toString().length()>0) {
            final String newEmail = editTextEmail.getText().toString();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // Get auth credentials from the user for re-authentication
            AuthCredential credential = EmailAuthProvider
                    .getCredential(mAuth.getInstance().getCurrentUser().getEmail(), editTextPassword1.getText().toString()); // Current Login Credentials \\
            // Prompt the user to re-provide their sign-in credentials
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "User re-authenticated.");
                            //Now change your email address \\
                            //----------------Code for Changing Email Address----------\\
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.updateEmail(newEmail)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User email address updated.");
                                                chipEmail.setVisibility(View.VISIBLE);
                                                chipEmail.toggle();
                                                progressBar.setVisibility(View.GONE);
                                                changeEmail = false;
                                                email = mAuth.getInstance().getCurrentUser().getEmail();

                                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        chipEmail.toggle();
                                                        chipEmail.setVisibility(View.INVISIBLE);
                                                    }
                                                }, 5000);
                                            } else {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(manageAccount.this, "Email already in use.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                            //----------------------------------------------------------\\
                        }
                    });
        }else{
            Toast.makeText(manageAccount.this, "Please enter your current password to make change.", Toast.LENGTH_LONG).show();
        }
    }

    if(changeName) {
        String newName = editTextName.getText().toString();
        DocumentReference docRef = fdb.collection("users").document(username);
        docRef.update("Name", newName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                chipName.setVisibility(View.VISIBLE);
                chipName.toggle();
                changeName = false;
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        chipName.toggle();
                        chipName.setVisibility(View.INVISIBLE);
                    }
                }, 5000);

            }
        });
    }

    if(changeGender) {
        DocumentReference docRef = fdb.collection("users").document(username);
        docRef.update("Gender", newGender).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                chipGender.setVisibility(View.VISIBLE);
                chipGender.toggle();
                changeGender = false;
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        chipGender.toggle();
                        chipGender.setVisibility(View.INVISIBLE);
                    }
                }, 5000);
            }
        });
    }
    if(changePic){
        progressBar.setVisibility(View.VISIBLE);
        imageView300 = findViewById(R.id.imageView300);
        Picasso.get().load(editTextURL.getText().toString()).into(imageView300, new com.squareup.picasso.Callback(){
            @Override
            public void onSuccess() {
                FirebaseUser user = mAuth.getInstance().getCurrentUser();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse(editTextURL.getText().toString())).build();
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User profile updated.");
                                    chipPic.setVisibility(View.VISIBLE);
                                    chipPic.toggle();
                                    progressBar.setVisibility(View.GONE);
                                    changePic = false;
                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            chipPic.toggle();
                                            chipPic.setVisibility(View.INVISIBLE);
                                        }
                                    }, 5000);
                                }else{
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
            }
            @Override
            public void onError(Exception e) {
                Toast.makeText(manageAccount.this, "Image not found", Toast.LENGTH_LONG).show();
            }
        });
    }


}

public void change(View view){

    AlertDialog.Builder mBuilder = new AlertDialog.Builder(manageAccount.this);
    View mView = getLayoutInflater().inflate(R.layout.layout_dialog, null);
    final EditText currentPass = (EditText) mView.findViewById(R.id.currentPass);
    final EditText newPass1 = (EditText) mView.findViewById(R.id.newPass1);
    final EditText newPass2 = (EditText) mView.findViewById(R.id.newPass2);


    Button mLogin = (Button) mView.findViewById(R.id.btnLogin);
    mBuilder.setView(mView);
    final AlertDialog dialog = mBuilder.create();
    dialog.show();
    mLogin.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (newPass1.getText().toString().equals(newPass2.getText().toString())) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // Get auth credentials from the user for re-authentication
                AuthCredential credential = EmailAuthProvider
                        .getCredential(mAuth.getInstance().getCurrentUser().getEmail(), currentPass.getText().toString()); // Current Login Credentials \\
                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "User re-authenticated.");
                                //Now change your email address \\
                                //----------------Code for Changing Email Address----------\\
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.updatePassword(newPass1.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    dialog.dismiss();
                                                    Toast.makeText(manageAccount.this, "Password updated!", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(manageAccount.this, "Current password incorrect or new password less than 8 characters.", Toast.LENGTH_LONG).show();
                                                }
                                            }

                                            // If sign in fails, display a message to the user.
                                            // ...
                                        });

                                // ...
                            }
                        });

            }else{
                Toast.makeText(manageAccount.this, "New passwords do not match", Toast.LENGTH_LONG).show();
            }
        }
        });
    }
}



