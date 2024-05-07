package com.example.roomdatabase

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdatabase.RecycleView.ShowData
import com.example.roomdatabase.RecycleView.ViewAdapter
import com.example.roomdatabase.databinding.ActivityMainBinding
import com.example.roomdatabase.databinding.DialogEditOrDeleteBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var studentDataBase: StudentDataBase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        studentDataBase = StudentDataBase.getDatabase(this)

        binding.saveBtn.isEnabled = false

        binding.fName.addTextChangedListener(textWatcher)
        binding.lName.addTextChangedListener(textWatcher)
        binding.rollNo.addTextChangedListener(textWatcher)

        binding.saveBtn.setOnClickListener {
            saveData()
            showData()
        }
        binding.showBtn.setOnClickListener {
            showData()
        }
        binding.deleteBtn.setOnClickListener {
            deleteAllData()
        }
    }

    private fun deleteAllData() {
        CoroutineScope(Dispatchers.IO).launch {
            val isEmpty = studentDataBase.studentDao().isEmpty()
            if (isEmpty) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "No data to delete", Toast.LENGTH_SHORT).show()
                }
            } else {
                studentDataBase.studentDao().deleteAll()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "All data deleted", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private val textWatcher = object : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val fNameText = binding.fName.text.toString().trim()
            val lNameText = binding.lName.text.toString().trim()
            val rollNoText = binding.rollNo.text.toString().trim()
            binding.saveBtn.isEnabled = fNameText.isNotEmpty() && lNameText.isNotEmpty() && rollNoText.isNotEmpty()
            if (binding.saveBtn.isEnabled) {
                binding.saveBtn.visibility = View.VISIBLE
                binding.showBtn.visibility = View.GONE
            } else {
                binding.saveBtn.visibility = View.GONE
                binding.showBtn.visibility = View.VISIBLE
            }
        }

        override fun afterTextChanged(s: android.text.Editable?) {}
    }

    private fun saveData() {
        val firstName = binding.fName.text.toString()
        val lastName = binding.lName.text.toString()
        val rollNum = binding.rollNo.text.toString()
        if (firstName.isNotBlank() && lastName.isNotBlank() && rollNum.isNotBlank()) {
            val student = StudentData(0, firstName, lastName, rollNum.toInt())
            CoroutineScope(Dispatchers.IO).launch {
                studentDataBase.studentDao().insertData(student)
            }
            binding.fName.text?.clear()
            binding.lName.text?.clear()
            binding.rollNo.text?.clear()
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Fill All Fields", Toast.LENGTH_SHORT).show()
        }
    }
    private fun showData() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        CoroutineScope(Dispatchers.IO).launch {
            val studentList = studentDataBase.studentDao().getAllData()
            val allShowData = studentList.map { student ->
                ShowData(student.id,student.fname, student.lname, student.roll)
            }
            withContext(Dispatchers.Main) {
                if (allShowData.isEmpty()) {
                    binding.noDataText.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                    Toast.makeText(this@MainActivity, "No data found", Toast.LENGTH_SHORT).show()
                } else {
                    binding.noDataText.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    val adapter = ViewAdapter(allShowData)
                    binding.recyclerView.adapter = adapter

                    adapter.onClick = {
//                        Toast.makeText(this@MainActivity, "${it.fname} ${it.lName}",Toast.LENGTH_SHORT).show()
                        showDialogBox(it.id,it.fname,it.lName,it.roll)
                    }
                }
            }
        }
    }
    private fun showDialogBox(idno: Int, fname: String, lname: String, roll: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_edit_or_delete)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val dialogBinding = DialogEditOrDeleteBinding.bind(dialog.findViewById(R.id.dialog_root))

        dialogBinding.editButton.setOnClickListener {
            // Show input fields for editing
            dialogBinding.constraintLayout.visibility = View.VISIBLE
            dialogBinding.saveButton.visibility = View.VISIBLE
            dialogBinding.editButton.visibility = View.GONE

            // Populate input fields with current values
            dialogBinding.editTextfName.setText(fname)
            dialogBinding.editTextlName.setText(lname)
            dialogBinding.editTextRoll.setText(roll.toString())

            dialogBinding.saveButton.setOnClickListener {
                val incomingFName = dialogBinding.editTextfName.text.toString()
                val incomingLName = dialogBinding.editTextlName.text.toString()
                val incomingRoll = dialogBinding.editTextRoll.text.toString()

                // Check if any field is empty
                if (incomingFName.isBlank() || incomingLName.isBlank() || incomingRoll.isBlank()) {
                    Toast.makeText(this@MainActivity, "Please fill all fields", Toast.LENGTH_SHORT).show()
                } else {
                    // Update data in the database
                    CoroutineScope(Dispatchers.IO).launch {
                        val student = StudentData(idno, incomingFName, incomingLName, incomingRoll.toInt())
                        studentDataBase.studentDao().updateData(student)
                        withContext(Dispatchers.Main) {
                            dialog.dismiss()
                            Toast.makeText(this@MainActivity, "Data Updated", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        dialogBinding.deleteButton.setOnClickListener {
            // Delete data from the database
            CoroutineScope(Dispatchers.IO).launch {
                studentDataBase.studentDao().deleteOne(idno)
                withContext(Dispatchers.Main) {
                    dialog.dismiss()
                    Toast.makeText(this@MainActivity, "Data Deleted", Toast.LENGTH_SHORT).show()
                    // Refresh UI with updated data
                    showData()
                }
            }
        }

        dialog.show()
    }

}
