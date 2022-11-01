package com.juc;

import lombok.Getter;

public enum CountryEnum {

    ONE(1, "齐"),
    TWO(2, "楚"),
    THREE(3, "燕"),
    FOUR(4, "韩"),
    FIVE(5, "赵"),
    SIX(6, "魏");


    @Getter
    private final Integer code;

    @Getter
    private final String msg;

    CountryEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static CountryEnum getCountry(Integer code) {

        CountryEnum[] countryEnums = CountryEnum.values();
        for (CountryEnum eum : countryEnums) {
            if (eum.code.equals(code)) {
                return eum;
            }
        }
        return null;
    }
}
