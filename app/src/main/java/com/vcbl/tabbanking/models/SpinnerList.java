package com.vcbl.tabbanking.models;

/**
 * Created by ecosoft2 on 30-Dec-17.
 */

public class SpinnerList {

    private int id;
    private String name;
    private String description;
    private boolean selected;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof SpinnerList){
            SpinnerList c = (SpinnerList)obj;
            if(c.getName().equals(name) && c.getId()==id ) return true;
        }
        return false;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
