package com.progii.finalgen.domain;

public enum Modo {
    INICIODIA("INICIODIA"),
    AHORA("AHORA"),
    FINDIA("FINDIA");

    private final String modo;

    Modo(String modo) {
        this.modo = modo;
    }

    public String getModo() {
        return modo;
    }
}
