package com.example.boardgamefinder.core

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class GenericTextWatcher(private val nextView: EditText?) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable) {
        val text = s.toString()
        if (nextView != null && text.length == 1) {
            nextView.requestFocus()
        } else if (nextView == null) {
            //ToDo hide the keyboard
        }
    }
}