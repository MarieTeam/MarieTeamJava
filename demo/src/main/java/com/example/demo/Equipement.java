package com.example.demo;

public class Equipement {
    private static int nextId = 1; // This will keep track of the next id to be assigned
    private int idEquip;
    private String libEquip;
    private int idBatVoy;

    public Equipement(String libEquip, int idBatVoy) {
        this.idEquip = nextId++; // Assign the next id, then increment the value
        this.libEquip = libEquip;
        this.idBatVoy = idBatVoy;
    }

    public Equipement(int idEquip, String libEquip, int idBatVoy) {
        this.idEquip = idEquip;
        this.libEquip = libEquip;
        this.idBatVoy = idBatVoy;
    }

    public int getIdEquip() {
        return idEquip;
    }

    public String getLibEquip() {
        return libEquip;
    }

    public int getIdBatVoy() {
        return idBatVoy;
    }

    @Override
    public String toString() {
        return this.libEquip;
    }
}
