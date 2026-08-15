package com.zilan.week1;

public class Student implements Gradable {
    private String name;
    private int age;
    private String course;

    public Student(String name, int age, String course) throws InvalidAgeException {
        this.name = name;
        setAge(age);
        this.course = course;
    }

    public String getName() { 
        return name; 
    }public int getAge() {
        return age;
    }public String getCourse() {
        return course;
    }

    public void setName(String name) { 
        this.name = name; 
    }

    public void setAge(int age) throws InvalidAgeException {
        if(age < 0){
            throw new InvalidAgeException(age + ", " + "age cannot be negative.");
        }
        this.age = age;
    }

    public void setCourse(String course) { 
        this.course = course; 
    }

    @Override
    public String computeStanding() {
        return age >= 18 ? "Adult Student" : "Minor Student";
    }
}