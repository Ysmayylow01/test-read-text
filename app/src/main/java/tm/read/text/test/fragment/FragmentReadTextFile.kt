package tm.read.text.test.fragment

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import tm.read.text.test.R
import tm.read.text.test.databinding.FragmentReadTextFileBinding
import tm.read.text.test.utils.SharedPreference
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader

class FragmentReadTextFile : Fragment(R.layout.fragment_read_text_file) {
    private val TAG: String = "FragmentReadTextFile"
    private lateinit var lines: List<String>
    private var currentLineIndex = 0
    private lateinit var b: FragmentReadTextFileBinding
    private lateinit var name : String
    private lateinit var surname : String
    private lateinit var group : String
    private lateinit var firebaseStorage : FirebaseStorage
    private lateinit var storageRef : StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        b = FragmentReadTextFileBinding.inflate(inflater)
        firebaseStorage = FirebaseStorage.getInstance()
        storageRef = FirebaseStorage.getInstance().reference
        val args = arguments
         name  = args?.getString("name")!!
         surname = args.getString("surname")!!
         group = args.getString("group")!!
        readText()
        initListeners()
        return b.root
    }

    private fun initListeners() {
        b.clickRight.setOnClickListener {
            if (currentLineIndex < lines.size - 1) {
                currentLineIndex++
                b.readText.text = lines[currentLineIndex]
                b.lineText.text = "Setir sany : $currentLineIndex"

            }
        }
        b.clickLeft.setOnClickListener {
            if (currentLineIndex > 0) {
                currentLineIndex--
                b.readText.text = lines[currentLineIndex]
                b.lineText.text = "Setir sany : $currentLineIndex"

            }
        }
        b.clickRead.setOnClickListener {
         SharedPreference.setLineIndex(currentLineIndex)
            uploadTxtToFirebase()
        }

    }

    private fun readText() {
        b.userName.text = "ady : $name"
        currentLineIndex = SharedPreference.getLineIndex()
        if (currentLineIndex!=0){
            currentLineIndex = currentLineIndex+1
        }
        val reader = BufferedReader(InputStreamReader(activity?.assets?.open("text.txt")))
        lines = reader.readLines()
        if (lines.isNotEmpty()) {
            b.readText.text = lines[currentLineIndex]
            b.lineText.text = "Setir sany : $currentLineIndex"
        }

    }
        private fun uploadTxtToFirebase() {
            val fileName = "$name $surname _ $currentLineIndex.txt"
        val tempFile = File.createTempFile("plany_1", ".txt")
        FileOutputStream(tempFile).use { outputStream ->
            val currentLine = lines[currentLineIndex]
            outputStream.write("$currentLine\n".toByteArray())
        }

        val fileUri = Uri.fromFile(tempFile)
        val fileRef = storageRef.child("texts/${fileName}")
        fileRef.putFile(fileUri)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "yuklendi", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "yuklenmedi", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "uploadTxtToFirebase: $exception", )
            }
    }
}