package com.teamrocket.superlocalcardgamesystem;

import android.app.Activity;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.net.InetAddress;

/**
 * Created by tj on 5/28/15.
 */
public class DiscoverNetworkService {
    private final String TAG = "DiscoverNetworkService";
    public NsdManager.DiscoveryListener mDiscoveryListener;
    public NsdManager.ResolveListener mResolveListener;
    public NsdManager mNsdManager;
    public NsdServiceInfo mService;
    public String mServiceName;
    public String SERVICE_TYPE = "_http._tcp.";
    public ServiceResolvedHandler serviceResolvedHandler;

    public DiscoverNetworkService(Context context, ServiceResolvedHandler serviceResolvedHandler){
        this.serviceResolvedHandler = serviceResolvedHandler;
        initializeDiscoveryListener();
        this.mNsdManager = (NsdManager)context.getSystemService(context.NSD_SERVICE);
        mNsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
        //initializeResolveListener();
    }

    public void setHandlerListener(ServiceResolvedHandler listener){
        serviceResolvedHandler = listener;
    }
    public void eventFired(InetAddress address, int port){
        if(serviceResolvedHandler != null){
            serviceResolvedHandler.onServiceResolved(address, port);
        }
    }

    public void initializeDiscoveryListener() {

        // Instantiate a new DiscoveryListener
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            //  Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found!  Do something with it.
                Log.d(TAG, "Service discovery success" + service);
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().equals(mServiceName)) {
                    // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                    Log.d(TAG, "Same machine: " + mServiceName);
                } else if (service.getServiceName().contains("SuperLocalCardGameSystem")){
                    initializeResolveListener();
                    mNsdManager.resolveService(service, mResolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(TAG, "service lost" + service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Called when the resolve fails.  Use the error code to debug.
                Log.e(TAG, "Resolve failed" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

                if (serviceInfo.getServiceName().equals(mServiceName)) {
                    Log.d(TAG, "Same IP.");
                    return;
                }
                mService = serviceInfo;
                int port = mService.getPort();
                Log.i(TAG, "service on port " + port);

                InetAddress host = mService.getHost();
                Log.i(TAG, "service on IP " + host.getHostAddress());
                serviceResolvedHandler.onServiceResolved(host, port);
                tearDown();
            }
        };
    }

    public void tearDown(){
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

}
