package com.ds18842.meetmenow.locationtest.common;

public interface IMessageHandler {
    public void receive(Packet msg);
    public void send(Packet msg);
    public void broadcast(Packet msg);
}
