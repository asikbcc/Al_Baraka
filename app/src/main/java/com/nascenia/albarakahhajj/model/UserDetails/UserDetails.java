
package com.nascenia.albarakahhajj.model.UserDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDetails {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("applicant_no")
    @Expose
    private String applicantNo;
    @SerializedName("applicant_name")
    @Expose
    private String applicantName;
    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;
    @SerializedName("installment")
    @Expose
    private com.nascenia.albarakahhajj.model.Installment installment;
    @SerializedName("total_diposit")
    @Expose
    private com.nascenia.albarakahhajj.model.TotalDiposit totalDiposit;
    @SerializedName("last_payment")
    @Expose
    private com.nascenia.albarakahhajj.model.LastPayment lastPayment;
    @SerializedName("due")
    @Expose
    private com.nascenia.albarakahhajj.model.Due due;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getApplicantNo() {
        return applicantNo;
    }

    public void setApplicantNo(String applicantNo) {
        this.applicantNo = applicantNo;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public com.nascenia.albarakahhajj.model.Installment getInstallment() {
        return installment;
    }

    public void setInstallment(com.nascenia.albarakahhajj.model.Installment installment) {
        this.installment = installment;
    }

    public com.nascenia.albarakahhajj.model.TotalDiposit getTotalDiposit() {
        return totalDiposit;
    }

    public void setTotalDiposit(com.nascenia.albarakahhajj.model.TotalDiposit totalDiposit) {
        this.totalDiposit = totalDiposit;
    }

    public com.nascenia.albarakahhajj.model.LastPayment getLastPayment() {
        return lastPayment;
    }

    public void setLastPayment(com.nascenia.albarakahhajj.model.LastPayment lastPayment) {
        this.lastPayment = lastPayment;
    }

    public com.nascenia.albarakahhajj.model.Due getDue() {
        return due;
    }

    public void setDue(com.nascenia.albarakahhajj.model.Due due) {
        this.due = due;
    }

}
