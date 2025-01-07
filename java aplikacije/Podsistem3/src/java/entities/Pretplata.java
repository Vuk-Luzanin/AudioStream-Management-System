/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "pretplata")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pretplata.findAll", query = "SELECT p FROM Pretplata p"),
    @NamedQuery(name = "Pretplata.findByIdPretplata", query = "SELECT p FROM Pretplata p WHERE p.idPretplata = :idPretplata"),
    @NamedQuery(name = "Pretplata.findByIdKorisnik", query = "SELECT p FROM Pretplata p WHERE p.idKorisnik = :idKorisnik"),
    @NamedQuery(name = "Pretplata.findByDatumPocetka", query = "SELECT p FROM Pretplata p WHERE p.datumPocetka = :datumPocetka"),
    @NamedQuery(name = "Pretplata.findByCena", query = "SELECT p FROM Pretplata p WHERE p.cena = :cena")})
public class Pretplata implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idPretplata")
    private Integer idPretplata;
    @Column(name = "idKorisnik")
    private Integer idKorisnik;
    @Basic(optional = false)
    @NotNull
    @Column(name = "DatumPocetka")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datumPocetka;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "Cena")
    private BigDecimal cena;
    @JoinColumn(name = "idPaket", referencedColumnName = "idPaket")
    @ManyToOne
    private Paket idPaket;

    public Pretplata() {
    }

    public Pretplata(Integer idPretplata) {
        this.idPretplata = idPretplata;
    }

    public Pretplata(Integer idPretplata, Date datumPocetka, BigDecimal cena) {
        this.idPretplata = idPretplata;
        this.datumPocetka = datumPocetka;
        this.cena = cena;
    }

    public Integer getIdPretplata() {
        return idPretplata;
    }

    public void setIdPretplata(Integer idPretplata) {
        this.idPretplata = idPretplata;
    }

    public Integer getIdKorisnik() {
        return idKorisnik;
    }

    public void setIdKorisnik(Integer idKorisnik) {
        this.idKorisnik = idKorisnik;
    }

    public Date getDatumPocetka() {
        return datumPocetka;
    }

    public void setDatumPocetka(Date datumPocetka) {
        this.datumPocetka = datumPocetka;
    }

    public BigDecimal getCena() {
        return cena;
    }

    public void setCena(BigDecimal cena) {
        this.cena = cena;
    }

    public Paket getIdPaket() {
        return idPaket;
    }

    public void setIdPaket(Paket idPaket) {
        this.idPaket = idPaket;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPretplata != null ? idPretplata.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pretplata)) {
            return false;
        }
        Pretplata other = (Pretplata) object;
        if ((this.idPretplata == null && other.idPretplata != null) || (this.idPretplata != null && !this.idPretplata.equals(other.idPretplata))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Pretplata[ idPretplata=" + idPretplata + " ]";
    }
    
}
