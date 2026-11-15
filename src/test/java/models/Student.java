package models;

import java.time.LocalDate;

public class Student {
    private final String firstName, lastName, email, gender, mobile;
    private final LocalDate dateOfBirth;
    private final String subject, hobby, picture, address, state, city;
    private final String subjects, hobbies;

    public Student(String firstName, String lastName, String email, String gender, String mobile,
            LocalDate dateOfBirth, String subject, String hobby, String picture, String address,
            String state, String city, String subjects, String hobbies) {
        this.firstName = firstName; this.lastName = lastName; this.email = email; this.gender = gender;
        this.mobile = mobile; this.dateOfBirth = dateOfBirth;
        this.subject = subject; this.hobby = hobby;
        this.picture = picture; this.address = address; this.state = state; this.city = city;
        this.subjects = subjects; this.hobbies = hobbies;
    }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getGender() { return gender; }
    public String getMobile() { return mobile; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getPicture() { return picture; }
    public String getAddress() { return address; }
    public String getState() { return state; }
    public String getCity() { return city; }
    public String getSubjects() { return subjects; }
    public String getHobbies() { return hobbies; }
}
