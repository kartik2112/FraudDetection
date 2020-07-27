/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myapps;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;

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
    "fraud",
    "count_1_day",
    "count_7_days",
    "count_30_days",
    "count_cust_merch_1_day",
    "count_cust_merch_7_days",
    "count_cust_merch_30_days"
})
public class EnhancedTransactionData {
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
    
    @JsonProperty("count_1_day")
    private Long last1DayTransCount;
    
    @JsonProperty("count_7_days")
    private Long last7DaysTransCount;
    
    @JsonProperty("count_30_days")
    private Long last30DaysTransCount;
    
    @JsonProperty("count_cust_merch_1_day")
    private Long last1DayTransCustMerchCount;
    
    @JsonProperty("count_cust_merch_7_days")
    private Long last7DaysTransCustMerchCount;
    
    @JsonProperty("count_cust_merch_30_days")
    private Long last30DaysTransCustMerchCount;

    public EnhancedTransactionData(TransactionData data, Long last1DayTransCount, Long last7DaysTransCount, Long last30DaysTransCount,Long last1DayTransCustMerchCount,Long last7DaysTransCustMerchCount,Long last30DaysTransCustMerchCount) {
        this.idx = data.getIdx();
        this.step = data.getStep();
        this.customer = data.getCustomer();
        this.age = data.getAge();
        this.gender = data.getGender();
        this.zipcodeOri = data.getZipcodeOri();
        this.merchant = data.getMerchant();
        this.zipMerchant = data.getZipMerchant();
        this.category = data.getCategory();
        this.amount = data.getAmount();
        this.fraud = data.getFraud();
        this.last1DayTransCount = last1DayTransCount;
        this.last7DaysTransCount = last7DaysTransCount;
        this.last30DaysTransCount = last30DaysTransCount;
        this.last1DayTransCustMerchCount = last1DayTransCustMerchCount;
        this.last7DaysTransCustMerchCount = last7DaysTransCustMerchCount;
        this.last30DaysTransCustMerchCount = last30DaysTransCustMerchCount;
    }

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
        this.customer = customer;
    }

    @JsonProperty("age")
    public String getAge() {
        return age;
    }

    @JsonProperty("age")
    public void setAge(String age) {
        this.age = age;
    }

    @JsonProperty("gender")
    public String getGender() {
        return gender;
    }

    @JsonProperty("gender")
    public void setGender(String gender) {
        this.gender = gender;
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
        this.merchant = merchant;
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
        this.category = category;
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

    @JsonProperty("count_1_day")
    public Long getLast1DayTransCount() {
        return last1DayTransCount;
    }

    @JsonProperty("count_1_day")
    public void setLast1DayTransCount(Long last1DayTransCount) {
        this.last1DayTransCount = last1DayTransCount;
    }

    @JsonProperty("count_7_days")
    public Long getLast7DaysTransCount() {
        return last7DaysTransCount;
    }

    @JsonProperty("count_7_days")
    public void setLast7DaysTransCount(Long last7DaysTransCount) {
        this.last7DaysTransCount = last7DaysTransCount;
    }

    @JsonProperty("count_30_days")
    public Long getLast30DaysTransCount() {
        return last30DaysTransCount;
    }

    @JsonProperty("count_30_days")
    public void setLast30DaysTransCount(Long last30DaysTransCount) {
        this.last30DaysTransCount = last30DaysTransCount;
    }

    @JsonProperty("count_cust_merch_1_day")
    public Long getLast1DayTransCustMerchCount() {
        return last1DayTransCustMerchCount;
    }

    @JsonProperty("count_cust_merch_1_day")
    public void setLast1DayTransCustMerchCount(Long last1DayTransCustMerchCount) {
        this.last1DayTransCustMerchCount = last1DayTransCustMerchCount;
    }

    @JsonProperty("count_cust_merch_7_days")
    public Long getLast7DaysTransCustMerchCount() {
        return last7DaysTransCustMerchCount;
    }

    @JsonProperty("count_cust_merch_7_days")
    public void setLast7DaysTransCustMerchCount(Long last7DaysTransCustMerchCount) {
        this.last7DaysTransCustMerchCount = last7DaysTransCustMerchCount;
    }

    @JsonProperty("count_cust_merch_30_days")
    public Long getLast30DaysTransCustMerchCount() {
        return last30DaysTransCustMerchCount;
    }

    @JsonProperty("count_cust_merch_30_days")
    public void setLast30DaysTransCustMerchCount(Long last30DaysTransCustMerchCount) {
        this.last30DaysTransCustMerchCount = last30DaysTransCustMerchCount;
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
                .append("count_1_day",last1DayTransCount)
                .append("count_7_days",last7DaysTransCount)
                .append("count_30_days",last30DaysTransCount)                
                .toString();
    }
}
