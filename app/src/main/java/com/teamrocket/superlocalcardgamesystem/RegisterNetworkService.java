package com.teamrocket.superlocalcardgamesystem;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

/**
 * Created by tj on 5/28/15.
 */
public class RegisterNetworkService {
	private final String TAG = "RegisterNetworkService";
	String serviceName;
	NsdManager.RegistrationListener mRegistrationListener;
	NsdManager mNsdManager;

	public RegisterNetworkService(Context context, String roomName) {
		serviceName = Constants.SERVICE_DISCOVERY_NAME + roomName;
		initializeRegistrationListener();
		registerService(Constants.SERVICE_PORT, context);
	}

	public void registerService(int port, Context context) {
		// Create the NsdServiceInfo object, and populate it.
		NsdServiceInfo serviceInfo = new NsdServiceInfo();
		// The name is subject to change based on conflicts
		// with other services advertised on the same network.
		serviceInfo.setServiceName(serviceName);
		serviceInfo.setServiceType("_http._tcp.");
		serviceInfo.setPort(port);
		this.mNsdManager = (NsdManager) context.getSystemService(context.NSD_SERVICE);
		mNsdManager.registerService(
				serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
		Log.i(TAG, "registering  on port " + serviceInfo.getPort());
	}

	public void initializeRegistrationListener() {
		mRegistrationListener = new NsdManager.RegistrationListener() {
			@Override
			public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
				// Save the service name.  Android may have changed it in order to
				// resolve a conflict, so update the name you initially requested
				// with the name Android actually used.
				serviceName = NsdServiceInfo.getServiceName();
				Log.i(TAG, "service registered as " + serviceName);
			}

			@Override
			public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
				// Registration failed!  Put debugging code here to determine why.
			}

			@Override
			public void onServiceUnregistered(NsdServiceInfo arg0) {
				// Service has been unregistered.  This only happens when you call
				// NsdManager.unregisterService() and pass in this listener.
                Log.i(TAG, "service unregistered successfully");
			}

			@Override
			public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
				// Unregistration failed.  Put debugging code here to determine why.
                Log.i(TAG, "failed to unregister network service");
			}
		};
	}

	public void tearDown() {
        try {
            mNsdManager.unregisterService(mRegistrationListener);
        }
        catch(IllegalArgumentException e){
            Log.e(TAG, "tried to tear down after having already been torn down");
        }
	}
}
