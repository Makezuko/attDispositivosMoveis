
package com.example.login_simulation

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TaskDetailActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvCreatedBy: TextView
    private lateinit var tvModifiedBy: TextView
    private lateinit var rvSubtasks: RecyclerView
    private lateinit var btnDelete: Button
    private lateinit var btnBack: Button
    private lateinit var btnAddSubtask: Button

    private var taskId: String? = null
    private var parentId: String? = null
    private var status: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        taskId = intent.getStringExtra("taskId")
        parentId = intent.getStringExtra("parentId")
        status = intent.getStringExtra("status")

        tvTitle = findViewById(R.id.tvTitle)
        tvDescription = findViewById(R.id.tvDescription)
        tvCreatedBy = findViewById(R.id.tvCreatedBy)
        tvModifiedBy = findViewById(R.id.tvModifiedBy)
        rvSubtasks = findViewById(R.id.rvSubtasks)
        btnDelete = findViewById(R.id.btnDelete)
        btnBack = findViewById(R.id.btnBack)
        btnAddSubtask = findViewById(R.id.btnAddSubtask)

        tvTitle.setOnClickListener {
            val input = EditText(this)
            val tituloAntigo = tvTitle.text.toString()
            input.setText(tituloAntigo)

            AlertDialog.Builder(this)
                .setTitle("Editar Título")
                .setView(input)
                .setPositiveButton("Salvar") { _, _ ->
                    val novoTitulo = input.text.toString()
                    val user = auth.currentUser?.email ?: "usuário_desconhecido"

                    db.collection("kanban").document(taskId!!)
                        .update("title", novoTitulo, "modifiedBy", user)
                        .addOnSuccessListener {
                            tvTitle.text = novoTitulo
                            tvModifiedBy.text = "Modificado por: $user"
                            Toast.makeText(this, "Título atualizado", Toast.LENGTH_SHORT).show()
                            logAction("[${status?.uppercase() ?: "SEM STATUS"}] Título alterado de '$tituloAntigo' para '$novoTitulo' por $user")
                        }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        tvDescription.setOnClickListener {
            val input = EditText(this)
            val descricaoAntiga = tvDescription.text.toString()
            input.setText(descricaoAntiga)

            AlertDialog.Builder(this)
                .setTitle("Editar Descrição")
                .setView(input)
                .setPositiveButton("Salvar") { _, _ ->
                    val novaDescricao = input.text.toString()
                    val user = auth.currentUser?.email ?: "usuário_desconhecido"

                    db.collection("kanban").document(taskId!!)
                        .update("description", novaDescricao, "modifiedBy", user)
                        .addOnSuccessListener {
                            tvDescription.text = novaDescricao
                            tvModifiedBy.text = "Modificado por: $user"
                            Toast.makeText(this, "Descrição atualizada", Toast.LENGTH_SHORT).show()

                            val tipo = if (parentId != null) "Subtarefa" else "Tarefa"
                            logAction("[${status?.uppercase() ?: "SEM STATUS"}] $tipo '${tvTitle.text}' teve a descrição alterada de '$descricaoAntiga' para '$novaDescricao' por $user")
                        }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        btnBack.setOnClickListener {
            val intent = if (parentId != null) {
                Intent(this, TaskDetailActivity::class.java).apply {
                    putExtra("taskId", parentId)
                }
            } else {
                Intent(this, KanbanActivity::class.java)
            }
            startActivity(intent)
            finish()
        }

        btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Confirmar exclusão")
                .setMessage("Deseja realmente apagar esta tarefa e todas as suas subtarefas?")
                .setPositiveButton("Apagar") { _, _ -> deleteTaskWithSubtasks(taskId!!) }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        btnAddSubtask.setOnClickListener {
            val input = EditText(this)
            input.hint = "Título da nova subtarefa"

            val user = auth.currentUser?.email ?: "usuário_desconhecido"
            AlertDialog.Builder(this)
                .setTitle("Nova Subtarefa")
                .setView(input)
                .setPositiveButton("Adicionar") { _, _ ->
                    val subtarefaTitulo = input.text.toString()
                    val task = Task(
                        title = subtarefaTitulo,
                        status = status ?: "to-do",
                        createdBy = user,
                        modifiedBy = user,
                        parentId = taskId
                    )
                    db.collection("kanban").add(task).addOnSuccessListener {
                        logAction("[${status?.uppercase() ?: "SEM STATUS"}] Subtarefa '$subtarefaTitulo' foi adicionada à tarefa '${tvTitle.text}' por $user")
                        Toast.makeText(this, "Subtarefa adicionada", Toast.LENGTH_SHORT).show()
                        loadSubtasks()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        loadTask()
        loadSubtasks()
    }

    override fun onResume() {
        super.onResume()
        loadTask()
        loadSubtasks()
    }

    private fun loadTask() {
        db.collection("kanban").document(taskId!!).get().addOnSuccessListener { doc ->
            val task = doc.toObject(Task::class.java)
            if (task != null) {
                tvTitle.text = task.title
                tvDescription.text = task.description ?: ""
                tvCreatedBy.text = "Criado por: ${task.createdBy ?: "-"}"
                tvModifiedBy.text = "Modificado por: ${task.modifiedBy ?: "-"}"
                parentId = task.parentId
            }
        }
    }

    private fun loadSubtasks() {
        rvSubtasks.layoutManager = LinearLayoutManager(this)
        db.collection("kanban")
            .whereEqualTo("parentId", taskId)
            .get()
            .addOnSuccessListener { result ->
                val subtasks = result.mapNotNull { it.toObject(Task::class.java).apply { id = it.id } }
                rvSubtasks.adapter = TaskAdapter(subtasks) { subtask ->
                    val intent = Intent(this, TaskDetailActivity::class.java)
                    intent.putExtra("taskId", subtask.id)
                    intent.putExtra("parentId", subtask.parentId)
                    intent.putExtra("status", subtask.status)
                    startActivity(intent)
                    finish()
                }
            }
    }

    private fun deleteTaskWithSubtasks(id: String) {
        val user = auth.currentUser?.email ?: "usuário_desconhecido"

        db.collection("kanban")
            .whereEqualTo("parentId", id)
            .get()
            .addOnSuccessListener { result ->
                val batch = db.batch()

                result.forEach { subtaskDoc ->
                    batch.delete(subtaskDoc.reference)
                }

                db.collection("kanban").document(id).get().addOnSuccessListener { doc ->
                    val task = doc.toObject(Task::class.java)
                    doc?.reference?.let { batch.delete(it) }

                    batch.commit().addOnSuccessListener {
                        if (task != null) {
                            logAction("[${status?.uppercase() ?: "SEM STATUS"}] Tarefa '${task.title}' e suas subtarefas foram removidas por $user")
                        }
                        Toast.makeText(this, "Tarefa e subtarefas apagadas.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, KanbanActivity::class.java))
                        finish()
                    }
                }
            }
    }

    private fun logAction(message: String) {
        val logData = hashMapOf(
            "message" to message,
            "timestamp" to com.google.firebase.Timestamp.now()
        )
        db.collection("logs").add(logData)
    }
}
