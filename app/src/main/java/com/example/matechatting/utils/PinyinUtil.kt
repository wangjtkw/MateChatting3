package com.example.matechatting.utils

import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination
import java.util.Objects.isNull


object PinyinUtil {

    fun getPinyin(str: String): String {
        val format = HanyuPinyinOutputFormat()
        format.caseType = HanyuPinyinCaseType.UPPERCASE
        format.toneType = HanyuPinyinToneType.WITHOUT_TONE
        val sb = StringBuilder()
        val charArray = str.toCharArray()
        for (c: Char in charArray) {
            if (' ' == c) {
                continue
            }
            if (c.toInt() >= -127 && c.toInt() <= 128) {
                sb.append(c)
            } else {
                val s: String = PinyinHelper.toHanyuPinyinStringArray(c, format)[0]
                sb.append(s)
            }
        }
        return sb.toString()
    }

    fun getFirstHeadWordChar(str: String): String {
        if (str.isEmpty()) {
            return ""
        }
        var convert = ""
        val word = str[0]
        // 提取汉字的首字母
        val pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word)
        convert += if (pinyinArray != null) {
            pinyinArray[0][0]
        } else {
            word
        }
        convert = string2AllTrim(convert)
        return convert.toUpperCase()
    }

    private fun string2AllTrim(value: String): String {
        return if (value.isEmpty()) {
            ""
        } else value.trim { it <= ' ' }.replace(" ", "")
    }


    fun getFirstSpell(chinese: String): String {
        val pybf = StringBuffer()
        val arr = chinese.toCharArray()
        val defaultFormat = HanyuPinyinOutputFormat()
        defaultFormat.caseType = HanyuPinyinCaseType.LOWERCASE
        defaultFormat.toneType = HanyuPinyinToneType.WITHOUT_TONE
        for (i in arr.indices) {
            if (arr[i].toInt() > 128) {
                try {
                    val temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat)
                    if (temp != null) {
                        pybf.append(temp[0][0])
                    }
                } catch (e: BadHanyuPinyinOutputFormatCombination) {
                    e.printStackTrace()
                }

            } else {
                pybf.append(arr[i])
            }
        }
        return pybf.toString().replace("\\W".toRegex(), "").trim { it <= ' ' }
    }
}