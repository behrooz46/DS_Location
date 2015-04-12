package com.ds18842.meetmenow.locationtest.network.infrastructure;

public interface IDevice {
    public ISocket createSocket(String uniqueID);
    public String getState() ;

    public String getAddress();
    public String getName() ;
}
