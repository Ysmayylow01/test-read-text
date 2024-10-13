package tm.read.text.test.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import tm.read.text.test.R
import tm.read.text.test.databinding.FragmentHomeBinding
import tm.read.text.test.db.DatabaseHelper
import tm.read.text.test.db.Student


class HomeFragment : Fragment(R.layout.fragment_home) {
    private var TAG: String = "HomeFragment"
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var b: FragmentHomeBinding
    private val db = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        b = FragmentHomeBinding.inflate(inflater)
        databaseHelper = DatabaseHelper(requireContext())
        initListeners()
        return b.root
    }

    private fun initListeners() {
        b.addStudent.setOnClickListener {
            b.progressBar.visibility = View.VISIBLE
            b.addStudent.text = ""
            val name = b.tvName.text.toString()
            val surname = b.tvSurname.text.toString()
            val group = b.tvGroup.text.toString()

            if (name.isNotEmpty() && surname.isNotEmpty() && group.isNotEmpty()) {
                uploadNotification(name,surname, group)
//                val id = databaseHelper.insertStudent(name, surname, group)
//                if (id > 0) {
//                    Toast.makeText(
//                        requireContext(),
//                        "Üstünlikli goşuldy!",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    b.tvName.text?.clear()
//                    b.tvSurname.text?.clear()
//                    b.tvGroup.text?.clear()
//                } else {
//                    Toast.makeText(requireContext(), "Ulanyjy goşulmady", Toast.LENGTH_SHORT)
//                        .show()
//                }
            } else {
                Toast.makeText(requireContext(), "Doly giriziň", Toast.LENGTH_SHORT)
                    .show()
            }
        }


        b.allStudents.setOnClickListener {
            val detailFragment = FragmentAllStudents()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, detailFragment)
                .addToBackStack(null)
                .commit()
        }

    }
    private fun uploadNotification(name: String, surname: String, group: String) {
        val uuid = db.collection("users").document().id
        val student = Student().apply {
            this.id = uuid
            this.name = name
            this.surname = surname
            this.group = group
        }

        db.collection("users").document(uuid)
            .set(student)
            .addOnCompleteListener { task ->
                b.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    b.addStudent.text = "Goşmak"
                    Toast.makeText(requireContext(), "Goyuldy", Toast.LENGTH_SHORT).show()
                    b.tvName.text?.clear()
                    b.tvSurname.text?.clear()
                    b.tvGroup.text?.clear()
                } else {
                    b.addStudent.text = "Goşmak"
                    Toast.makeText(requireContext(), "Ýalňyşlyk ýüze çykdy", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Error: ${task.exception?.message}")
                }
            }
            .addOnFailureListener { e ->
                b.progressBar.visibility = View.GONE
                b.addStudent.text = "Goşmak"
                Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Firestore failure: ${e.message}")
            }
    }
}

