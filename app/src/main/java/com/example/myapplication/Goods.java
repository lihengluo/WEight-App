
package com.example.myapplication;

public class Goods {
    private String id;
    private String foodname;
    private float heats;
    private float fat;
    private float protein;
    private float Carbohydrates;
    private float Ca;
    private float Fe;


    public Goods() {
        super();
    }

    public Goods(String id, String foodname, float heats, float fat, float protein,
                 float Carbohydrates, float Ca, float Fe) {
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

    public float getHeats() {
        return heats;
    }

    public void setHeats(float heats) {
        this.heats = heats;
    }

    public float getFat() {
        return fat;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public float getCarbohydrates() {
        return Carbohydrates;
    }

    public void setCarbohydrates(float Carbohydrates) {
        this.Carbohydrates = Carbohydrates;
    }

    public float getCa() { return Ca; }

    public void setCa(float Ca) {
        this.Ca = Ca;
    }

    public float getFe() {
        return Fe;
    }

    public void setFe(float Fe) {
        this.protein = Fe;
    }



}