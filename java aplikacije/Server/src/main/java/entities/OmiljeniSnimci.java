/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vukluzanin
 */
@Entity
@Table(name = "omiljeniSnimci")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OmiljeniSnimci.findAll", query = "SELECT o FROM OmiljeniSnimci o"),
    @NamedQuery(name = "OmiljeniSnimci.findByIdKorisnik", query = "SELECT o FROM OmiljeniSnimci o WHERE o.omiljeniSnimciPK.idKorisnik = :idKorisnik"),
    @NamedQuery(name = "OmiljeniSnimci.findByIdAudio", query = "SELECT o FROM OmiljeniSnimci o WHERE o.omiljeniSnimciPK.idAudio = :idAudio")})
public class OmiljeniSnimci implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected OmiljeniSnimciPK omiljeniSnimciPK;


    public OmiljeniSnimci() {
    }

    public OmiljeniSnimci(OmiljeniSnimciPK omiljeniSnimciPK) {
        this.omiljeniSnimciPK = omiljeniSnimciPK;
    }

    public OmiljeniSnimci(int idKorisnik, int idAudio) {
        this.omiljeniSnimciPK = new OmiljeniSnimciPK(idKorisnik, idAudio);
    }

    public OmiljeniSnimciPK getOmiljeniSnimciPK() {
        return omiljeniSnimciPK;
    }

    public void setOmiljeniSnimciPK(OmiljeniSnimciPK omiljeniSnimciPK) {
        this.omiljeniSnimciPK = omiljeniSnimciPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (omiljeniSnimciPK != null ? omiljeniSnimciPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OmiljeniSnimci)) {
            return false;
        }
        OmiljeniSnimci other = (OmiljeniSnimci) object;
        if ((this.omiljeniSnimciPK == null && other.omiljeniSnimciPK != null) || (this.omiljeniSnimciPK != null && !this.omiljeniSnimciPK.equals(other.omiljeniSnimciPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.OmiljeniSnimci[ omiljeniSnimciPK=" + omiljeniSnimciPK + " ]";
    }
    
}
