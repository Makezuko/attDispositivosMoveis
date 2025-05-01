package com.example.login_simulation

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RecoveryActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovery)

        auth = FirebaseAuth.getInstance()

        val emailEditText = findViewById<EditText>(R.id.recoveryEmail)
        val sendEmailButton = findViewById<Button>(R.id.sendRecoveryEmailButton)
        val messageText = findViewById<TextView>(R.id.recoveryMessage)

        sendEmailButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Digite seu e-mail", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val result = task.result
                        if (result.signInMethods?.isEmpty() == true) {
                            Toast.makeText(this, "Este e-mail não está cadastrado.", Toast.LENGTH_LONG).show()
                        } else {
                            auth.sendPasswordResetEmail(email)
                                .addOnCompleteListener { resetTask ->
                                    if (resetTask.isSuccessful) {
                                        Toast.makeText(this, "E-mail de recuperação enviado para $email", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(this, "Erro ao enviar e-mail: ${resetTask.exception?.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                        }
                    } else {
                        Toast.makeText(this, "Erro ao verificar o e-mail: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        val goToLogin = findViewById<TextView>(R.id.goToLogin)
        goToLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
