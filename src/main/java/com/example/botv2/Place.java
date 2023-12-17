package com.example.botv2;

import jakarta.persistence.*;

@Entity
@Table(name = "LocationList")
public class Place {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    String monument, town, oblast, address, autor;



    public Place(String monument, String town, String oblast, String address, String name) {
    }

    public Place() {

    }

    public String getMonument() {
        return monument;
    }

    public void setMonument(String monument) {
        this.monument = monument;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getOblast() {
        return oblast;
    }

    public void setOblast(String oblast) {
        this.oblast = oblast;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAutor(String name) {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
