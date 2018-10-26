package com.example.khokan.tutorisbdservice;

/**
 * Created by USER on 9/22/2018.
 */

public class Contacts {
    public String name, status, image,private_tutors,gender,profession;

    public String getPrivate_tutors() {
        return private_tutors;
    }

    public void setPrivate_tutors(String private_tutors) {
        this.private_tutors = private_tutors;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public Contacts()
    {


    }
    public Contacts(String name, String status, String image) {
        this.name = name;
        this.status = status;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
