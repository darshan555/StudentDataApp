package com.example.student_data_app

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudentDataAdapter(var context: Context, val studentDataList: List<StudentData>):RecyclerView.Adapter<DataViewHolder>() {
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
        holder.usernameTextview.setText(student.username)

        holder.itemView.setOnClickListener{
            val intent = Intent(context, StudentDetail::class.java)
            intent.putExtra("uid",student.userId)
            context.startActivity(intent)
        }
    }


}
class DataViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    val usernameTextview: TextView = itemView.findViewById(R.id.admUsernameTV)

}