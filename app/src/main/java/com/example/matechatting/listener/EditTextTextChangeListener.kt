package com.example.matechatting.listener

import android.text.Editable
import android.text.TextWatcher

class EditTextTextChangeListener(
    val afterTextChangedBack: (p0: Editable) -> Unit = {},
    val beforeTextChangedBack: (p0: CharSequence, p1: Int, p2: Int, p3: Int) -> Unit
    = { charSequence: CharSequence, i: Int, i1: Int, i2: Int -> },
    val onTextChangedBack: (p0: CharSequence, p1: Int, p2: Int, p3: Int) -> Unit
    = { charSequence: CharSequence, i: Int, i1: Int, i2: Int -> }
) : TextWatcher {
    override fun afterTextChanged(p0: Editable?) {
        p0?.let {
            afterTextChangedBack(it)
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        p0?.let {
            beforeTextChangedBack(it, p1, p2, p3)
        }
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        p0?.let {
            onTextChangedBack(it, p1, p2, p3)
        }
    }
}