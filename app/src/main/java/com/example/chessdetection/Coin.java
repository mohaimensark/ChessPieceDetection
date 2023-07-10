package com.example.chessdetection;

public class Coin {
    private String symbol;
    private String name;
    private int rank;
    private double priceUSD;

    public Coin(String symbol, String name, int rank, double priceUSD) {
        this.symbol = symbol;
        this.name = name;
        this.rank = rank;
        this.priceUSD = priceUSD;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public int getRank() {
        return rank;
    }

    public double getPriceUSD() {
        return priceUSD;
    }
}
