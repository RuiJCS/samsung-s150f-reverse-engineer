package com.samsungimaging.filesharing;

import com.samsungimaging.filesharing.util.Logger;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ArgumentList;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.UPnP;
import org.cybergarage.xml.Node;
import org.cybergarage.xml.Parser;
import org.cybergarage.xml.ParserException;

/* JADX INFO: loaded from: classes.dex */
public class Item {
    public static final String ELEM_NAME_OF_CONTAINER = "container";
    public static final String ELEM_NAME_OF_ITEM = "item";
    private static final String TAG = "ITEM";
    private static final String TAG_INRY = "INRY";
    private String album;
    private Action browseAction;
    private boolean checkFlag;
    private String date;
    private String id;
    private Boolean isContainer;
    private ItemList itemList;
    private Node itemNode;
    private int itemState;
    private int mStartingIndex;
    private String parentId;
    private String protocolInfo;
    private String res;
    private String resURL;
    private String resolution;
    private String size;
    private String thumbProtocolInfo;
    private String thumbRes;
    private String thumbResolution;
    private String thumbSize;
    private String thumbUrl;
    private String title;
    private String upnpClass;

    public Item() {
        this.id = Service.MINOR_VALUE;
        this.parentId = "-1";
        this.isContainer = false;
        this.browseAction = null;
        this.checkFlag = false;
        this.mStartingIndex = 0;
        Logger.d(TAG, "Item()contructor shouldn't be called");
        this.itemNode = new Node(Action.ELEM_NAME);
        this.itemList = new ItemList();
        setItemState(1);
        setResolution("");
    }

    public Item(Node itemNode, Action browseAction) {
        this.id = Service.MINOR_VALUE;
        this.parentId = "-1";
        this.isContainer = false;
        this.browseAction = null;
        this.checkFlag = false;
        this.mStartingIndex = 0;
        this.itemNode = itemNode;
        this.browseAction = browseAction;
        this.itemList = new ItemList();
        setItemState(1);
        setResolution("");
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return this.parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpnpClass() {
        return this.upnpClass;
    }

    public void setUpnpClass(String upnpClass) {
        this.upnpClass = upnpClass;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Node getItemNode() {
        return this.itemNode;
    }

    public void setItemNode(Node itemNode) {
        this.itemNode = itemNode;
    }

    public Boolean getIsContainer() {
        return this.isContainer;
    }

    public void setIsContainer(Boolean isContainer) {
        this.isContainer = isContainer;
    }

    public String getRes() {
        return this.res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public String getResolution() {
        return this.resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getThumbRes() {
        return this.thumbRes;
    }

    public void setThumbRes(String thumbRes) {
        this.thumbRes = thumbRes;
    }

    public String getThumbSize() {
        return this.thumbSize;
    }

    public void setThumbSize(String thumbSize) {
        this.thumbSize = thumbSize;
    }

    public String getThumbResolution() {
        return this.thumbResolution;
    }

    public void setThumbResolution(String thumbResolution) {
        this.thumbResolution = thumbResolution;
    }

    public String getThumbProtocolInfo() {
        return this.thumbProtocolInfo;
    }

    public void setThumbProtocolInfo(String thumbProtocolInfo) {
        this.thumbProtocolInfo = thumbProtocolInfo;
    }

    public String getThumbUrl() {
        return this.thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getProtocolInfo() {
        return this.protocolInfo;
    }

    public void setProtocolInfo(String protocolInfo) {
        this.protocolInfo = protocolInfo;
    }

    public String getResURL() {
        return this.resURL;
    }

    public void setResURL(String resURL) {
        this.resURL = resURL;
    }

    public ItemList getItemList() {
        return this.itemList;
    }

    public void setItemList(ItemList itemList) {
        this.itemList = itemList;
    }

    public boolean isCheckFlag() {
        return this.checkFlag;
    }

    public void setCheckFlag(boolean checkFlag) {
        this.checkFlag = checkFlag;
    }

    public int getmStartingIndex() {
        return this.mStartingIndex;
    }

    public void setmStartingIndex(int mStartingIndex) {
        this.mStartingIndex = mStartingIndex;
    }

    public boolean isContainer() {
        return this.isContainer.booleanValue();
    }

    public void changedToggle() {
    }

    public String toString() {
        return this.title;
    }

    private int addItem(Node rootNode, int remainCnt) {
        Item newItem;
        int res = 0;
        Logger.d(TAG, "addItem()");
        if (rootNode == null) {
            return 0;
        }
        int nodeCnt = rootNode.getNNodes();
        int n = 0;
        int remainCnt2 = remainCnt;
        while (n < nodeCnt) {
            int remainCnt3 = remainCnt2 - 1;
            if (remainCnt2 <= 0) {
                Logger.d("INRY", "Max Full!! break");
                break;
            }
            Node itemNode = rootNode.getNode(n);
            String nodeName = itemNode.getName();
            String title = itemNode.getNode("dc:title").getValue();
            Logger.d(TAG, nodeName);
            Logger.d(TAG, title);
            if (itemNode.getName().equals(ELEM_NAME_OF_CONTAINER)) {
                newItem = new Container(itemNode, this.browseAction);
                newItem.setIsContainer(true);
            } else {
                newItem = new Item(itemNode, this.browseAction);
            }
            newItem.setId(itemNode.getAttributeValue("id"));
            newItem.setParentId(getId());
            Node tempNode = itemNode.getNode("dc:title");
            if (tempNode != null) {
                newItem.setTitle(tempNode.getValue());
            }
            Node tempNode2 = itemNode.getNode("upnp:album");
            if (tempNode2 != null) {
                newItem.setAlbum(tempNode2.getValue());
            }
            Node tempNode3 = itemNode.getNode("dc:date");
            if (tempNode3 != null) {
                newItem.setDate(tempNode3.getValue());
            }
            Node tempNode4 = itemNode.getNode("upnp:class");
            if (tempNode4 != null) {
                newItem.setUpnpClass(tempNode4.getValue());
            }
            int size = 0;
            int nSubNodeCnt = itemNode.getNNodes();
            int thumbnailType = 0;
            for (int i = 0; i < nSubNodeCnt; i++) {
                Node subNode = itemNode.getNode(i);
                if (subNode.getName().equals("res")) {
                    String protoInfo = subNode.getAttributeValue("protocolInfo");
                    Logger.d("INRY", String.valueOf(n) + " / " + i + "-=> protoInfo=" + protoInfo);
                    if (protoInfo != null) {
                        if (protoInfo.contains("JPEG_TN")) {
                            if (subNode.getValue() != null || subNode.getValue() != "") {
                                thumbnailType = 0;
                                newItem.setThumbRes(subNode.getValue());
                            }
                            if (subNode.getAttributeValue("protocolInfo") != null || subNode.getAttributeValue("protocolInfo") != "") {
                                newItem.setThumbProtocolInfo(subNode.getAttributeValue("protocolInfo"));
                            }
                            if (subNode.getAttributeValue("size") != null || subNode.getAttributeValue("size") != "") {
                                newItem.setThumbSize(subNode.getAttributeValue("size"));
                            }
                        } else if (protoInfo.contains("JPEG_LRG")) {
                            newItem.setRes(subNode.getValue());
                            newItem.setProtocolInfo(subNode.getAttributeValue("protocolInfo"));
                            newItem.setSize(subNode.getAttributeValue("size"));
                            size = 3;
                            if (newItem.getThumbRes() == null || newItem.getThumbRes() == "") {
                                thumbnailType = 3;
                                newItem.setThumbRes(subNode.getValue());
                            }
                            if (newItem.getThumbProtocolInfo() == null || newItem.getThumbProtocolInfo() == "") {
                                newItem.setThumbProtocolInfo(subNode.getAttributeValue("protocolInfo"));
                            }
                            if (newItem.getThumbSize() == null || newItem.getThumbSize() == "") {
                                newItem.setThumbSize(subNode.getAttributeValue("size"));
                            }
                        } else if (protoInfo.contains("JPEG_MED")) {
                            if (newItem.getThumbRes() == null || newItem.getThumbRes() == "") {
                                newItem.setThumbRes(subNode.getValue());
                            }
                            if (newItem.getThumbProtocolInfo() == null || newItem.getThumbProtocolInfo() == "") {
                                newItem.setThumbProtocolInfo(subNode.getAttributeValue("protocolInfo"));
                            }
                            if (newItem.getThumbSize() == null || newItem.getThumbSize() == "") {
                                newItem.setThumbSize(subNode.getAttributeValue("size"));
                            }
                            if (size <= 2) {
                                newItem.setRes(subNode.getValue());
                                newItem.setProtocolInfo(subNode.getAttributeValue("protocolInfo"));
                                newItem.setSize(subNode.getAttributeValue("size"));
                                size = 2;
                            }
                        } else if (protoInfo.contains("JPEG_SM")) {
                            if (thumbnailType == 3 || newItem.getThumbRes() == null || newItem.getThumbRes() == "") {
                                newItem.setThumbRes(subNode.getValue());
                            }
                            if (thumbnailType == 3 || newItem.getThumbProtocolInfo() == null || newItem.getThumbProtocolInfo() == "") {
                                newItem.setThumbProtocolInfo(subNode.getAttributeValue("protocolInfo"));
                            }
                            if (thumbnailType == 3 || newItem.getThumbSize() == null || newItem.getThumbSize() == "") {
                                newItem.setThumbSize(subNode.getAttributeValue("size"));
                            }
                            if (size <= 1) {
                                newItem.setRes(subNode.getValue());
                                newItem.setProtocolInfo(subNode.getAttributeValue("protocolInfo"));
                                newItem.setSize(subNode.getAttributeValue("size"));
                                size = 1;
                            }
                        } else if (protoInfo.contains("video/mp4")) {
                            newItem.setRes(subNode.getValue());
                            newItem.setResolution(subNode.getAttributeValue("resolution"));
                            newItem.setProtocolInfo(subNode.getAttributeValue("protocolInfo"));
                            newItem.setSize(subNode.getAttributeValue("size"));
                        } else {
                            newItem.setRes(subNode.getValue());
                            newItem.setProtocolInfo(subNode.getAttributeValue("protocolInfo"));
                            newItem.setSize(subNode.getAttributeValue("size"));
                        }
                    }
                }
            }
            if (newItem.isContainer()) {
                Logger.d(TAG, "add container");
                getItemList().add(newItem);
            } else {
                Logger.d(TAG, "This is item");
                res++;
                FileSharing.mContainerList.add(newItem);
            }
            n++;
            remainCnt2 = remainCnt3;
        }
        Logger.GG("List : " + getItemList().toString());
        Logger.d(TAG, "return res = " + res);
        return res;
    }

    public int browse(int requestCnt, int remainCnt) {
        int res = 0;
        if (!isContainer()) {
            return 0;
        }
        this.browseAction.setArgumentValue("ObjectID", this.id);
        this.browseAction.setArgumentValue("BrowseFlag", "BrowseDirectChildren");
        this.browseAction.setArgumentValue("Filter", "*");
        this.browseAction.setArgumentValue("StartingIndex", getmStartingIndex());
        this.browseAction.setArgumentValue("RequestedCount", requestCnt);
        this.browseAction.setArgumentValue("SortCriteria", "");
        Logger.d("INRY", "ObjectID=" + this.id + ", StartingIndex=" + getmStartingIndex() + ", RequestedCount=" + requestCnt);
        if (this.browseAction.postControlAction()) {
            ArgumentList argumentlist = this.browseAction.getOutputArgumentList();
            Argument argument1 = argumentlist.getArgument("NumberReturned");
            argument1.getValue();
            Argument argument2 = argumentlist.getArgument("Result");
            String result = argument2.getValue();
            try {
                Parser parser = UPnP.getXMLParser();
                if (parser == null) {
                    Logger.d("INRY", "Error: No parser");
                    return 0;
                }
                Node rootNode = parser.parse(result);
                res = addItem(rootNode, remainCnt);
            } catch (ParserException e) {
                e.printStackTrace();
            }
        }
        setmStartingIndex(getmStartingIndex() + res);
        return res;
    }

    public int getItemState() {
        return this.itemState;
    }

    public void setItemState(int itemState) {
        this.itemState = itemState;
    }
}
