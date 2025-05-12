package com.example.login_simulation

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        if (savedInstanceState == null) {
            auth.signOut()
        }

        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val errorMessage = findViewById<TextView>(R.id.errorMessage)
        val showPasswordButton = findViewById<ImageButton>(R.id.showPasswordButton)

        var isPasswordVisible = false

        showPasswordButton.setOnClickListener {
            if (isPasswordVisible) {
                passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                showPasswordButton.setImageResource(R.drawable.ic_eye_closed)
            } else {
                passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                showPasswordButton.setImageResource(R.drawable.ic_eye_open)
            }
            passwordEditText.setSelection(passwordEditText.text.length)
            isPasswordVisible = !isPasswordVisible
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            errorMessage.visibility = TextView.GONE

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, LoginSuccessActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        errorMessage.text = "Email ou senha inválidos"
                        errorMessage.visibility = TextView.VISIBLE
                    }
                }
        }

        val goToRegister = findViewById<TextView>(R.id.goToRegister)
        goToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val goToRecovery = findViewById<TextView>(R.id.goToRecovery)
        goToRecovery.setOnClickListener{
            val intent = Intent(this, RecoveryActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}