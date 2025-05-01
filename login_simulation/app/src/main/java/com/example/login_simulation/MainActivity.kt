package com.example.login_simulation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val errorMessage = findViewById<TextView>(R.id.errorMessage)

        val showPasswordButton = findViewById<ImageButton>(R.id.showPasswordButton)

        var isPasswordVisible = false

        showPasswordButton.setOnClickListener {
            if (isPasswordVisible) {
                passwordEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                showPasswordButton.setImageResource(R.drawable.ic_eye_closed)
            } else {
                passwordEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                showPasswordButton.setImageResource(R.drawable.ic_eye_open)
            }
            passwordEditText.setSelection(passwordEditText.text.length)
            isPasswordVisible = !isPasswordVisible
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            errorMessage.visibility = TextView.GONE

            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha o e-mail", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha a senha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val validEmail = "usuario@valido.com"
            val validPassword = "senha123"

            if (email != validEmail) {
                errorMessage.text = "E-mail não cadastrado"
                errorMessage.visibility = TextView.VISIBLE
                return@setOnClickListener
            }

            if (password != validPassword) {
                errorMessage.text = "Senha inválida"
                errorMessage.visibility = TextView.VISIBLE
                return@setOnClickListener
            }

            val intent = Intent(this, LoginSuccessActivity::class.java)
            startActivity(intent)
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
