package com.example.login_simulation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login_simulation.databinding.ActivityKanbanBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class KanbanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKanbanBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKanbanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVoltarLoginSuccess.setOnClickListener {
            startActivity(Intent(this, LoginSuccessActivity::class.java))
            finish()
        }

        binding.btnAddTodo.setOnClickListener { showCreateTaskDialog("to-do") }
        binding.btnAddDoing.setOnClickListener { showCreateTaskDialog("doing") }
        binding.btnAddPriority.setOnClickListener { showCreateTaskDialog("priority") }
        binding.btnAddMeeting.setOnClickListener { showCreateTaskDialog("meeting") }

        binding.btnLogs.setOnClickListener { showLogs() }
    }

    override fun onResume() {
        super.onResume()
        loadTasks("to-do", binding.rvTodo, binding.tvEmptyTodo)
        loadTasks("doing", binding.rvDoing, binding.tvEmptyDoing)
        loadTasks("priority", binding.rvPriority, binding.tvEmptyPriority)
        loadTasks("meeting", binding.rvMeeting, binding.tvEmptyMeeting)
    }

    private fun loadTasks(status: String, recyclerView: RecyclerView, emptyTextView: TextView) {
        db.collection("kanban")
            .whereEqualTo("status", status)
            .get()
            .addOnSuccessListener { result ->
                val tasks = result.mapNotNull { doc ->
                    doc.toObject(Task::class.java).apply { id = doc.id }
                        .takeIf { it.parentId == null }
                }

                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = TaskAdapter(tasks) { task ->
                    val intent = Intent(this, TaskDetailActivity::class.java)
                    intent.putExtra("taskId", task.id)
                    intent.putExtra("status", task.status)
                    startActivity(intent)
                }

                recyclerView.visibility = if (tasks.isEmpty()) View.GONE else View.VISIBLE
                emptyTextView.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
            }
    }

    private fun showCreateTaskDialog(status: String) {
        val input = EditText(this)
        input.hint = "Título da nova tarefa"

        val user = auth.currentUser?.email
        if (user.isNullOrEmpty()) {
            Toast.makeText(this, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Criar nova tarefa")
            .setView(input)
            .setPositiveButton("Criar") { _, _ ->
                val title = input.text.toString()
                val task = Task(
                    title = title,
                    status = status,
                    createdBy = user,
                    modifiedBy = user
                )
                db.collection("kanban").add(task).addOnSuccessListener {
                    logAction(
                        "[${status.uppercase()}] Tarefa '$title' criada por $user"
                    )
                    onResume()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showLogs() {
        db.collection("logs")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val logs = result.joinToString("\n\n") {
                    val msg = it.getString("message") ?: ""
                    val timestamp = it.getTimestamp("timestamp")
                    val time = timestamp?.toDate()?.toString()?.substring(11, 19) ?: ""
                    "[$time] $msg"
                }

                AlertDialog.Builder(this)
                    .setTitle("Logs de Ações")
                    .setMessage(if (logs.isNotBlank()) logs else "Sem logs disponíveis.")
                    .setPositiveButton("Fechar", null)
                    .show()
            }
    }

    private fun logAction(message: String) {
        val logData = hashMapOf(
            "message" to message,
            "timestamp" to Timestamp.now()
        )
        db.collection("logs").add(logData)
    }
}
