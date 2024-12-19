/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package thethongminh.model;

/**
 *
 * @author ACER
 */
public class Card {
    private String cardId;
    private int modulus;
    private int exponent;

    public Card(String cardId, int modulus, int exponent) {
        this.cardId = cardId;
        this.modulus = modulus;
        this.exponent = exponent;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public int getModulus() {
        return modulus;
    }

    public void setModulus(int modulus) {
        this.modulus = modulus;
    }

    public int getExponent() {
        return exponent;
    }

    public void setExponent(int exponent) {
        this.exponent = exponent;
    }
    
    
    
}
