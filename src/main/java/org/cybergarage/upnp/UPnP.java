package org.cybergarage.upnp;

import android.support.v4.internal.view.SupportMenu;
import org.cybergarage.net.HostInterface;
import org.cybergarage.soap.SOAP;
import org.cybergarage.upnp.ssdp.SSDP;
import org.cybergarage.util.Debug;
import org.cybergarage.xml.Parser;

/* JADX INFO: loaded from: classes.dex */
public class UPnP {
    public static final int DEFAULT_EXPIRED_DEVICE_EXTRA_TIME = 60;
    public static final int DEFAULT_TTL = 4;
    public static final String INMPR03 = "INMPR03";
    public static final int INMPR03_DISCOVERY_OVER_WIRELESS_COUNT = 4;
    public static final String INMPR03_VERSION = "1.0";
    public static final String NAME = "CyberLinkJava";
    public static final int SERVER_RETRY_COUNT = 100;
    public static final int USE_IPV6_ADMINISTRATIVE_SCOPE = 5;
    public static final int USE_IPV6_GLOBAL_SCOPE = 7;
    public static final int USE_IPV6_LINK_LOCAL_SCOPE = 3;
    public static final int USE_IPV6_SITE_LOCAL_SCOPE = 6;
    public static final int USE_IPV6_SUBNET_SCOPE = 4;
    public static final int USE_LOOPBACK_ADDR = 2;
    public static final int USE_ONLY_IPV4_ADDR = 9;
    public static final int USE_ONLY_IPV6_ADDR = 1;
    public static final int USE_SSDP_SEARCHRESPONSE_MULTIPLE_INTERFACES = 8;
    public static final String VERSION = "1.8";
    public static final String XML_CLASS_PROPERTTY = "cyberlink.upnp.xml.parser";
    public static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
    private static int timeToLive = 4;
    private static Parser xmlParser;

    public static final String getServerName() {
        String osName = System.getProperty("os.name");
        String osVer = System.getProperty("os.version");
        return String.valueOf(osName) + "/" + osVer + " UPnP/1.0 " + NAME + "/" + VERSION;
    }

    public static final void setEnable(int value) {
        switch (value) {
            case 1:
                HostInterface.USE_ONLY_IPV6_ADDR = true;
                break;
            case 2:
                HostInterface.USE_LOOPBACK_ADDR = true;
                break;
            case 3:
                SSDP.setIPv6Address(SSDP.IPV6_LINK_LOCAL_ADDRESS);
                break;
            case 4:
                SSDP.setIPv6Address(SSDP.IPV6_SUBNET_ADDRESS);
                break;
            case 5:
                SSDP.setIPv6Address(SSDP.IPV6_ADMINISTRATIVE_ADDRESS);
                break;
            case 6:
                SSDP.setIPv6Address(SSDP.IPV6_SITE_LOCAL_ADDRESS);
                break;
            case 7:
                SSDP.setIPv6Address(SSDP.IPV6_GLOBAL_ADDRESS);
                break;
            case 9:
                HostInterface.USE_ONLY_IPV4_ADDR = true;
                break;
        }
    }

    public static final void setDisable(int value) {
        switch (value) {
            case 1:
                HostInterface.USE_ONLY_IPV6_ADDR = false;
                break;
            case 2:
                HostInterface.USE_LOOPBACK_ADDR = false;
                break;
            case 9:
                HostInterface.USE_ONLY_IPV4_ADDR = false;
                break;
        }
    }

    public static final boolean isEnabled(int value) {
        switch (value) {
            case 1:
                return HostInterface.USE_ONLY_IPV6_ADDR;
            case 2:
                return HostInterface.USE_LOOPBACK_ADDR;
            case 9:
                return HostInterface.USE_ONLY_IPV4_ADDR;
            default:
                return false;
        }
    }

    private static final String toUUID(int seed) {
        String id = Integer.toString(65535 & seed, 16);
        int idLen = id.length();
        String uuid = "";
        for (int n = 0; n < 4 - idLen; n++) {
            uuid = String.valueOf(uuid) + "0";
        }
        return String.valueOf(uuid) + id;
    }

    public static final String createUUID() {
        long time1 = System.currentTimeMillis();
        long time2 = (long) (System.currentTimeMillis() * Math.random());
        return String.valueOf(toUUID((int) (time1 & 65535))) + "-" + toUUID(((int) ((time1 >> 32) | 40960)) & SupportMenu.USER_MASK) + "-" + toUUID((int) (time2 & 65535)) + "-" + toUUID(((int) ((time2 >> 32) | 57344)) & SupportMenu.USER_MASK);
    }

    public static final void setXMLParser(Parser parser) {
        xmlParser = parser;
        SOAP.setXMLParser(parser);
    }

    public static final Parser getXMLParser() {
        if (xmlParser == null) {
            xmlParser = loadDefaultXMLParser();
            if (xmlParser == null) {
                throw new RuntimeException("No XML parser defined. And unable to laod any. \nTry to invoke UPnP.setXMLParser before UPnP.getXMLParser");
            }
            SOAP.setXMLParser(xmlParser);
        }
        return xmlParser;
    }

    private static Parser loadDefaultXMLParser() {
        String[] parserClass = {System.getProperty(XML_CLASS_PROPERTTY), "org.cybergarage.xml.parser.XmlPullParser", "org.cybergarage.xml.parser.JaxpParser", "org.cybergarage.xml.parser.kXML2Parser", "org.cybergarage.xml.parser.XercesParser"};
        for (int i = 0; i < parserClass.length; i++) {
            if (parserClass[i] != null) {
                try {
                    return (Parser) Class.forName(parserClass[i]).newInstance();
                } catch (Throwable e) {
                    Debug.warning("Unable to load " + parserClass[i] + " as XMLParser due to " + e);
                }
            }
        }
        return null;
    }

    static {
        setTimeToLive(4);
    }

    public static final void setTimeToLive(int value) {
        timeToLive = value;
    }

    public static final int getTimeToLive() {
        return timeToLive;
    }

    public static final void initialize() {
    }
}
