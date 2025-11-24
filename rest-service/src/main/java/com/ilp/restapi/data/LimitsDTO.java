package com.ilp.restapi.data;


public class LimitsDTO {

    private Double lower;
    private Double upper;

    public LimitsDTO() {}

    public Double getLower() { return lower; }
    public void setLower(Double lower) { this.lower = lower; }

    public Double getUpper() { return upper; }
    public void setUpper(Double upper) { this.upper = upper; }
}

