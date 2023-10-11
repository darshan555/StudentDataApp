package com.example.student_data_app.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.student_data_app.Activity.StudentActivity
import com.example.student_data_app.Activity.StudentsDetail
import com.example.student_data_app.Model.StudentDataModel
import com.example.student_data_app.R

class StudentDataAdapter(var context: Context, val studentDataList: List<StudentDataModel>):RecyclerView.Adapter<DataViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.name_row_design, parent, false)
        return DataViewHolder(view)
    }

    override fun getItemCount(): Int {
        return studentDataList.size
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val student = studentDataList[position]
        holder.nameTextview.setText(student.name)

        holder.itemView.setOnClickListener{
            val intent = Intent(context, StudentsDetail::class.java)
            intent.putExtra("userId",student.userId)
            context.startActivity(intent)
        }
    }


}
class DataViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    val nameTextview: TextView = itemView.findViewById(R.id.admNameTV)

}