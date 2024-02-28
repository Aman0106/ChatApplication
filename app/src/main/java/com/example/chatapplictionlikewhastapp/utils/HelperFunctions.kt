package com.example.chatapplictionlikewhastapp.utils

import android.util.Log

object HelperFunctions {

    fun removeAllSpacesFromString(str: String):String {
        return str.replace(" ", "")
    }

    fun normalisePhoneNumber(str: String): String {
        var number = removeAllSpacesFromString(str)
        if(number.length > 10) {
            number = number.drop(3)
        }
        return number
    }
}