package com.example.boardgamefinder.presentation.views.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.boardgamefinder.core.MySettings
import com.example.boardgamefinder.databinding.ActivityConfirmationCodeBinding
import com.example.boardgamefinder.databinding.ActivitySignUpBinding
import com.example.boardgamefinder.domain.models.Email
import com.example.boardgamefinder.domain.models.Password
import com.example.boardgamefinder.presentation.viewModels.SignUpViewModel

internal class SignUpActivity : AppCompatActivity() {
    private var _binding: ActivitySignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var mSettings: SharedPreferences
    private val signUpViewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkCode()

        _binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {openLogInActivity()}

        binding.signupButton.setOnClickListener {
            signUpViewModel.register(
                Email(binding.editEmail.text.toString()),
                Password(binding.editPassword.text.toString()),
                Password(binding.editPasswordConfirm.text.toString())) }

        signUpViewModel.success.observe(this){
            if(signUpViewModel.success.value == true) {
                // open confirmation code activity
                val intent = Intent(this, ConfirmationCodeActivity::class.java)
                startActivity(intent)
            }else{
                //ToDo highlight fields
            }
        }
    }

    private fun openLogInActivity(){
        val intent = Intent(this, LogInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun checkCode(){
        mSettings = getSharedPreferences(MySettings.APP_PREFERENCES, MODE_PRIVATE)

        // has to write code
        if (mSettings.getBoolean(MySettings.APP_PREFERENCES_CODE_STAGE, false)) {
            val intent = Intent(this, ConfirmationCodeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}