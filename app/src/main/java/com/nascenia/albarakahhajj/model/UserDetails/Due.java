
package com.nascenia.albarakahhajj.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Due {

    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("date")
    @Expose
    private String date;

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
