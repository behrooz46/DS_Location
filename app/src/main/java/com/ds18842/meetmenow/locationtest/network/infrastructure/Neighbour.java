package com.ds18842.meetmenow.locationtest.network.infrastructure;

import com.ds18842.meetmenow.locationtest.common.* ;

public class Neighbour{
    Node node ;
    IDevice device ;
    ISocket socket ;

    public Neighbour(Node node) {
        this.node = node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public void setDevice(IDevice device) {
        this.device = device;
    }

    public void setSocket(ISocket socket) {
        this.socket = socket;
    }

    public Node getNode() {
        return node;
    }

    public IDevice getDevice() {
        return device;
    }

    public ISocket getSocket() {
        return socket;
    }


}
