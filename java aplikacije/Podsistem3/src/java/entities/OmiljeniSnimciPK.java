/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author vukluzanin
 */
@Embeddable
public class OmiljeniSnimciPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "idKorisnik")
    private int idKorisnik;
    @Basic(optional = false)
    @NotNull
    @Column(name = "idAudio")
    private int idAudio;

    public OmiljeniSnimciPK() {
    }

    public OmiljeniSnimciPK(int idKorisnik, int idAudio) {
        this.idKorisnik = idKorisnik;
        this.idAudio = idAudio;
    }

    public int getIdKorisnik() {
        return idKorisnik;
    }

    public void setIdKorisnik(int idKorisnik) {
        this.idKorisnik = idKorisnik;
    }

    public int getIdAudio() {
        return idAudio;
    }

    public void setIdAudio(int idAudio) {
        this.idAudio = idAudio;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idKorisnik;
        hash += (int) idAudio;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OmiljeniSnimciPK)) {
            return false;
        }
        OmiljeniSnimciPK other = (OmiljeniSnimciPK) object;
        if (this.idKorisnik != other.idKorisnik) {
            return false;
        }
        if (this.idAudio != other.idAudio) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.OmiljeniSnimciPK[ idKorisnik=" + idKorisnik + ", idAudio=" + idAudio + " ]";
    }
    
}
