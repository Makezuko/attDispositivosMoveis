package com.example.login_simulation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginSuccessActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_success)

        auth = FirebaseAuth.getInstance()

        val tvUserEmail = findViewById<TextView>(R.id.tvUserEmail)
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val kanbanButton = findViewById<Button>(R.id.kanbanButton)

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            tvUserEmail.text = "${user.email}"
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Sessão encerrada com sucesso", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        kanbanButton.setOnClickListener {
            val intent = Intent(this, KanbanActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }
}
