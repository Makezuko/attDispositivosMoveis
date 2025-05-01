package com.example.login_simulation

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_success)

        val successMessage = findViewById<TextView>(R.id.successMessage)
        successMessage.text = "Login realizado com sucesso!"
    }
}
