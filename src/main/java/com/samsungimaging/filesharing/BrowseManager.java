package com.samsungimaging.filesharing;

import com.samsungimaging.filesharing.util.Logger;
import org.cybergarage.upnp.Action;

/* JADX INFO: loaded from: classes.dex */
public class BrowseManager extends Item {
    private static final String TAG = "ITEM";
    private static final String TAG_INRY = "INRY";
    private static BrowseManager mInstance = null;
    private Container mCurrentContainer;
    public boolean mFirstFlag;
    private Container mRootContainer;

    public BrowseManager() {
        this.mRootContainer = null;
        this.mCurrentContainer = null;
        this.mFirstFlag = false;
    }

    public BrowseManager(Action browseAction) {
        super(null, browseAction);
        this.mRootContainer = null;
        this.mCurrentContainer = null;
        this.mFirstFlag = false;
        Logger.d(TAG, "BrowseManager()");
        this.mRootContainer = new Container(browseAction);
        this.mCurrentContainer = this.mRootContainer;
    }

    public static synchronized BrowseManager getInstance(Action browseAction) {
        if (mInstance == null) {
            mInstance = new BrowseManager(browseAction);
        }
        return mInstance;
    }

    public int doBrowse() {
        Logger.d(TAG, "doBrowse()");
        if (!this.mFirstFlag) {
            Logger.d(TAG, "mFirstFlag");
            int cnt = this.mRootContainer.browse(100, 100);
            Logger.d(TAG, "cnt = " + cnt);
            this.mFirstFlag = true;
            if (this.mRootContainer.getItemList().size() > 0) {
                this.mRootContainer.setmCurrentContainerIndex(1);
            }
        }
        int index = this.mRootContainer.getmCurrentContainerIndex();
        Logger.d(TAG, "Index = " + index);
        int currentCnt = 0;
        if (Container.mContainerIndex > 0) {
            int size = this.mRootContainer.getItemList().size();
            Logger.d(TAG, "size = " + size);
            this.mCurrentContainer = (Container) this.mRootContainer.getItemList().getItem(index - 1);
            while (currentCnt < 1000) {
                int remainCnt = 1000 - currentCnt;
                Logger.d("INRY", "currentCnt = " + currentCnt + ", remainCnt = " + remainCnt);
                int count = this.mCurrentContainer.browse(20, remainCnt);
                Logger.d("INRY", "Result = " + count);
                currentCnt += count;
                if (count != 20 && currentCnt < 1000) {
                    Logger.d("INRY", "less than NUM_OF_ITEMS : currentCnt = " + currentCnt);
                    if (size == this.mRootContainer.getmCurrentContainerIndex()) {
                        break;
                    }
                    index++;
                    this.mRootContainer.setmCurrentContainerIndex(index);
                    Item content = this.mRootContainer.getItemList().getItem(index - 1);
                    if (content == null) {
                        break;
                    }
                    this.mCurrentContainer = (Container) content;
                }
            }
        }
        Logger.d("INRY", "doBrowse = " + currentCnt);
        return currentCnt;
    }
}
