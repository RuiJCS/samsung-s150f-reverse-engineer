package com.samsungimaging.connectionmanager.app.pullservice.demo.ml;

import com.samsungimaging.connectionmanager.util.Trace;
import java.util.Collections;
import java.util.Comparator;
import org.cybergarage.upnp.Action;

/* JADX INFO: loaded from: classes.dex */
public class BrowseManager extends Item {
    private static BrowseManager mInstance = null;
    private Trace.Tag TAG;
    private Container mCurrentContainer;
    public boolean mFirstFlag;
    private Container mRootContainer;

    public BrowseManager() {
        this.TAG = Trace.Tag.ML;
        this.mRootContainer = null;
        this.mCurrentContainer = null;
        this.mFirstFlag = false;
    }

    public BrowseManager(Action browseAction) {
        super(null, browseAction);
        this.TAG = Trace.Tag.ML;
        this.mRootContainer = null;
        this.mCurrentContainer = null;
        this.mFirstFlag = false;
        Trace.d(this.TAG, "BrowseManager()");
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
        Trace.d(this.TAG, "Performance Check Point : start doBrowse()");
        this.mFirstFlag = false;
        this.mRootContainer.setId("0");
        this.mRootContainer.getItemList().clear();
        this.mCurrentContainer.getItemList().clear();
        if (!this.mFirstFlag) {
            Trace.d(this.TAG, "mFirstFlag");
            int cnt = this.mRootContainer.browse(100, 100);
            Trace.d(this.TAG, "cnt = " + cnt);
            this.mFirstFlag = true;
            if (this.mRootContainer.getItemList().size() > 0) {
                this.mRootContainer.setmCurrentContainerIndex(1);
            }
        }
        int index = this.mRootContainer.getmCurrentContainerIndex();
        Trace.d(this.TAG, "Index = " + index);
        int currentCnt = 0;
        if (Container.mContainerIndex > 0) {
            int size = this.mRootContainer.getItemList().size();
            Trace.d(this.TAG, "size = " + size);
            this.mCurrentContainer = (Container) this.mRootContainer.getItemList().getItem(index - 1);
            while (currentCnt < 1000) {
                int remainCnt = 1000 - currentCnt;
                Trace.d(this.TAG, "currentCnt = " + currentCnt + ", remainCnt = " + remainCnt);
                int count = this.mCurrentContainer.browse(100, remainCnt);
                Trace.d(this.TAG, "Result = " + count);
                currentCnt += count;
                if (count != 100 && currentCnt < 1000) {
                    Trace.d(this.TAG, "less than NUM_OF_ITEMS : currentCnt = " + currentCnt);
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
        Collections.sort(FileSharing.mContainerList, new NameAscCompare());
        Trace.d(this.TAG, "doBrowse = " + currentCnt);
        Trace.d(this.TAG, "Performance Check Point : end doBrowse()");
        return currentCnt;
    }

    static class NameAscCompare implements Comparator<Item> {
        NameAscCompare() {
        }

        @Override // java.util.Comparator
        public int compare(Item arg0, Item arg1) {
            return arg1.getDate().compareTo(arg0.getDate());
        }
    }
}
