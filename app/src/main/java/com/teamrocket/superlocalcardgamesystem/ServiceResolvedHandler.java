package com.teamrocket.superlocalcardgamesystem;

import java.net.InetAddress;

interface ServiceResolvedHandler{
    void onServiceResolved(InetAddress address, int port);
}