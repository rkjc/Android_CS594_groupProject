package com.teamrocket.superlocalcardgamesystem;

import android.bluetooth.BluetoothAdapter;
import android.test.AndroidTestCase;

/**
 * Created by tj on 5/11/15.
 */
public class TestNetwork extends AndroidTestCase {

    public void test() {
        assertTrue(true);
    }

    public void testSupportsBluetooth(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        assertEquals(true, mBluetoothAdapter != null);
    }

    public void testBluetoothEnabled() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        assertTrue( mBluetoothAdapter.isEnabled() );
    }
}
