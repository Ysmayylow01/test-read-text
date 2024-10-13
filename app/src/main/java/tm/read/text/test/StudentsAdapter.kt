package tm.read.text.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tm.read.text.test.db.Student
import tm.read.text.test.databinding.ItemStudentsBinding

 class StudentsAdapter(var onPressed: OnItemPressed?) : ListAdapter<Student, StudentsAdapter.StudentViewHolder>(DiffCallback()) {

    class StudentViewHolder(private val binding: ItemStudentsBinding, private val onPressed: OnItemPressed?) : RecyclerView.ViewHolder(binding.root) {

        fun bind(student: Student, position: Int, itemCount: Int) {
            binding.name.text = student.name +"  "+ student.surname
            binding.group.text = student.group ?: "Unknown Group"
            binding.root.setOnClickListener {
               onPressed?.onPressed(student)
            }
            if (position == itemCount-1){
                binding.viewLine.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
         onPressed = onPressed
        return StudentViewHolder(binding, onPressed)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = getItem(position)
        holder.bind(student, position, itemCount)
    }

    class DiffCallback : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem == newItem
        }
    }
}
