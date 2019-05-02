/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean;

import java.io.Serializable;
import java.sql.Date;

/**
 *
 * @author Edenilson
 */
public class cadPaciente implements Serializable{

    private String nome;
    private Date dt_nasciment;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getDt_nasciment() {
        return dt_nasciment;
    }

    public void setDt_nasciment(Date dt_nasciment) {
        this.dt_nasciment = dt_nasciment;
    }
    
    
}
