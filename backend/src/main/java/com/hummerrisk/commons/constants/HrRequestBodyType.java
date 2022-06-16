package com.hummerrisk.commons.constants;

public enum HrRequestBodyType {


    KV("KeyValue"), FORM_DATA("Form Data"), RAW("Raw");

    private final String value;

    HrRequestBodyType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
