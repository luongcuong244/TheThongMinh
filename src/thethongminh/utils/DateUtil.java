/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package thethongminh.utils;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author ACER
 */
public class DateUtil {
 
    
    public static Date  convertStringToSqlDate(String dateString, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        java.util.Date utilDate = sdf.parse(dateString); 
        return new Date(utilDate.getTime()); 
    }
}
