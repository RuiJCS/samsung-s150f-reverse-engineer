package com.samsungimaging.connectionmanager.app.pullservice.demo.ml;

import org.cybergarage.upnp.Action;
import org.cybergarage.xml.Node;

/* JADX INFO: loaded from: classes.dex */
public class Container extends Item {
    public static int mContainerIndex = 0;
    private int mCurrentContainerIndex;

    public Container(Action browseAction) {
        super(null, browseAction);
        mContainerIndex = 0;
        this.mCurrentContainerIndex = 0;
        setIsContainer(true);
    }

    public Container(Node itemNode, Action browseAction) {
        super(itemNode, browseAction);
        mContainerIndex++;
        this.mCurrentContainerIndex = 0;
        setIsContainer(true);
    }

    public int getmContainerIndex() {
        return mContainerIndex;
    }

    public void setmContainerIndex(int mContainerIndex2) {
        mContainerIndex = mContainerIndex2;
    }

    public int getmCurrentContainerIndex() {
        return this.mCurrentContainerIndex;
    }

    public void setmCurrentContainerIndex(int mCurrentContainerIndex) {
        this.mCurrentContainerIndex = mCurrentContainerIndex;
    }
}
