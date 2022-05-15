package com.example.boardgamefinder.presentation.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.boardgamefinder.R
import com.example.boardgamefinder.databinding.ActivityLogInBinding
import com.example.boardgamefinder.databinding.ActivityPasswordRecoveryBinding
import com.example.boardgamefinder.presentation.viewModels.LogInViewModel
import com.example.boardgamefinder.presentation.viewModels.PasswordRecoveryViewModel
import com.google.android.material.textfield.TextInputEditText

internal class PasswordRecoveryActivity : AppCompatActivity() {
    private var _binding: ActivityPasswordRecoveryBinding? = null
    private val binding get() = _binding!!

    private val passwordRecoveryViewModel: PasswordRecoveryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPasswordRecoveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.send.setOnClickListener {
            if(binding.email.text?.isEmpty() == true)
                // ToDo move to resources
                Toast.makeText(this, "Please, input email address", Toast.LENGTH_SHORT).show()
            else
                passwordRecoveryViewModel.send(binding.email.text.toString())
        }

        passwordRecoveryViewModel.success.observe(this){
            openLogIntActivity()
        }
    }

    private fun openLogIntActivity(){
        val intent = Intent(this, LogInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}