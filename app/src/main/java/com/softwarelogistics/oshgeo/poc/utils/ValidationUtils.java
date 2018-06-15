package com.softwarelogistics.oshgeo.poc.utils;

import java.util.regex.Pattern;

public class ValidationUtils {
    public static boolean isValidDBName(String name) {
        String regex = "^[a-z][a-z0-9]{2,20}$";
        return Pattern.compile(regex).matcher(name).find();
    }
}
