
package com.nascenia.albarakahhajj.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Installment {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("amount")
    @Expose
    private Integer amount;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

}
