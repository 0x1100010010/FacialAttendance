package com.shahzadakhtar.attendancecam.Model;

public class Teacher {

    String teacherId;
    String teacherName;
    String teacherEmail;
    int birthDay;
    int birthMonth;
    int birthYear;
    String gender;
    String phoneNo;
    String degree;
    String salary;
    String address;

    public Teacher() {
    }


    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }

}
