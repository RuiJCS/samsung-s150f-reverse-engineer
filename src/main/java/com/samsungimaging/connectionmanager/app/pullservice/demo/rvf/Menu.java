package com.samsungimaging.connectionmanager.app.pullservice.demo.rvf;

/* JADX INFO: loaded from: classes.dex */
public class Menu {
    private String Name;
    private int iconResourceId;
    private boolean isSelected;

    public Menu(String _Name) {
        this.Name = _Name;
    }

    public String getName() {
        return this.Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public int getIconResourceId() {
        return this.iconResourceId;
    }

    public void setIconResourceId(int iconResourceId) {
        this.iconResourceId = iconResourceId;
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
