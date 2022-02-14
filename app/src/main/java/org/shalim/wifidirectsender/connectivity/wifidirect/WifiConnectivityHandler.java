package org.shalim.wifidirectsender.connectivity.wifidirect;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import org.shalim.wifidirectsender.connectivity.IConnectivityHandler;
import static android.os.Looper.getMainLooper;
import static android.content.Context.WIFI_P2P_SERVICE;
import static android.content.Context.WIFI_SERVICE;
import lombok.Getter;

@Getter
public class WifiConnectivityHandler implements IConnectivityHandler, WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener {
    private final String serverDeviceName;
    private final Context applicationContext;
    private final WifiManager wifiManager;
    private final WifiP2pManager wifiP2pManager;
    private final WifiP2pManager.Channel channel;
    private final BroadcastReceiver receiver;
    private final IntentFilter intentFilter;
    private IConnectivityHandlerListener connectivityHandlerListener;
    private static WifiConnectivityHandler instance;
    private static final int GROUP_OWNER_MIN_INTENT = 0;

    public static WifiConnectivityHandler getInstance(Context applicationContext, String serverDeviceName) {
        if (instance == null) {
            instance = new WifiConnectivityHandler(applicationContext, serverDeviceName);
        }
        return instance;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void start(IConnectivityHandlerListener listener) {
        applicationContext.registerReceiver(receiver, intentFilter);
        connectivityHandlerListener = listener;
        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(getClass().getSimpleName(), "Wifi Direct Peers Discovered");
            }

            @Override
            public void onFailure(int i) {
                // TODO: Error handling
            }
        });
    }

    @Override
    public void stop() {
        applicationContext.unregisterReceiver(receiver);
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        connectToServer(getServerDevice(wifiP2pDeviceList));
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        connectivityHandlerListener.onConnectionEstablished(wifiP2pInfo.groupOwnerAddress);
    }

    public void handleWifiP2PDisabled() {

    }

    // Private constructor
    private WifiConnectivityHandler(Context applicationContext, String serverDeviceName) {
        this.applicationContext = applicationContext;
        this.serverDeviceName = serverDeviceName;
        wifiManager = (WifiManager) applicationContext.getSystemService(WIFI_SERVICE);
        wifiP2pManager = (WifiP2pManager) applicationContext.getSystemService(WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(applicationContext, getMainLooper(), null);
        intentFilter = createIntentFilter();
        receiver = new WifiDirectBroadcastReceiver(this);
    }

    private IntentFilter createIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        return intentFilter;
    }

    private WifiP2pDevice getServerDevice(WifiP2pDeviceList wifiP2pDeviceList) {
        // TODO: Throw an exception instead of returning null if no Device is found
        return wifiP2pDeviceList.getDeviceList().stream().filter(d -> d.deviceName.equalsIgnoreCase(serverDeviceName)).findFirst().orElse(null);
    }

    @SuppressLint("MissingPermission")
    private void connectToServer(WifiP2pDevice serverDevice) {
        wifiP2pManager.connect(channel, createWifiP2pConfig(serverDevice), new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(getClass().getSimpleName(), String.format("Successfully connected to Device: %s", serverDevice.deviceAddress));
            }

            @Override
            public void onFailure(int i) {
                // TODO: Error handling
            }
        });
    }

    private WifiP2pConfig createWifiP2pConfig(WifiP2pDevice serverDevice) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = serverDevice.deviceAddress;
        config.wps.setup = WpsInfo.PBC; // TODO: Find a way to bypass WPS user manual authentication
        config.groupOwnerIntent = GROUP_OWNER_MIN_INTENT; // Ensure that the client won't be the group owner
        return config;
    }
}
