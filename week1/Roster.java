package com.zilan.week1;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Roster {
    private List<Student> students = new ArrayList<>();

    public List<Student> getStudents() {
        return students;
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void printAdultReport() {
        var adults = students.stream()
                .filter(s -> s.getAge() >= 18)
                .map(Student::getName)
                .collect(Collectors.toList());

        System.out.println("Adult students: " + adults);
        System.out.println("Count: " + adults.size());
    }
}