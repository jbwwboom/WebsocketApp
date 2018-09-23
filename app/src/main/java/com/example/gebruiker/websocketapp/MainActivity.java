package com.example.gebruiker.websocketapp;

import android.content.res.Configuration;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private ConstraintLayout layout;


    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.178.24:3000");
        } catch (URISyntaxException e) {}
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.layout);
        textView = findViewById(R.id.orientationText);

        mSocket.on("orientation", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String data = (String) args[0];
                        Boolean orientation = Boolean.valueOf(data);

                        changeOrientation(orientation);
                    }
                });
            }
        });

        mSocket.connect();

        sendStartOrientation();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        String orientation;

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                orientation = "Landscape";
                attemptSend(orientation);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
                orientation = "Portrait";
                attemptSend(orientation);
        }
    }

    public void sendStartOrientation(){
        String orientation;

        // Checks the orientation of the screen
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            orientation = "Landscape";
            attemptSend(orientation);
        }else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            orientation = "Portrait";
            attemptSend(orientation);
        }
    }

    public void changeOrientation(Boolean orientation){
        if (orientation) {
            layout.setBackgroundColor(Color.GREEN);
            textView.setText("CORRECT");
        } else{
            layout.setBackgroundColor(Color.RED);
            textView.setText("FALSE");
        }
    }

    private void attemptSend(String orientation) {
        mSocket.emit("change orientation", orientation.trim());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
    }
}
