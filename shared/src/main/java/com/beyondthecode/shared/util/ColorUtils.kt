package com.beyondthecode.shared.util

import java.lang.Long

object ColorUtils{

    fun parseHexcolor(colorString: String): Int{
        if(colorString.isNotEmpty() && colorString[0] == '#'){
            //Use a long to avoid rollovers on #ffXXXXXX
            var color = Long.parseLong(colorString.substring(1), 16)
            if(colorString.length == 7){
                //Set the alpha value
                color = color or 0x00000000ff000000
            }else if(colorString.length != 9){
                throw IllegalArgumentException("Unknown color: $colorString")
            }
            return color.toInt()
        }
        throw IllegalArgumentException("Unknown color")
    }
}