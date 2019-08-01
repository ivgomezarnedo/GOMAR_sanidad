package com.gomar.gomar_sanidad;

import java.io.Serializable;

/**
 * The type Condicion will serve to standarise the different components that form a Condition.
 * In this class we have four attributes: campo,criterio and valor.
 * See constructor method to get more information about these parameters
 */
public class Condicion implements Serializable {


    private String campo;
    private String criterio;
    private String valor;
    private boolean numerico;

    /**
     * Instantiates a new Condicion.
     *
     * @param campo    It will contain the field we want to search for
     * @param criterio It will contain one of the criterion of the search. It could be one of these values: '<','>' or '='
     * @param valor    It will contain the value by which we want to search in the previous @campo
     * @param numerico It will contain true if the @campo is numeric or false if not.
     */
    public Condicion(String campo, String criterio, String valor, Boolean numerico) {
        this.campo = campo;
        this.criterio = criterio;
        this.valor = valor;
        this.numerico=numerico;
    }

    /**
     * Gets campo.
     *
     * @return the campo
     */
    public String getCampo() {
        return campo;
    }

    /**
     * Gets criterio.
     *
     * @return the criterio
     */
    public String getCriterio() {
        return criterio;
    }

    /**
     * Gets valor.
     *
     * @return the valor
     */
    public String getValor() {
        return valor;
    }

    /**
     * Sets campo.
     *
     * @param campo the campo
     */
    public void setCampo(String campo) {
        this.campo = campo;
    }

    /**
     * Sets criterio.
     *
     * @param criterio the criterio
     */
    public void setCriterio(String criterio) {
        this.criterio = criterio;
    }

    /**
     * Sets valor.
     *
     * @param valor the valor
     */
    public void setValor(String valor) {
        this.valor = valor;
    }


    /**
     * The method 'toString' is overwritten (is part of Object) to create the formatted String that will be added to the SQL query that we will use to search in the DB.
     * Notice that there are three different ways: a numeric way, a normal text way with functionality to be made case insensitive
     * and a Date way (these three ways will return different Strings).
     */

    //TODO El hecho de que tengamos los nombres de los campos de la BD hardcodeados aquí es una violación de la separación en capas.
    @Override
    public String toString() {
        if(numerico)
            return campo+" "+criterio+" "+valor; //Numeric way
        else
        {
            if(campo.equals(SQL.NombreFichaCampo) || campo.equals(SQL.NombreComponenteCampo) || campo.equals(SQL.ProveedorComponenteCampo) || campo.equals(SQL.LoteComponenteCampo))
            {
                return "upper("+campo+") "+criterio+" '"+valor.toUpperCase()+"'"; //Normal text way

            }
            return campo+" "+criterio+" '"+valor+"'"; //Date way

        }

    }


}
