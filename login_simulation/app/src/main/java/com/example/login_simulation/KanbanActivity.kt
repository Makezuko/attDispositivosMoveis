package com.example.login_simulation

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login_simulation.databinding.ActivityKanbanBinding
import com.google.firebase.firestore.FirebaseFirestore

class KanbanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKanbanBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKanbanBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                val tasks = mutableListOf<Task>()

                for (doc in result.documents) {
                    try {
                        val task = doc.toObject(Task::class.java)
                        if (task != null) {
                            tasks.add(task)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this, "Erro ao converter: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                if (tasks.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    emptyTextView.visibility = View.VISIBLE
                } else {
                    recyclerView.layoutManager = LinearLayoutManager(this)
                    recyclerView.adapter = TaskAdapter(tasks)
                    recyclerView.visibility = View.VISIBLE
                    emptyTextView.visibility = View.GONE
                }
            }
    }
}

