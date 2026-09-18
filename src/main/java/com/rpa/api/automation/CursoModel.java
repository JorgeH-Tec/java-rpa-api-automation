package com.rpa.api.automation;

public class CursoModel {
    private final int id;
    private final String nome;
    private final String data;
    private int inscritos;
    private int ouvintes;
    private final int linhaPlanilha;

    public CursoModel(int id, String nome, String data, int linhaPlanilha) {
        this.id = id;
        this.nome = nome;
        this.data = data;
        this.linhaPlanilha = linhaPlanilha;
    }

    public String getNome() {
        return nome;
    }
    public String getData() {
        return data;
    }
    public int getLinhaPlanilha() {
        return linhaPlanilha;
    }
    public int getId() {
        return id;
    }

    public int getInscritos() {
        return inscritos;
    }
    public void setInscritos(int inscritos) {
        this.inscritos = inscritos;
    }

    public int getOuvintes() {
        return ouvintes;
    }
    public void setOuvintes(int ouvintes) {
        this.ouvintes = ouvintes;
    }
}
