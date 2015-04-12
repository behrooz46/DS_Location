package com.ds18842.meetmenow.locationtest.network.infrastructure;

import java.io.InputStream;
import java.io.OutputStream;

public interface ISocket {
    public IDevice	getRemoteDevice();
    public boolean	isConnected();

    public void close();
    public void connect();

    public InputStream getInputStream();
    public OutputStream getOutputStream();
}
