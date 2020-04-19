package com.shahzadakhtar.attendancecam.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shahzadakhtar.attendancecam.Model.Attendance;
import com.shahzadakhtar.attendancecam.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewAttendanceAdapter extends RecyclerView.Adapter<ViewAttendanceAdapter.AttendanceViewHolder> {

    Context context;
    ArrayList<Attendance> attendances;

    public ViewAttendanceAdapter(Context context, ArrayList<Attendance> attendances) {
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

        Attendance attendance = attendances.get(position);

        if (attendance.getAttendanceStatus().toLowerCase().equals("present")) {

            holder.tvAttendanceStatus.setText("P");
            holder.tvAttendanceStatus.setBackgroundResource(R.drawable.green_bg);
        } else {
            holder.tvAttendanceStatus.setText("A");
            holder.tvAttendanceStatus.setBackgroundResource(R.drawable.red_bg);
        }

        holder.tvDate.setText(attendance.getDay() + "/" + attendance.getMonth() + "/" + attendance.getYear());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child("Students").orderByChild("studentId").equalTo(attendance.getStudentId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"

                        Log.e("adapterData", issue.toString());
                        String name = issue.child("studentName").getValue(String.class);
                        String rollNo = issue.child("rollNo").getValue(String.class);
                        String imageUrl = issue.child("imageUrl").getValue(String.class);
                        String email = issue.child("email").getValue(String.class);
                        String parentName = issue.child("parentName").getValue(String.class);

                        if (!imageUrl.equals("null")) {

                            Picasso.get().load(imageUrl).into(holder.profile_image);
                        }

                        holder.tvStudentName.setText(name);
                        holder.tvRoll.setText(email + "\n" + "parent:" + parentName + "\nRoll#" + rollNo);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return attendances.size();
    }

    public class AttendanceViewHolder extends RecyclerView.ViewHolder {

        TextView tvStudentName, tvAttendanceStatus, tvDate, tvRoll;
        CircleImageView profile_image;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.profile_image);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvAttendanceStatus = itemView.findViewById(R.id.tvAttendanceStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvRoll = itemView.findViewById(R.id.tvRoll);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
    }
}
