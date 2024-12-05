package com.dicoding.thriftify.ui.custom

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import com.dicoding.thriftify.R
import com.google.android.material.textfield.TextInputEditText

class PhoneEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextInputEditText(context, attrs, defStyleAttr) {
    init {
        setupView()
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrBlank()) {
                    error = context.getString(R.string.phone_empty)
                } else if (s.toString().length < 8 || s.toString().length > 13) {
                    error = context.getString(R.string.phone_invalid)
                } else if (!isValidPhoneNumber(s.toString())) {
                    error = context.getString(R.string.phone_must_number)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return phoneNumber.matches(Regex("^\\d{8,13}$")) && phoneNumber.all { it.isDigit() }
    }

    private fun setupView() {

        isFocusable = true

        isFocusableInTouchMode = true

        isClickable = true

        isEnabled = true

        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        gravity = Gravity.CENTER_VERTICAL

        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

    }
}