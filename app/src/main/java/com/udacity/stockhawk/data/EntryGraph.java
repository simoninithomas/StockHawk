package com.udacity.stockhawk.data;



public class EntryGraph {
    private String mDate;
    private float mPrice;

    public EntryGraph(String mDate, float mPrice){
        this.mDate = mDate;
        this.mPrice = mPrice;
    }

    public String getmDate() {
        return mDate;
    }

    public float getmPrice() {
        return mPrice;
    }
}
