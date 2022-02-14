package org.shalim.wifidirectsender.connectivity.wifidirect;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {
    private WifiConnectivityHandler handler;
    private WifiP2pManager wifiP2pManager;
    private Channel channel;

    public WifiDirectBroadcastReceiver(WifiConnectivityHandler handler) {
        this.handler = handler;
        wifiP2pManager = handler.getWifiP2pManager();
        channel = handler.getChannel();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch(action) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                handleStateChange(intent);
                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                handlePeerChange();
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                handleConnectionChange(intent);
                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                handleThisDeviceChange();
                break;
            default:
                break;
        }
    }

    private void handleStateChange(Intent intent) {
        int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
        if (state == WifiP2pManager.WIFI_P2P_STATE_DISABLED) {
            handler.handleWifiP2PDisabled();
        }
    }

    @SuppressLint("MissingPermission")
    private void handlePeerChange() {
        wifiP2pManager.requestPeers(channel, handler);
    }

    private void handleConnectionChange(Intent intent) {
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        if (networkInfo.isConnected()) {
            wifiP2pManager.requestConnectionInfo(channel, handler);
        }
    }

    private void handleThisDeviceChange() {

    }
}
