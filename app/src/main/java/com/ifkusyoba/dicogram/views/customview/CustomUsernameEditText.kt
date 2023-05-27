package com.ifkusyoba.dicogram.views.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.ifkusyoba.dicogram.R

class CustomUsernameEditText : AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        context.apply {
            background = ContextCompat.getDrawable(context, R.drawable.custom_edit_text)
            setTextColor(ContextCompat.getColor(context, R.color.dicoding_light_grey))
            setHintTextColor(ContextCompat.getColor(context, R.color.dicoding_light_grey))
            hint = context.getString(R.string.username)
        }
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // *Not Implemented*
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                error = if (p0!!.length < 5) context.getString(R.string.invalid_username) else null
            }

            override fun afterTextChanged(p0: Editable?) {
                // *Not Implemented*
            }
        })
    }
}