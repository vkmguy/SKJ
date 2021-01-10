package com.syc.portknock;

import java.net.InetAddress;
import java.util.Objects;

public class ClientAddress {
    private final InetAddress address;
    private final int port;

    public ClientAddress(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientAddress that = (ClientAddress) o;
        return port == that.port &&
                Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, port);
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
