package com.mcdenny.farmerapp.Model;

public class Product {
    private String Name, Image, Description, Price, Discount, Menuid;
    public Product() {
    }

    public Product(String name, String image, String description, String price, String discount, String menuid) {
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        Discount = discount;
        Menuid = menuid;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getMenuid() {
        return Menuid;
    }

    public void setMenuid(String menuid) {
        Menuid = menuid;
    }
}
