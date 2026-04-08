package model;

public class Student {
    private int id;
    private String name;
    private int age;
    private String grade;
    private String subject;
    private double score;
    private String email;

    public Student(int id, String name, int age, String grade, String subject, double score, String email) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.grade = grade;
        this.subject = subject;
        this.score = score;
        this.email = email;
    }

    public Student(String name, int age, String grade, String subject, double score, String email) {
        this(-1, name, age, grade, subject, score, email);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGrade() { return grade; }
    public String getSubject() { return subject; }
    public double getScore() { return score; }
    public String getEmail() { return email; }
}

