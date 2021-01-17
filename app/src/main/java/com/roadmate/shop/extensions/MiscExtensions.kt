package com.roadmate.shop.extensions

import java.text.SimpleDateFormat
import java.util.*

/**
 * Miscellaneous extension methods
 */

/**
 * diIfTrue - Boolean extension method to execute a block of code if the value is True.
 */
infix fun Boolean.doIfTrue(block: () -> Unit): Boolean{
    return if(this){
        block()
        this
    }
    else{
        false
    }
}

/**
 * doIfFalse - Boolean extension method to execute a block of code if the value is False.
 */
infix fun Boolean.doIfFalse(block: () -> Unit): Boolean{
    if(!this){
        block()
    }

    return false
}

/**
 * doIfFalse - Boolean extension method to execute a block of code if the value is False.
 */
infix fun Boolean.elseDo(block: () -> Unit): Boolean {
    if(!this) block()

    return this
}

/**
 * formattedDate - Generic extension method to format a Date object to specified format,
 * default is MM/dd/yyyy format
 */
fun formattedDate(date: Date, pattern: String = "MM/dd/yyyy"): String{
    val df = SimpleDateFormat(pattern, Locale.ENGLISH)

    return df.format(date);
}

/**
 * formatted24HourTime - Generic extension method to format a Date object to specified time format,
 * default is "HH:mm:ss" format
 */
fun formatted24HourTime(date: Date, pattern: String = "HH:mm:ss"): String{
    val df = SimpleDateFormat(pattern, Locale.ENGLISH)

    return df.format(date)
}

/**
 * formattedTime - Generic extension method to format a Date object to specified time format,
 * default is "HH:mm a" format
 */
fun formattedTime(date: Date): String{
    val df = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

    return df.format(date)
}
