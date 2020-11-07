package com.appzoro.milton.ui.view.otp_view

import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.View.OnKeyListener
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import com.appzoro.milton.R
import com.appzoro.milton.utility.AppLogger

/**
 * this is custom otp view for taking 6 digit otp no from user
 */
class OtpView : LinearLayout, PinEditTextListener {

    private lateinit var mOtpOneField: PinEditText
    private lateinit var mOtpTwoField: PinEditText
    private lateinit var mOtpThreeField: PinEditText
    private lateinit var mOtpFourField: PinEditText
    private lateinit var mOtpFiveField: PinEditText
    private lateinit var mOtpSixField: PinEditText

    var currentFocusedEditText: PinEditText? = null
        private set

    var otp: String
        get() = makeOTP()
        set(otp) {
            if (otp.length != 6) {
                AppLogger.e("OTPView Invalid otp param")
                return
            }
            if (mOtpOneField.inputType == InputType.TYPE_CLASS_NUMBER && !otp.matches("[0-9]+".toRegex())) {
                AppLogger.e("OTPView OTP doesn't match INPUT TYPE")
                return
            }

            mOtpOneField.setText(otp[0].toString())
            mOtpTwoField.setText(otp[1].toString())
            mOtpThreeField.setText(otp[2].toString())
            mOtpFourField.setText(otp[3].toString())
            mOtpFiveField.setText(otp[4].toString())
            mOtpSixField.setText(otp[5].toString())
        }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    private fun init() {
        //val styles = context.obtainStyledAttributes(attrs, R.styleable.OtpView)
        val mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        mInflater.inflate(R.layout.otpview_layout, this)

        mOtpOneField = findViewById(R.id.otp_one_edit_text)
        mOtpTwoField = findViewById(R.id.otp_two_edit_text)
        mOtpThreeField = findViewById(R.id.otp_three_edit_text)
        mOtpFourField = findViewById(R.id.otp_four_edit_text)
        mOtpFiveField = findViewById(R.id.otp_five_edit_text)
        mOtpSixField = findViewById(R.id.otp_six_edit_text)
        setFocusListener()
        setOnTextChangeListener()
        mOtpOneField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                AppLogger.e("after $after")
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                AppLogger.e("onTextChanged count $count")
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    mOtpTwoField.requestFocus()
                }
            }
        })
        mOtpOneField.addListener(this)
        mOtpTwoField.addListener(this)
        mOtpThreeField.addListener(this)
        mOtpFourField.addListener(this)
        mOtpFiveField.addListener(this)
        mOtpSixField.addListener(this)
    }

    private fun makeOTP(): String {
        return (mOtpOneField.text.toString()
                + mOtpTwoField.text.toString()
                + mOtpThreeField.text.toString()
                + mOtpFourField.text.toString()
                + mOtpFiveField.text.toString()
                + mOtpSixField.text.toString())
    }

    fun hasValidOTP(): Boolean {
        return makeOTP().length == 6
    }

    private fun setFocusListener() {
        val onFocusChangeListener = OnFocusChangeListener { v, _ ->
            currentFocusedEditText = v as PinEditText
            currentFocusedEditText?.setSelection(currentFocusedEditText?.text?.length ?: 0)
        }

        val onKeyListener = OnKeyListener { _, keyCode, _ ->

            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (currentFocusedEditText != null)
                    if (currentFocusedEditText != mOtpOneField)
                        if (currentFocusedEditText?.text.toString()
                        .isEmpty()
                ) currentFocusedEditText?.focusSearch(View.FOCUS_LEFT)?.requestFocus()
            }
            false
        }

        mOtpOneField.setOnKeyListener(onKeyListener)
        mOtpTwoField.setOnKeyListener(onKeyListener)
        mOtpThreeField.setOnKeyListener(onKeyListener)
        mOtpFourField.setOnKeyListener(onKeyListener)
        mOtpFiveField.setOnKeyListener(onKeyListener)
        mOtpSixField.setOnKeyListener(onKeyListener)

        mOtpTwoField.onFocusChangeListener = onFocusChangeListener
        mOtpThreeField.onFocusChangeListener = onFocusChangeListener
        mOtpFourField.onFocusChangeListener = onFocusChangeListener
        mOtpFiveField.onFocusChangeListener = onFocusChangeListener
        mOtpSixField.onFocusChangeListener = onFocusChangeListener
        mOtpOneField.onFocusChangeListener = onFocusChangeListener

    }

    private fun setOnTextChangeListener() {

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                try {
                    if (currentFocusedEditText?.focusSearch(View.FOCUS_RIGHT) != null || currentFocusedEditText?.focusSearch(
                            View.FOCUS_LEFT
                        ) != null
                    ) {
                        if (currentFocusedEditText!!.text!!.isNotEmpty() && currentFocusedEditText !== mOtpSixField) {
                            currentFocusedEditText!!.focusSearch(View.FOCUS_RIGHT).requestFocus()
                        } else if (currentFocusedEditText!!.text!!.isNotEmpty() && currentFocusedEditText === mOtpSixField) {
                            val imm = context.getSystemService(
                                Context.INPUT_METHOD_SERVICE
                            ) as InputMethodManager
                            imm.hideSoftInputFromWindow(windowToken, 0)
                        } else {
                            val currentValue = currentFocusedEditText!!.text.toString()
                            if (currentValue.isEmpty() && currentFocusedEditText!!.selectionStart <= 0) {
                                currentFocusedEditText!!.focusSearch(View.FOCUS_LEFT)
                                    .requestFocus()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        mOtpOneField.addTextChangedListener(textWatcher)
        mOtpTwoField.addTextChangedListener(textWatcher)
        mOtpThreeField.addTextChangedListener(textWatcher)
        mOtpFourField.addTextChangedListener(textWatcher)
        mOtpFiveField.addTextChangedListener(textWatcher)
        mOtpSixField.addTextChangedListener(textWatcher)
    }

    /*back press to delete otp pin*/
    fun simulateDeletePress() {
        currentFocusedEditText?.setText("")
    }

    override fun onUpdate() {

        val clipboard: ClipboardManager? =
            context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
//        var text = clipboard?.text.toString()
        var text = (clipboard?.primaryClip?.getItemAt(0)?.text ?: "").toString()
        AppLogger.e("onUpdate text $text")

        try {
            text.toLong()
            if (text.length > 1) {

                text = text.replace("+", "")

                AppLogger.e("length ${text.length}")
                for (position in 0..(text.length)) {
                    AppLogger.e("position $position")
                    //AppLogger.e("test ${text[position]}")
                    if (position > 7) {
                        break
                    }

                    if (position == 0) {
                        val index = text[0].toString()
                        mOtpOneField.setText(index)
                    }
                    if (position == 1) {
                        val index = text[1].toString()
                        mOtpTwoField.setText(index)
                    }
                    if (position == 2) {
                        val index = text[2].toString()
                        mOtpThreeField.setText(index)
                    }
                    if (position == 3) {
                        val index = text[3].toString()
                        mOtpFourField.setText(index)
                    }
                    if (position == 4) {
                        val index = text[4].toString()
                        mOtpFiveField.setText(index)
                    }
                    if (position == 5) {
                        val index = text[5].toString()
                        mOtpSixField.setText(index)
                    }
                }

                mOtpOneField.setSelection((mOtpOneField.text.toString()).length)
                mOtpTwoField.setSelection((mOtpTwoField.text.toString()).length)
                mOtpThreeField.setSelection((mOtpThreeField.text.toString()).length)
                mOtpFourField.setSelection((mOtpFourField.text.toString()).length)
                mOtpFiveField.setSelection((mOtpFiveField.text.toString()).length)
                mOtpSixField.setSelection((mOtpSixField.text.toString()).length)

            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}