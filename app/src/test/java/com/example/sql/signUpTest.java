package com.example.sql;

import android.content.Context;
import android.util.Log;

import org.junit.Test;

import static org.junit.Assert.*;
public class signUpTest {
    LoginDatabaseAdapter loginDatabaseAdapter;

    @Test
public void Conversion(){
    float inputnum = 12;
    double output;
    double expectednum = 53.6;
    double delta = .2;
    output = (inputnum*9/5)+32;

    assertEquals(expectednum,output,delta);
}




    @Test
    public void signUP_OK() {

        try {
            loginDatabaseAdapter = loginDatabaseAdapter.open();

            String input = "athan";
            String expected = "password";
            String output = loginDatabaseAdapter.getSingleEntry(input);
            assertEquals(expected, output);
            Thread.dumpStack();
        }catch (Exception ex){
            Log.e("Error","Error");
        }

    }
}