package com.example.boardgamefinder.core

import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.EditText

class GenericOnFocus : OnFocusChangeListener {
    override fun onFocusChange(v: View, hasFocus: Boolean) {
        if (hasFocus) {
            (v as EditText).text = null
        }
    }
}