package com.example.emad.bookapp;

/**
 * Created by EMAD on 12/2/2017.
 */

public class result_object {
    String name;
    String descreption;
    String link;
    String author;

    public result_object(String name, String author, String descreption, String link) {
        this.author = author;
        this.descreption = descreption;
        this.link = link;
        this.name = name;
    }

    public String getname() {
        return name;
    }

    public String getlink() {
        return link;
    }

    public String getauthor() {
        return author;
    }

    public String getdescreption() {
        return descreption;
    }


}
