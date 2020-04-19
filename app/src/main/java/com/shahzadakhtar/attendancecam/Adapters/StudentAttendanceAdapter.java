package com.shahzadakhtar.attendancecam.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shahzadakhtar.attendancecam.Model.Attendance;
import com.shahzadakhtar.attendancecam.R;

import java.util.ArrayList;

public class StudentAttendanceAdapter extends RecyclerView.Adapter<StudentAttendanceAdapter.AttendanceViewHolder> {

    Context context;
    ArrayList<Attendance> attendances;

    public StudentAttendanceAdapter(Context context, ArrayList<Attendance> attendances) {
        this.context = context;
        this.attendances = attendances;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.view_attendance_lay, parent, false);

        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {

        if(position %3 == 0){
            holder.tvAttendanceStatus.setText("Absent");
            holder.tvAttendanceStatus.setBackgroundResource(R.drawable.red_bg);

        }else{
            holder.tvAttendanceStatus.setText("Present");
            holder.tvAttendanceStatus.setBackgroundResource(R.drawable.green_bg);
        }

        holder.tvStudentName.setText("Student " + (position++));

    }

    @Override
    public int getItemCount() {
        return 15;
    }

    public class AttendanceViewHolder extends RecyclerView.ViewHolder{

        TextView tvStudentName, tvAttendanceStatus;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);

            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvAttendanceStatus = itemView.findViewById(R.id.tvAttendanceStatus);

        }
    }
}
