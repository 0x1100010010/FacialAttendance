package com.shahzadakhtar.attendancecam.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shahzadakhtar.attendancecam.MainActivity;
import com.shahzadakhtar.attendancecam.Model.Attendance;
import com.shahzadakhtar.attendancecam.Model.Student;
import com.shahzadakhtar.attendancecam.MyPrefs;
import com.shahzadakhtar.attendancecam.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class StudentActivity extends AppCompatActivity {


    @BindView(R.id.recyclerAttendance)
    RecyclerView recyclerAttendance;

    Query databaseReference;
    CommentAdapter adapter;

    Query query;
    FirebaseAuth auth;

    MyPrefs myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        ButterKnife.bind(this);

        myPrefs = new MyPrefs(this);

        recyclerAttendance.setHasFixedSize(true);
        recyclerAttendance.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();


    }

   /* @OnClick(R.id.btnSearch)
    void btnSearchOnClick(View view){

        if (TextUtils.isEmpty(etRollNo.getText().toString())) {
            etRollNo.setError("Enter roll no");
            return;
        }

        adapter = new CommentAdapter(this, databaseReference);
        recyclerAttendance.setAdapter(adapter);
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Attendance").child("authId").equalTo(""+auth.getUid());

    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            adapter.cleanupListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private static class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView tvStudentName, tvAttendanceStatus, tvDate;
        CircleImageView profile_image;


        public CommentViewHolder(View itemView) {
            super(itemView);


            profile_image = itemView.findViewById(R.id.profile_image);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvAttendanceStatus = itemView.findViewById(R.id.tvAttendanceStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            profile_image.setVisibility(View.GONE);

          /*  tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvBusinessType = itemView.findViewById(R.id.tvBusinessType);
            tvBusinessDesc = itemView.findViewById(R.id.tvBusinessDesc);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDate = itemView.findViewById(R.id.tvDate);*/

        }
    }

    private static class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

        ProgressDialog progressDialog;
        private Context mContext;
        private Query mDatabaseReference;
        private ChildEventListener mChildEventListener;
        private List<String> mCommentIds = new ArrayList<>();
        private List<Attendance> mComments = new ArrayList<>();

        public CommentAdapter(final Context context, Query ref) {
            mContext = context;
            mDatabaseReference = ref;

            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading");
            progressDialog.show();
            // Create child event listener
            // [START child_event_listener_recycler]
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    // Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                    progressDialog.dismiss();

                    // A new comment has been added, add it to the displayed list
                    Attendance comment = dataSnapshot.getValue(Attendance.class);

                    // [START_EXCLUDE]
                    // Update RecyclerView
                    mCommentIds.add(dataSnapshot.getKey());
                    mComments.add(comment);
                    notifyItemInserted(mComments.size() - 1);
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    // Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                 /*   BusinessRequests newComment = dataSnapshot.getValue(BusinessRequests.class);
                    String commentKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Replace with the new data
                        mComments.set(commentIndex, newComment);

                        // Update the RecyclerView
                        notifyItemChanged(commentIndex);
                    } else {
                        Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
                    }
                    // [END_EXCLUDE]*/
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    // Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    String commentKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Remove data from the list
                        mCommentIds.remove(commentIndex);
                        mComments.remove(commentIndex);

                        // Update the RecyclerView
                        notifyItemRemoved(commentIndex);
                    } else {
                        // Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    // Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    // Comment movedComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                    Toast.makeText(mContext, "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(childEventListener);
            // [END child_event_listener_recycler]

            // Store reference to listener so it can be removed on app stop
            mChildEventListener = childEventListener;
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.view_attendance_lay, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, int position) {
            Attendance comment = mComments.get(position);
           /* holder.tvName.setText(comment.getFullName());
            if (comment.getPhone() != null) {
                if (!comment.getPhone().equals("")) {

                    holder.tvEmail.setText(comment.getEmail() + "\n" + comment.getPhone());
                }
            } else {

                holder.tvEmail.setText(comment.getEmail());
            }
            holder.tvBusinessDesc.setText(comment.getBusinessDescription());
            holder.tvBusinessType.setText(comment.getBusinessType());
            holder.tvDate.setText(comment.getDay() + "/" + comment.getMonth() + "/" + comment.getYear());
            holder.tvTime.setText(comment.getHour() + ":" + comment.getMinute());*/

            if(comment.getAttendanceStatus().toLowerCase().equals("absent")){
                holder.tvAttendanceStatus.setText("Absent");
                holder.tvAttendanceStatus.setBackgroundResource(R.drawable.red_bg);

            }else if(comment.getAttendanceStatus().toLowerCase().equals("Present")){
                holder.tvAttendanceStatus.setText("Present");
                holder.tvAttendanceStatus.setBackgroundResource(R.drawable.green_bg);
            }

            holder.tvStudentName.setText("" + comment.getStudentName());
            holder.tvDate.setText(comment.getDay()+"/"+comment.getMonth()+"/"+comment.getYear());

        }

        @Override
        public int getItemCount() {
            return mComments.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.top_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuLogout:
                myPrefs.setType("");
                finish();
                auth.signOut();
                startActivity(new Intent(StudentActivity.this, StartingActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}
