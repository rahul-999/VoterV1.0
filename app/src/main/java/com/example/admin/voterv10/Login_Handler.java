package com.example.admin.voterv10;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.List;

/**
 * Created by ADMIN on 3/6/2017.
 */

public class Login_Handler extends AppCompatActivity{

    EditText user,pass;
    Button login;
    String server;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        initialize();
        credentialsChecking();
    }

    private void credentialsChecking() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Connection","trying for connection");
                        try {
                            //creating a request object
                            //HttpURLConnection con = (HttpURLConnection)(new URL(server+":5657/login")).openConnection();
                            HttpURLConnection con = (HttpURLConnection)(new URL("http://"+server+":5657/login")).openConnection();
                            con.setRequestMethod("POST");
                            //con.setRequestMethod("GET");
                            Log.d("Connection","waiting...");
                            con.connect();      //connection established...
                            Log.d("Connection","connection established...");
                            //proceed to writing json data as a string which server automaticallyl converts to json object
                            String json;
                            json=null;
                            json = "{ \"email\":\""+user.getText()+"\",\"password\":\""+pass.getText()+"\" }";
                            con.getOutputStream().write(json.getBytes());

                            //now reading response data

                            InputStream is = con.getInputStream();
                            StringBuffer buffer = new StringBuffer();
                            byte []b = new byte[1];
                            while(is.read(b) !=-1){
                                buffer.append(new String(b,"UTF-8"));
                            }
                            con.disconnect();

                            if(buffer!=null)
                                Log.d("JSON", String.valueOf(buffer));
                            final JSONObject jsn = new JSONObject(String.valueOf(buffer));
                            Log.d("json",jsn.toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if(jsn.getBoolean("status")){
                                            Toast.makeText(Login_Handler.this,"Login Successfull",Toast.LENGTH_LONG).show();
                                        }else{
                                            Toast.makeText(Login_Handler.this,"Login Failed, try again ...",Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            is.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                //JSONObject jsonObj =


            }
        });
    }

    private void initialize() {
        user = (EditText)findViewById(R.id.username);
        pass = (EditText)findViewById(R.id.password);
        login = (Button)findViewById(R.id.login);
        Intent intent = getIntent();
        server = intent.getStringExtra("server");
        Log.d("SERVER",server);
    }
}
