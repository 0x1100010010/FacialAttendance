package com.shahzadakhtar.attendancecam.Model;

public class Class {

    String classId;
    String department;
    String className;
    String courseName;
    String classSession;
    String classSemester;
    String classHour;
    String classMinute;
    String teacherId;
    String roomNo;

    public Class() {
    }

    public Class(String classId, String department, String className, String courseName, String classSession, String classSemester, String classHour, String classMinute, String teacherId, String roomNo) {
        this.classId = classId;
        this.department = department;
        this.className = className;
        this.courseName = courseName;
        this.classSession = classSession;
        this.classSemester = classSemester;
        this.classHour = classHour;
        this.classMinute = classMinute;
        this.teacherId = teacherId;
        this.roomNo = roomNo;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getClassSession() {
        return classSession;
    }

    public void setClassSession(String classSession) {
        this.classSession = classSession;
    }

    public String getClassSemester() {
        return classSemester;
    }

    public void setClassSemester(String classSemester) {
        this.classSemester = classSemester;
    }

    public String getClassHour() {
        return classHour;
    }

    public void setClassHour(String classHour) {
        this.classHour = classHour;
    }

    public String getClassMinute() {
        return classMinute;
    }

    public void setClassMinute(String classMinute) {
        this.classMinute = classMinute;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
