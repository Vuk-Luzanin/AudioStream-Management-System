/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vukluzanin
 */
@Entity
@Table(name = "slusanje")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Slusanje.findAll", query = "SELECT s FROM Slusanje s"),
    @NamedQuery(name = "Slusanje.findByIdSlusanje", query = "SELECT s FROM Slusanje s WHERE s.idSlusanje = :idSlusanje"),
    @NamedQuery(name = "Slusanje.findByIdKorisnik", query = "SELECT s FROM Slusanje s WHERE s.idKorisnik = :idKorisnik"),
    @NamedQuery(name = "Slusanje.findByIdAudio", query = "SELECT s FROM Slusanje s WHERE s.idAudio = :idAudio"),
    @NamedQuery(name = "Slusanje.findByDatumPocetka", query = "SELECT s FROM Slusanje s WHERE s.datumPocetka = :datumPocetka"),
    @NamedQuery(name = "Slusanje.findBySekundPocetka", query = "SELECT s FROM Slusanje s WHERE s.sekundPocetka = :sekundPocetka"),
    @NamedQuery(name = "Slusanje.findBySekundOdslusano", query = "SELECT s FROM Slusanje s WHERE s.sekundOdslusano = :sekundOdslusano")})
public class Slusanje implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idSlusanje")
    private Integer idSlusanje;
    @Column(name = "idKorisnik")
    private Integer idKorisnik;
    @Column(name = "idAudio")
    private Integer idAudio;
    @Basic(optional = false)
    @NotNull
    @Column(name = "DatumPocetka")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datumPocetka;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SekundPocetka")
    private int sekundPocetka;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SekundOdslusano")
    private int sekundOdslusano;

    public Slusanje() {
    }

    public Slusanje(Integer idSlusanje) {
        this.idSlusanje = idSlusanje;
    }

    public Slusanje(Integer idSlusanje, Date datumPocetka, int sekundPocetka, int sekundOdslusano) {
        this.idSlusanje = idSlusanje;
        this.datumPocetka = datumPocetka;
        this.sekundPocetka = sekundPocetka;
        this.sekundOdslusano = sekundOdslusano;
    }

    public Integer getIdSlusanje() {
        return idSlusanje;
    }

    public void setIdSlusanje(Integer idSlusanje) {
        this.idSlusanje = idSlusanje;
    }

    public Integer getIdKorisnik() {
        return idKorisnik;
    }

    public void setIdKorisnik(Integer idKorisnik) {
        this.idKorisnik = idKorisnik;
    }

    public Integer getIdAudio() {
        return idAudio;
    }

    public void setIdAudio(Integer idAudio) {
        this.idAudio = idAudio;
    }

    public Date getDatumPocetka() {
        return datumPocetka;
    }

    public void setDatumPocetka(Date datumPocetka) {
        this.datumPocetka = datumPocetka;
    }

    public int getSekundPocetka() {
        return sekundPocetka;
    }

    public void setSekundPocetka(int sekundPocetka) {
        this.sekundPocetka = sekundPocetka;
    }

    public int getSekundOdslusano() {
        return sekundOdslusano;
    }

    public void setSekundOdslusano(int sekundOdslusano) {
        this.sekundOdslusano = sekundOdslusano;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idSlusanje != null ? idSlusanje.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Slusanje)) {
            return false;
        }
        Slusanje other = (Slusanje) object;
        if ((this.idSlusanje == null && other.idSlusanje != null) || (this.idSlusanje != null && !this.idSlusanje.equals(other.idSlusanje))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Slusanje[ idSlusanje=" + idSlusanje + " ]";
    }
    
}
