package com.softwarelogistics.oshgeo.poc.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateParser {
    public static Date parse(String input )  {
        String originalDate = input;

        //NOTE: SimpleDateFormat uses GMT[-+]hh:mm for the TZ which breaks
        //things a bit.  Before we go on we have to repair this.
        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSz" );


        //this is zero time so we need to add that TZ indicator for
        if ( originalDate.endsWith( "Z" ) ) {
            input = input.substring( 0, input.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;

            String s0 = input.substring( 0, input.length() - inset );
            String s1 = input.substring( input.length() - inset, input.length() );

            input = s0 + "GMT" + s1;
        }

        try {
            return df.parse(input);
        }
        catch(java.text.ParseException ex){
            //TODO: Is there a try parse?
        }

        try {
            return new Date(Double.valueOf(originalDate).longValue() * 1000);
        }
        catch(NumberFormatException ex){
            return new Date();
        }
        catch(Exception ex){
            return new Date();
        }
    }
}

