package com.example.demo;

public class Equipement {
    private int idEquip;
    private String libEquip;
    private int idBatVoy;

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
}
