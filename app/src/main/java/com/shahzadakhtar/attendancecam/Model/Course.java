package com.shahzadakhtar.attendancecam.Model;

public class Course {

    String courseId;
    String courseName;
    String courseCode;
    String className;
    String classId;
    int totalStudents;
    int presentStudents;
    int absentStudents;

    public Course(String courseId, String courseName, int totalStudents, int presentStudents, int absentStudents) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.totalStudents = totalStudents;
        this.presentStudents = presentStudents;
        this.absentStudents = absentStudents;
    }

    public Course() {
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public int getPresentStudents() {
        return presentStudents;
    }

    public void setPresentStudents(int presentStudents) {
        this.presentStudents = presentStudents;
    }

    public int getAbsentStudents() {
        return absentStudents;
    }

    public void setAbsentStudents(int absentStudents) {
        this.absentStudents = absentStudents;
    }
}
