package com.sw.mobsale.online.bean;


public class Product {
    String name;
    String info;
    String number;
    String  price;
    String image;
    public Product(){

    }
    public Product(String name, String info ,String number, String price,String image) {
        this.name = name;
        this.info = info;
        this.number = number;
        this.price = price;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name +
                ", info='" + info +
                ", number=" + number +
                ", price=" + price +
                ", image ="+ image + "}";
    }

}
