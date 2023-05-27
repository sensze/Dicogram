package com.ifkusyoba.dicogram.views.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.ifkusyoba.dicogram.R

class CustomPasswordEditText : AppCompatEditText {
    private var charLength = 0
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
            hint = context.getString(R.string.password)
        }
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // *Not Implemented*
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                charLength = p0!!.length
                error =
                    if (charLength < 8) context.getString(R.string.invalid_password) else null
            }

            override fun afterTextChanged(p0: Editable?) {
                // *Not Implemented*
            }
        })
    }
}