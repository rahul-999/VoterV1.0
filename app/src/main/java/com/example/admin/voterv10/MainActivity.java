package com.example.admin.voterv10;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Declarations
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> arrayList;
    ListView lview;
    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initialize();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initialize() {

        lview = (ListView)findViewById(R.id.listServers);
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arrayList);
        lview.setAdapter(arrayAdapter);
        addServer("Servers : ");
        listSevers();
        Log.d("Status","after call");
    }

    private void listSevers() {
        final boolean[] proceed = {false};

        final DatagramSocket[] ss = {null};
        Thread thread= new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("Socket","creating server socket...");
                    ss[0] = new DatagramSocket(2566);
                    proceed[0] =true;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("Failiure","ServerSocket creation failed...");
                }
                while(true){
                    if(proceed[0]){
                        Log.d("STATUS","server socket is functioning...");
                        try {
                            byte []b = new byte[5];
                            final DatagramPacket dp = new DatagramPacket(b,5);
                            ss[0].receive(dp);
                            Log.d("Status","socket is created successfully...");
                            Log.d("Message received", new String(dp.getData(),"UTF-8"));
                            Log.d("IP",dp.getAddress().getHostName());
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addServer(dp.getAddress().getHostName());
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("STATUS","Listener Socket creation failed...");
                        }
                    }
                }
            }
        });
        thread.start();
    }


    private void addServer(String str) {
        if(!arrayList.contains(str)) {
            arrayAdapter.add(str);
            arrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
