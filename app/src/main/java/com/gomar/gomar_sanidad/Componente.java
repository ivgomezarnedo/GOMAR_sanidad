package com.gomar.gomar_sanidad;

import java.io.Serializable;

/**
 * The class Componente will serve to store all the information that will be in a row of Componente Table.
 * A Componente can form part of several Fichas.
 */
public class Componente implements Serializable {
    private String nombre;
    private String lote;
    private String proveedor;
    private Double Kg_Componente;
    private String ficha;

    /**
     * Instantiates a new Componente.
     *
     * @param nombre        Name of the Componente
     * @param lote          Id of the Componente
     * @param proveedor     Name of the supplier of this Componente
     * @param kg_Componente Weight (in KG) of the Componente
     * @param ficha         ID of the card of which this Componente is a part.
     */
    public Componente(String nombre, String lote, String proveedor, Double kg_Componente, String ficha){
        this.nombre = nombre;
        this.lote = lote;
        this.proveedor = proveedor;
        Kg_Componente = kg_Componente;
        this.ficha = ficha;
    }

    /**
     * Gets nombre.
     *
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Sets nombre.
     *
     * @param nombre the nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Gets lote.
     *
     * @return the lote
     */
    public String getLote() {
        return lote;
    }

    /**
     * Sets lote.
     *
     * @param lote the lote
     */
    public void setLote(String lote) {
        this.lote = lote;
    }

    /**
     * Gets proveedor.
     *
     * @return the proveedor
     */
    public String getProveedor() {
        return proveedor;
    }

    /**
     * Sets proveedor.
     *
     * @param proveedor the proveedor
     */
    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * Gets kg componente.
     *
     * @return the kg componente
     */
    public Double getKg_Componente() {
        return Kg_Componente;
    }

    /**
     * Sets kg componente.
     *
     * @param kg_Componente the kg componente
     */
    public void setKg_Componente(Double kg_Componente) {
        Kg_Componente = kg_Componente;
    }

    /**
     * Gets ficha.
     *
     * @return the ficha
     */
    public String getFicha() {
        return ficha;
    }

    /**
     * Sets ficha.
     *
     * @param ficha the ficha
     */
    public void setFicha(String ficha) {
        this.ficha = ficha;
    }


}
