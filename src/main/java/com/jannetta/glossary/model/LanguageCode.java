package com.jannetta.glossary.model;

public class LanguageCode {

    private String code;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LanguageCode(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    
}
