
package com.example.myapplication;

public class Goods {
    private String id;
    private String foodname;
    private double heats;
    private double fat;
    private double protein;
    private double Carbohydrates;
    private double Ca;
    private double Fe;


    public Goods() {
        super();
    }

    public Goods(String id, String foodname, double heats, double fat, double protein,
                 double Carbohydrates, double Ca, double Fe) {
        super();
        this.id = id;
        this.foodname = foodname;
        this.heats = heats;
        this.fat = fat;
        this.protein = protein;
        this.Carbohydrates = Carbohydrates;
        this.Ca = Ca;
        this.Fe = Fe;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFoodName() {
        return foodname;
    }

    public void setFoodName(String foodname) {
        this.foodname = foodname;
    }

    public double getHeats() {
        return heats;
    }

    public void setHeats(float heats) {
        this.heats = heats;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public double getCarbohydrates() {
        return Carbohydrates;
    }

    public void setCarbohydrates(float Carbohydrates) {
        this.Carbohydrates = Carbohydrates;
    }

    public double getCa() { return Ca; }

    public void setCa(float Ca) {
        this.Ca = Ca;
    }

    public double getFe() {
        return Fe;
    }

    public void setFe(float Fe) {
        this.protein = Fe;
    }



}