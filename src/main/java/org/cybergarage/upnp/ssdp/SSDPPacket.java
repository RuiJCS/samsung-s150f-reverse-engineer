package org.cybergarage.upnp.ssdp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.cybergarage.http.HTTP;
import org.cybergarage.http.HTTPHeader;
import org.cybergarage.soap.SOAP;
import org.cybergarage.upnp.device.MAN;
import org.cybergarage.upnp.device.NT;
import org.cybergarage.upnp.device.NTS;
import org.cybergarage.upnp.device.ST;
import org.cybergarage.upnp.device.USN;

/* JADX INFO: loaded from: classes.dex */
public class SSDPPacket {
    private DatagramPacket dgmPacket;
    private String localAddr = "";
    public byte[] packetBytes = null;
    private long timeStamp;

    public SSDPPacket(byte[] buf, int length) {
        this.dgmPacket = null;
        this.dgmPacket = new DatagramPacket(buf, length);
    }

    public DatagramPacket getDatagramPacket() {
        return this.dgmPacket;
    }

    public void setLocalAddress(String addr) {
        this.localAddr = addr;
    }

    public String getLocalAddress() {
        return this.localAddr;
    }

    public void setTimeStamp(long value) {
        this.timeStamp = value;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public InetAddress getRemoteInetAddress() {
        return getDatagramPacket().getAddress();
    }

    public String getRemoteAddress() {
        return getDatagramPacket().getAddress().getHostAddress();
    }

    public int getRemotePort() {
        return getDatagramPacket().getPort();
    }

    public byte[] getData() {
        if (this.packetBytes != null) {
            return this.packetBytes;
        }
        DatagramPacket packet = getDatagramPacket();
        int packetLen = packet.getLength();
        String packetData = new String(packet.getData(), 0, packetLen);
        this.packetBytes = packetData.getBytes();
        return this.packetBytes;
    }

    public String getHost() {
        return HTTPHeader.getValue(getData(), HTTP.HOST);
    }

    public String getCacheControl() {
        return HTTPHeader.getValue(getData(), HTTP.CACHE_CONTROL);
    }

    public String getLocation() {
        return HTTPHeader.getValue(getData(), HTTP.LOCATION);
    }

    public String getMAN() {
        return HTTPHeader.getValue(getData(), HTTP.MAN);
    }

    public String getST() {
        return HTTPHeader.getValue(getData(), HTTP.ST);
    }

    public String getNT() {
        return HTTPHeader.getValue(getData(), HTTP.NT);
    }

    public String getNTS() {
        return HTTPHeader.getValue(getData(), HTTP.NTS);
    }

    public String getServer() {
        return HTTPHeader.getValue(getData(), HTTP.SERVER);
    }

    public String getUSN() {
        return HTTPHeader.getValue(getData(), HTTP.USN);
    }

    public int getMX() {
        return HTTPHeader.getIntegerValue(getData(), HTTP.MX);
    }

    public InetAddress getHostInetAddress() {
        String addrStr = "127.0.0.1";
        String host = getHost();
        int canmaIdx = host.lastIndexOf(SOAP.DELIM);
        if (canmaIdx >= 0) {
            addrStr = host.substring(0, canmaIdx);
            if (addrStr.charAt(0) == '[') {
                addrStr = addrStr.substring(1, addrStr.length());
            }
            if (addrStr.charAt(addrStr.length() - 1) == ']') {
                addrStr = addrStr.substring(0, addrStr.length() - 1);
            }
        }
        InetSocketAddress isockaddr = new InetSocketAddress(addrStr, 0);
        return isockaddr.getAddress();
    }

    public boolean isRootDevice() {
        if (NT.isRootDevice(getNT()) || ST.isRootDevice(getST())) {
            return true;
        }
        return USN.isRootDevice(getUSN());
    }

    public boolean isDiscover() {
        return MAN.isDiscover(getMAN());
    }

    public boolean isAlive() {
        return NTS.isAlive(getNTS());
    }

    public boolean isByeBye() {
        return NTS.isByeBye(getNTS());
    }

    public int getLeaseTime() {
        return SSDP.getLeaseTime(getCacheControl());
    }

    public String toString() {
        return new String(getData());
    }
}
