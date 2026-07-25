package com.client.mpanjifa.models;

import java.io.Serializable;
import java.util.Date;

public class Client implements Serializable {
    private String idcli;
    private String nom;
    private Date dateNaissance;
    private String photo;

    public Client(){};

    public Client(String idcli,String nom,Date dateNaissance,String photo){
        this.idcli = idcli;
        this.nom = nom;
        this.dateNaissance = dateNaissance;
        this.photo = photo;
    }

    public void setIdcli(String id){
        this.idcli = id;
    }
    public String getIdcli(){
        return idcli;
    }

    public void setNom(String nom){
        this.nom = nom;
    }
    public String getNom(){
        return nom;
    }

    public void setDateNaissance(Date date){
        this.dateNaissance = date;
    }
    public Date getDateNaissance(){
        return dateNaissance;
    }

    public void setPhoto(String photo){
        this.photo = photo;
    }
    public String getPhoto() {
        return photo;
    }
}
