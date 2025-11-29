package com.kairionyuta.ilp.graphql_gateway.data;

public class QueryFilterInputDTO {
    private String attribute;
    private String operator;
    private String value;

    public QueryFilterInputDTO() {
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
