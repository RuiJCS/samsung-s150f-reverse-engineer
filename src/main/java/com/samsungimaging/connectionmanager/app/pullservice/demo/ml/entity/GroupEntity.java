package com.samsungimaging.connectionmanager.app.pullservice.demo.ml.entity;

import com.samsungimaging.connectionmanager.app.pullservice.demo.ml.Item;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class GroupEntity {
    public String Name;
    public int groupExpandedStatus = 1;
    public boolean groupCheckedAllStatus = false;
    public List<GroupItemEntity> GroupItemCollection = new ArrayList();

    public class GroupItemEntity {
        public ArrayList<Item> ItemList;

        public GroupItemEntity() {
        }
    }
}
