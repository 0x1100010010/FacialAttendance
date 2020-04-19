package com.shahzadakhtar.attendancecam.Model;

public class Parent {

    String parentId;
    String parentName;
    String parentEmail;
    Student[] students;

    public Parent() {
    }

    public Parent(String parentId, String parentName, String parentEmail, Student[] students) {
        this.parentId = parentId;
        this.parentName = parentName;
        this.parentEmail = parentEmail;
        this.students = students;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    public Student[] getStudents() {
        return students;
    }

    public void setStudents(Student[] students) {
        this.students = students;
    }
}
