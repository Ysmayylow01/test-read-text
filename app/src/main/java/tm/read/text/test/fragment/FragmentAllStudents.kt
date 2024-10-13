package tm.read.text.test.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import tm.read.text.test.OnItemPressed
import tm.read.text.test.R
import tm.read.text.test.StudentsAdapter
import tm.read.text.test.databinding.FragmentAllStudentsBinding
import tm.read.text.test.db.DatabaseHelper
import tm.read.text.test.db.Student

class FragmentAllStudents : Fragment(R.layout.fragment_all_students), OnItemPressed {
    private lateinit var b: FragmentAllStudentsBinding
    private lateinit var adapterStudent: StudentsAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private val TAG: String = "FragmentAllStudent"
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = DatabaseHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        b = FragmentAllStudentsBinding.inflate(inflater)

        setRecycler()
        setData()
        initListeners()
        return b.root
    }

    private fun initListeners() {
        b.back.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setData() {
        b.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val studentList = mutableListOf<Student>()
                val snapshot = db.collection("users").get().await()

                for (document in snapshot.documents) {
                    val student = document.toObject(Student::class.java)
                    if (student != null) {
                        studentList.add(student)
                    }
                }

                withContext(Dispatchers.Main) {
                    adapterStudent.submitList(studentList)

                    b.progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Error fetching data: ${e.message}", e)

                    b.progressBar.visibility = View.GONE
                }
            }
        }
    }


    private fun setRecycler() {
        adapterStudent = StudentsAdapter(this)
        b.list.layoutManager = LinearLayoutManager(requireContext())
        b.list.adapter = adapterStudent


    }

    override fun onPressed(item: Student) {
        val detailFragment = FragmentReadTextFile()
        val bundle = Bundle()
        bundle.putString("name", item.name)
        bundle.putString("surname", item.surname)
        bundle.putString("group", item.group)
        detailFragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, detailFragment)
            .addToBackStack(null)
            .commit()
    }

}