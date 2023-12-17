package com.example.botv2;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Place {
    String location,town,address,oblast,name;
    @Id
    private Long id;

    public Place() {
    }

    public Place(String location, String town, String address, String oblast, String name, Long id) {
        this.location = location;
        this.town = town;
        this.address = address;
        this.oblast = oblast;
        this.name = name;
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOblast() {
        return oblast;
    }

    public void setOblast(String oblast) {
        this.oblast = oblast;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
