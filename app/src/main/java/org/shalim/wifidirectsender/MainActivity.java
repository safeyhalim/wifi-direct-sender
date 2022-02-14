package org.shalim.wifidirectsender;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import org.shalim.wifidirectsender.connectivity.IConnectivityHandler;
import org.shalim.wifidirectsender.connectivity.wifidirect.WifiConnectivityHandler;
import org.shalim.wifidirectsender.data.FileSenderTask;

import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();
        FileSenderTask fileSenderTask = new FileSenderTask("filePath");
        WifiConnectivityHandler wifiHandler = WifiConnectivityHandler.getInstance(context, "car");
        wifiHandler.start(new IConnectivityHandler.IConnectivityHandlerListener() {
            @Override
            public void onConnectionEstablished(InetAddress carServerIp) {
                fileSenderTask.execute(new FileSenderTask.Inputs(carServerIp));
            }

            @Override
            public void onFailure() {

            }
        });
    }
}