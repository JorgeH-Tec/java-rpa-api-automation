package com.rpa.api.automation;

public class CursoModel {
    private final String nome;
    private final String data;
    private int inscritos;
    private int ouvintes;
    private final int linhaPlanilha;

    public CursoModel(String nome, String data, int linhaPlanilha) {
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
