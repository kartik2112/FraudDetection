package myapps;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author karti
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "idx",
    "step",
    "customer",
    "age",
    "gender",
    "zipcodeOri",
    "merchant",
    "zipMerchant",
    "category",
    "amount",
    "fraud"
})
public class TransactionDataTrimmer {

    @JsonProperty("idx")
    private Long idx;

    @JsonProperty("step")
    private Long step;

    @JsonProperty("customer")
    private String customer;

    @JsonProperty("age")
    private String age;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("zipcodeOri")
    private String zipcodeOri;

    @JsonProperty("merchant")
    private String merchant;

    @JsonProperty("zipMerchant")
    private String zipMerchant;

    @JsonProperty("category")
    private String category;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("fraud")
    private Integer fraud;

    @JsonProperty("idx")
    public Long getIdx() {
        return idx;
    }

    @JsonProperty("idx")
    public void setIdx(Long idx) {
        this.idx = idx;
    }

    @JsonProperty("step")
    public Long getStep() {
        return step;
    }

    @JsonProperty("step")
    public void setStep(Long step) {
        this.step = step;
    }

    @JsonProperty("customer")
    public String getCustomer() {
        return customer;
    }

    @JsonProperty("customer")
    public void setCustomer(String customer) {
        this.customer = customer.substring(1,customer.length()-1);
    }

    @JsonProperty("age")
    public String getAge() {
        return age;
    }

    @JsonProperty("age")
    public void setAge(String age) {
        this.age = age.substring(1,age.length()-1);
    }

    @JsonProperty("gender")
    public String getGender() {
        return gender;
    }

    @JsonProperty("gender")
    public void setGender(String gender) {
        this.gender = gender.substring(1,gender.length()-1);
    }

    @JsonProperty("zipcodeOri")
    public String getZipcodeOri() {
        return zipcodeOri;
    }

    @JsonProperty("zipcodeOri")
    public void setZipcodeOri(String zipcodeOri) {
        this.zipcodeOri = zipcodeOri;
    }

    @JsonProperty("merchant")
    public String getMerchant() {
        return merchant;
    }

    @JsonProperty("merchant")
    public void setMerchant(String merchant) {
        this.merchant = merchant.substring(1,merchant.length()-1);
    }

    @JsonProperty("zipMerchant")
    public String getZipMerchant() {
        return zipMerchant;
    }

    @JsonProperty("zipMerchant")
    public void setZipMerchant(String zipMerchant) {
        this.zipMerchant = zipMerchant;
    }

    @JsonProperty("category")
    public String getCategory() {
        return category;
    }

    @JsonProperty("category")
    public void setCategory(String category) {
        this.category = category.substring(1,category.length()-1);
    }

    @JsonProperty("amount")
    public BigDecimal getAmount() {
        return amount;
    }

    @JsonProperty("amount")
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @JsonProperty("fraud")
    public Integer getFraud() {
        return fraud;
    }

    @JsonProperty("fraud")
    public void setFraud(Integer fraud) {
        this.fraud = fraud;
    }

    @Override
    public String toString(){
        return new ToStringBuilder(this)
                .append("idx",idx)
                .append("step",step)
                .append("customer",customer)
                .append("age",age)
                .append("gender",gender)
                .append("zipcodeOri",zipcodeOri)
                .append("merchant",merchant)
                .append("zipMerchant",zipMerchant)
                .append("category",category)
                .append("amount",amount)
                .append("fraud",fraud)
                .toString();
    }
}
