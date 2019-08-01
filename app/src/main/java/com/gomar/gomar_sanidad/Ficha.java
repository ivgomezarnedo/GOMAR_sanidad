package com.gomar.gomar_sanidad;

import java.io.Serializable;

/**
 * The class Ficha will serve to store all the information that will be in a row of Ficha Table.
 * Notice that a Ficha will has, at least, one component.
 */
public class Ficha implements Serializable {
    private String nombre;
    private String lote;
    private String fecha_elaboracion;
    private Double kg_Producto;
    private String fecha_preferente;

    /**
     * Instantiates a new Ficha.
     *
     * @param nombre            Name of the Ficha
     * @param lote              ID of the Ficha
     * @param fecha_elaboracion Elaboration Date of the product that is represented by this Ficha
     * @param kg_Producto       Weight (in KG) of the product that is represented by this Ficha
     * @param fecha_preferente  Expiration Date of the product that is represented by this Ficha
     */
    public Ficha(String nombre, String lote, String fecha_elaboracion, Double kg_Producto, String fecha_preferente) {
        this.nombre = nombre;
        this.lote = lote;
        this.fecha_elaboracion = fecha_elaboracion;
        this.kg_Producto = kg_Producto;
        this.fecha_preferente = fecha_preferente;
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
     * Gets fecha elaboracion.
     *
     * @return the fecha elaboracion
     */
    public String getFecha_elaboracion() {
        return fecha_elaboracion;
    }

    /**
     * Sets fecha elaboracion.
     *
     * @param fecha_elaboracion the fecha elaboracion
     */
    public void setFecha_elaboracion(String fecha_elaboracion) {
        this.fecha_elaboracion = fecha_elaboracion;
    }

    /**
     * Gets producto.
     *
     * @return the producto
     */
    public Double getkg_Producto() {
        return kg_Producto;
    }

    /**
     * Sets producto.
     *
     * @param kg_Producto the kg producto
     */
    public void setkg_Producto(Double kg_Producto) {
        kg_Producto = kg_Producto;
    }

    /**
     * Gets fecha preferente.
     *
     * @return the fecha preferente
     */
    public String getFecha_preferente() {
        return fecha_preferente;
    }

    /**
     * Sets fecha preferente.
     *
     * @param fecha_preferente the fecha preferente
     */
    public void setFecha_preferente(String fecha_preferente) {
        this.fecha_preferente = fecha_preferente;
    }

    /**
     * The method 'equals' is overwritten (is part of Object) to be able to use 'get' method in List type.
     * If this method had not been overwritten, when performing a search to find if an object is in a List, if this object
     * had different reference than the one searched, the 'search' result will be negative.
     * By overwriting this method, it is being told how to make the comparison between objects (otherwise, it will compare them by their reference to memory)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) //If reference is the same
            return true;
        if (obj == null) //If object is null
            return false;
        if (getClass() != obj.getClass()) //If class is not the same
            return false;
        Ficha other = (Ficha) obj;
        if (nombre == null) {
            if (other.nombre != null)
                return false;
        } else if (!nombre.equals(other.nombre))
            return false;
        if (lote == null) {
            if (other.lote != null)
                return false;
        } else if (!lote.equals(other.lote))
            return false;
        if (fecha_elaboracion == null) {
            if (other.fecha_elaboracion != null)
                return false;
        } else if (!fecha_elaboracion.equals(other.fecha_elaboracion))
            return false;
        if (kg_Producto == null) {
            if (other.kg_Producto != null)
                return false;
        } else if (!kg_Producto.equals(other.kg_Producto))
            return false;
        if (fecha_preferente == null) {
            if (other.fecha_preferente != null)
                return false;
        } else if (!fecha_preferente.equals(other.fecha_preferente))
            return false;
        return true;
    }
}

