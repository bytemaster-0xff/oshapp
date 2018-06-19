package com.softwarelogistics.oshgeo.poc.utils;

import java.util.regex.Pattern;

public class ValidationUtils {
    public static boolean isValidDBName(String name) {
        String regex = "^[a-z][a-z0-9]{2,20}$";
        return Pattern.compile(regex).matcher(name).find();
    }

    public static boolean isValidIPOrDomain(String name) {
        if(Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$").matcher(name).find()){
            return true;
        }

        if(Pattern.compile("^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$").matcher(name).find()) {
            return true;
        }

        return false;
    }
}
