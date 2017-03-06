package com.example.admin.voterv10;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Declarations
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> arrayList;
    ListView lview;
    Thread thread;
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
        handleclick();
    }
    private void handleclick() {
        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //this handles the server choise and logs the user
                if(position!=0) {
                    Intent intent = new Intent(MainActivity.this, Login_Handler.class);
                    intent.putExtra("server", parent.getItemAtPosition(position).toString());
                    
                    startActivity(intent);
                    //thread.stop() is deprecated,hence temp flag is used
                    flag=1;
                    Log.d("pressed",parent.getItemAtPosition(position).toString());
                }
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
        //Log.d("Status","after call");
    }



    private void listSevers() {
        final boolean[] proceed = {false};

        final DatagramSocket[] ss = {null};
        thread= new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Log.d("Socket","creating server socket...");
                    ss[0] = new DatagramSocket(2566);
                    proceed[0] =true;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("Failiure","ServerSocket creation failed...");
                }
                while(true){
                    if(flag==1){
                        ss.clone();
                        break;
                    }
                    if(proceed[0]){
                        //Log.d("STATUS","server socket is functioning...");
                        try {
                            byte []b = new byte[5];
                            final DatagramPacket dp = new DatagramPacket(b,5);
                            ss[0].receive(dp);
                            //Log.d("Status","socket is created successfully...");
                            //WTF, just try commenting below tow lines, it gives error
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
                            //Log.d("STATUS","Listener Socket creation failed...");
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();

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
