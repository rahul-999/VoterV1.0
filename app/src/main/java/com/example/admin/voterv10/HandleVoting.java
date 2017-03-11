package com.example.admin.voterv10;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ADMIN on 3/6/2017.
 */

public class HandleVoting extends AppCompatActivity {

    ListView lview;
    Button submit;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> arrayList;
    String server,username,password,selectedName;
    int selected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voting_gui);
        initialize();


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Log.d("Status","establishing connection...");
                    //Log.d("SERVER",server);
                    HttpURLConnection con = (HttpURLConnection)(new URL("http://"+server+":5657/vote")).openConnection();
                    con.setRequestMethod("GET");
                    con.connect();
                    //Log.d("Status","connection established...");
                    InputStream is = con.getInputStream();
                    //Log.d("POINT","1");
                    final StringBuffer buffer = new StringBuffer(300);
                    byte []b = new byte[1];
                    while(is.read(b) !=-1){
                        buffer.append(new String(b,"UTF-8"));
                    }
                    Log.d("Value",String.valueOf(buffer));
                    con.disconnect();
                    is.close();
                   // JSONObject jsn = new JSONObject(String.valueOf(buffer));
                    final StringBuffer temp = new StringBuffer(50);
                    int point=0;
                    while(buffer.charAt(point) != ']'){
                        if(buffer.charAt(point)=='{'){
                            temp.setLength(0);
                            temp.append('{');
                            point++;
                        }else if(buffer.charAt(point)=='}'){
                            temp.append('}');
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {

                                        JSONObject jsn = new JSONObject(String.valueOf(temp));
                                        //Log.d("Voila",String.valueOf(temp));
                                        addCandidate(jsn.getString("name"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            Thread.sleep(300);
                            //this sleep statement is must, because new thread creation takes time
                            //and this will lead to same last entry addition into list because the current thread is executed very fast
                            point++;
                        }else if(buffer.charAt(point)=='['){
                            //donothing, acts to discard '[' string
                            point++;
                        }else{
                            temp.append(buffer.charAt(point));
                            point++;
                        }
                    }

                    //Log.d("size", String.valueOf(jsn.length()));
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }

    private void initialize() {
        lview = (ListView)findViewById(R.id.candidate);
        submit = (Button)findViewById(R.id.submitt);
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
        lview.setAdapter(arrayAdapter);

        addCandidate("Candidates : ");
        Intent myIntent = getIntent();
        server = myIntent.getStringExtra("server");
        Log.d("server name",server);
        username=myIntent.getStringExtra("username");
        password=myIntent.getStringExtra("password");
        selected=99;
        selectoption();
        selectedName=null;
        submitOption();

    }

    private void submitOption() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thred = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            HttpURLConnection mycon = (HttpURLConnection)(new URL("http://"+server+":5657/voter")).openConnection();
                            mycon.setRequestMethod("POST");
                            mycon.setDoOutput(true);
                            mycon.setDoInput(true);
                            mycon.connect();
                            //below statement forces app to actually establish connection, i dont know why this is required, but without it post request won't work
                            //mycon.getResponseCode();
                            OutputStream os = mycon.getOutputStream();
                            //InputStream is= mycon.getInputStream();
                            String json;
                            json=null;
                            json = "{ \"id\" : \""+username+"\",\"pass\":\""+password+"\",\"selection\":\""+selectedName+"\"}";
                            Log.d("passdata",json);
                            os.write(json.getBytes());
                            os.close();


                            //os.write(json.getBytes());
                            String result=null;
                            InputStream is= mycon.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(
                                    mycon.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String line = null;

                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                            result = sb.toString();


                            mycon.disconnect();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    lview.setEnabled(false);
                                    submit.setEnabled(false);
                                }
                            });



                        } catch (IOException e) {
                            Log.d("ERROR","post request IO failiur");
                            e.printStackTrace();
                        }

                    }
                });
                thred.start();
            }
        });
    }

    private void selectoption() {
        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(selected==99){
                    lview.getChildAt(position).setBackgroundColor(Color.BLUE);
                    selected=position;
                    selectedName = parent.getItemAtPosition(selected).toString();
                }else{
                    lview.getChildAt(selected).setBackgroundColor(Color.WHITE);
                    lview.getChildAt(position).setBackgroundColor(Color.BLUE);
                    selected=position;
                }

            }
        });
    }

    private void addCandidate(String candidates) {
        if(!arrayList.contains(candidates)){
            arrayList.add(candidates);
            arrayAdapter.notifyDataSetChanged();
        }
    }
}
