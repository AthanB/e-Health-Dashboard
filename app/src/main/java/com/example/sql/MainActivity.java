package com.example.sql;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    Boolean rememberMe = false;
    ProgressBar progressBar;
    CheckBox checkBox;
    EditText editTextUsername;
    EditText editTextPassword;
    String password;

    public static String storedUnits = "";
    FirebaseAuth mAuth;
    public static String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar2);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        checkBox= findViewById(R.id.checkBox);
        editTextUsername = findViewById(R.id.editText_username);
        editTextPassword = findViewById(R.id.editText_password);
        rememberMe = loginPreferences.getBoolean("rememberMe", false);
        if (rememberMe == true) {
            editTextUsername.setText(loginPreferences.getString("username", ""));
            editTextPassword.setText(loginPreferences.getString("password", ""));
            checkBox.setChecked(true);
        }

    }

    public void signIn(View V) {
        checkBox = findViewById(R.id.checkBox);
        progressBar.setVisibility(View.VISIBLE);
        username = ((EditText) findViewById(R.id.editText_username)).getText().toString();
        password = ((EditText) findViewById(R.id.editText_password)).getText().toString();

        if(username.equals("admin")){
            Intent intent = new Intent(MainActivity.this, admin.class);
            startActivity(intent);
        }
        if (username.equals("")) {
            Toast.makeText(MainActivity.this, "Please enter a username!", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);

        }
        if (password.equals("")) {
            Toast.makeText(MainActivity.this, "Please enter a password!", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);

        } else if(!username.equals("")&!password.equals("")) {


            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if(checkBox.isChecked()){
                                    rememberMe = true;
                                }else{
                                    rememberMe = false;
                                }
                                if(rememberMe){
                                    loginPrefsEditor.putBoolean("rememberMe", true);
                                    loginPrefsEditor.putString("username", username);
                                    loginPrefsEditor.putString("password", password);
                                    loginPrefsEditor.commit();
                                }else{
                                    {
                                        loginPrefsEditor.clear();
                                        loginPrefsEditor.commit();
                                    }
                                }
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(MainActivity.this, "Logged in successfully!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MainActivity.this, Home.class);
                                startActivity(intent);
                                ((EditText) findViewById(R.id.editText_password)).getText().clear();
                                progressBar.setVisibility(View.INVISIBLE);

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(MainActivity.this, "Username or password incorrect", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.INVISIBLE);

                            }

                        }
                    });
        }
    }

    public void GoToSignUP(View view){
        Intent intent = new Intent(MainActivity.this, signUp.class);
        startActivity(intent);
    }




    public String DisplayUnits(){
        return this.storedUnits;
    }
}
