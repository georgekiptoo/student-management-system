package model;

// Represents a single student record
public class Student {

    private int    id;
    private String name;
    private int    age;
    private String grade;
    private String subject;
    private double score;
    private String email;

    // default constructor
    public Student() {}

    // constructor with all fields
    public Student(int id, String name, int age, String grade,
                   String subject, double score, String email) {
        this.id      = id;
        this.name    = name;
        this.age     = age;
        this.grade   = grade;
        this.subject = subject;
        this.score   = score;
        this.email   = email;
    }

    // getters
    public int    getId()      { return id; }
    public String getName()    { return name; }
    public int    getAge()     { return age; }
    public String getGrade()   { return grade; }
    public String getSubject() { return subject; }
    public double getScore()   { return score; }
    public String getEmail()   { return email; }

    // setters
    public void setId     (int id)       { this.id      = id; }
    public void setName   (String name)  { this.name    = name; }
    public void setAge    (int age)      { this.age     = age; }
    public void setGrade  (String grade) { this.grade   = grade; }
    public void setSubject(String subj)  { this.subject = subj; }
    public void setScore  (double score) { this.score   = score; }
    public void setEmail  (String email) { this.email   = email; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name=" + name + ", grade=" + grade + "}";
    }
}
