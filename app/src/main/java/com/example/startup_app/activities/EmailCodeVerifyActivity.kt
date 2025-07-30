package com.example.startup_app.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.EditText
import android.widget.Toast
import com.example.startup_app.databinding.ActivityEmailCodeVerifyBinding
import java.util.Locale

class EmailCodeVerifyActivity : BaseActivity() {
    lateinit var binding: ActivityEmailCodeVerifyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailCodeVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.back.setOnClickListener {
            startActivity(
                Intent(this@EmailCodeVerifyActivity
                    ,ForgotPasswordActivity::class.java),
                ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                ).toBundle()
            )
        }
        val lang = Locale.getDefault().language
        val isFr = lang == "fr"
        val code = intent.getStringExtra("verification_code")
        Toast.makeText(this@EmailCodeVerifyActivity, if(isFr) "votre code est : $code" else "your code is : $code", Toast.LENGTH_SHORT)
            .show()
        setupOtpInputs(binding.text1, binding.text2, binding.text3, binding.text4, binding.text5)

        binding.verifybtn.setOnClickListener {
            val ips =
                listOf(binding.text1, binding.text2, binding.text3, binding.text4, binding.text5)
            var str = ""
            ips.forEach {
                str += it.text.toString()
            }
            if (str.toInt() == code?.toInt()) {
                val i = Intent(this@EmailCodeVerifyActivity, NewPasswordActivity::class.java).apply {
                    putExtra("code", code)
                }
                startActivity(
                    i,
                    ActivityOptions.makeCustomAnimation(
                        this,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    ).toBundle()
                )
            } else {
                Toast.makeText(this, "Code incorrect", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun setupOtpInputs(vararg fields: EditText) {
        for (i in fields.indices) {
            fields[i].addTextChangedListener(object : TextWatcher {
                private var isPasting = false

                override fun afterTextChanged(s: Editable?) {
                    val text = s.toString()
                    if (isPasting) {
                        isPasting = false
                        return
                    }

                    if (text.length > 1) {
                        isPasting = true
                        val chars = text.toCharArray()
                        fields[i].setText("")

                        for (j in chars.indices) {
                            if (i + j < fields.size) {
                                fields[i + j].setText(chars[j].toString())
                            }
                        }

                        val lastIndex = minOf(i + chars.size, fields.size) - 1
                        fields[lastIndex].requestFocus()
                        fields[lastIndex].setSelection(fields[lastIndex].text.length)
                        return
                    }

                    if (text.length == 1 && i < fields.lastIndex) {
                        fields[i + 1].requestFocus()
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            fields[i].setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL &&
                    event.action == KeyEvent.ACTION_DOWN &&
                    fields[i].text.isEmpty() &&
                    i > 0
                ) {
                    fields[i - 1].requestFocus()
                    fields[i - 1].setSelection(fields[i - 1].text.length)
                    return@setOnKeyListener true
                }
                false
            }
        }
    }


}