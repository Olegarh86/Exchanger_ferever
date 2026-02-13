package models;

public record Currency(Integer id, String name, String code, String sign) {
    public Currency(String name, String code, String sign) {
        this(0, name, code, sign);
    }
}
