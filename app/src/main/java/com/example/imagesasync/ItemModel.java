package com.example.imagesasync;

public class ItemModel {
    private String Image;
    private String name, age, city;

    public ItemModel(String name, String age, String city, String Image){
        this.Image = Image;
        this.name = name;
        this.age = age;
        this.city = city;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
