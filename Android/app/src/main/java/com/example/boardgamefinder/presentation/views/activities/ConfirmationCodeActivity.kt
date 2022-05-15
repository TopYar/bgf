package com.example.boardgamefinder.presentation.views.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.boardgamefinder.R
import com.example.boardgamefinder.core.GenericKeyEvent
import com.example.boardgamefinder.core.GenericOnFocus
import com.example.boardgamefinder.core.GenericTextWatcher
import com.example.boardgamefinder.core.MySettings
import com.example.boardgamefinder.databinding.ActivityConfirmationCodeBinding
import com.example.boardgamefinder.presentation.viewModels.ConfirmationCodeViewModel
import java.util.*

internal class ConfirmationCodeActivity : AppCompatActivity() {

    private var _binding: ActivityConfirmationCodeBinding? = null
    private val binding get() = _binding!!
    private val confirmationCodeViewModel: ConfirmationCodeViewModel by viewModels()

    private var resendTimer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityConfirmationCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setInputCode()

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        // resend button states
        confirmationCodeViewModel.resend.observe(this){
            if(confirmationCodeViewModel.resend.value == true) {
                binding.resendCode.isEnabled = true
                binding.resendCode.setTextColor(MySettings.getSecondaryColor(this))
                binding.nextResend.visibility = View.INVISIBLE
            }else{
                binding.resendCode.isEnabled = false
                binding.resendCode.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
                binding.nextResend.visibility = View.VISIBLE
            }
        }

        //resend button
        binding.resendCode.setOnClickListener { confirmationCodeViewModel.resendCode() }

        // confirmButton
        binding.confirm.setOnClickListener {
            with(getCode()){
                if(length != 4)
                    // ToDo move to string resourse
                    Toast.makeText(this@ConfirmationCodeActivity, "Please, input the confirmation code first", Toast.LENGTH_SHORT).show()
                else
                    confirmationCodeViewModel.confirm(this)
            }
        }

        confirmationCodeViewModel.confirmed.observe(this){
            if(confirmationCodeViewModel.confirmed.value == true) {
                // open main activity
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }else{
                // ToDo clean code input
            }
        }

        confirmationCodeViewModel.goBack.observe(this){
            if(confirmationCodeViewModel.goBack.value == true)
                onBackPressed()
        }
    }

    private fun getCode() : String{
        return binding.code1.text.toString()+
                binding.code2.text.toString()+
                binding.code3.text.toString()+
                binding.code4.text.toString()
    }

    private fun setInputCode(){
        with(binding){
            // For switching to the next
            code1.addTextChangedListener(GenericTextWatcher(code2))
            code2.addTextChangedListener(GenericTextWatcher(code3))
            code3.addTextChangedListener(GenericTextWatcher(code4))
            code4.addTextChangedListener(GenericTextWatcher(null))

            // For delete and switching to the previous
            code1.setOnKeyListener(GenericKeyEvent(code1, null))
            code2.setOnKeyListener(GenericKeyEvent(code2, code1))
            code3.setOnKeyListener(GenericKeyEvent(code3, code2))
            code4.setOnKeyListener(GenericKeyEvent(code4, code3))

            // For cleaning up when focusing on
            code1.onFocusChangeListener = GenericOnFocus()
            code2.onFocusChangeListener = GenericOnFocus()
            code3.onFocusChangeListener = GenericOnFocus()
            code4.onFocusChangeListener = GenericOnFocus()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
