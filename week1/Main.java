package com.zilan.week1;

public class Main {
    public static void main(String[] args) {
        Roster roster = new Roster();

        roster.addStudent(new Student("Alice", 20, "Computer Science"));
        roster.addStudent(new Student("John", 17, "IT"));
        roster.addStudent(new Student("Charlie", 22, "Nursing"));
        roster.addStudent(new Student("Declan", 19, "Pharmacy"));
        roster.addStudent(new Student("Ethan", 16, "Business Admin"));
        roster.addStudent(new Student("Paschal", 21, "Computer Engineering"));

        for (Student s : roster.getStudents()) {
            System.out.println(s.getName() + ", " + s.getAge() + ", " + s.getCourse());
        }

        try{
        roster.addStudent(new Student("Christian", -17, "Physical Therapy"));
        }catch(InvalidAgeException e) {
            System.out.print(e.getMessage());
        }
        
        System.out.println();
        roster.printAdultReport();
    }
}