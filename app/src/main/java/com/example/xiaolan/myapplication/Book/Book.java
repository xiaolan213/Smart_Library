package com.example.xiaolan.myapplication.Book;

import java.io.Serializable;

public class Book implements Serializable {
    private String book_name;
    private String author;
    private int status1;
    private String borrower;
    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getBorrower() {
        return borrower;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    public Book() {
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getStatus1() {
        return status1;
    }

    public void setStatus1(int status1) {
        this.status1 = status1;
    }

    @Override
    public String toString() {
        return "Book{" +
                "book_name='" + book_name + '\'' +
                ", author='" + author + '\'' +
                ", status=" + status1 +
                ", borrower='" + borrower + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
