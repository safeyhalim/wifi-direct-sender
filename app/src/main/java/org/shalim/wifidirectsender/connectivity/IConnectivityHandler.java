package org.shalim.wifidirectsender.connectivity;

import java.net.InetAddress;

public interface IConnectivityHandler {
    void start(IConnectivityHandlerListener listener);
    void stop();
    interface IConnectivityHandlerListener {
        void onConnectionEstablished(InetAddress carServerIp);
        void onFailure();
    }
}
