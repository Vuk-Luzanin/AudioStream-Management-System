/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author vukluzanin
 */
@Entity
@Table(name = "audio")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Audio.findAll", query = "SELECT a FROM Audio a"),
    @NamedQuery(name = "Audio.findByIdAudio", query = "SELECT a FROM Audio a WHERE a.idAudio = :idAudio"),
    @NamedQuery(name = "Audio.findByNaziv", query = "SELECT a FROM Audio a WHERE a.naziv = :naziv"),
    @NamedQuery(name = "Audio.findByTrajanje", query = "SELECT a FROM Audio a WHERE a.trajanje = :trajanje"),
    @NamedQuery(name = "Audio.findByIdKorisnik", query = "SELECT a FROM Audio a WHERE a.idKorisnik = :idKorisnik"),
    @NamedQuery(name = "Audio.findByDatumPostavljanja", query = "SELECT a FROM Audio a WHERE a.datumPostavljanja = :datumPostavljanja")})
public class Audio implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idAudio")
    private Integer idAudio;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "Naziv")
    private String naziv;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "Trajanje")
    private BigDecimal trajanje;
    @Column(name = "idKorisnik")
    private Integer idKorisnik;
    @Basic(optional = false)
    @NotNull
    @Column(name = "DatumPostavljanja")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datumPostavljanja;
    @JoinTable(name = "audioKategorija", joinColumns = {
        @JoinColumn(name = "idAudio", referencedColumnName = "idAudio")}, inverseJoinColumns = {
        @JoinColumn(name = "idKategorija", referencedColumnName = "idKategorija")})
    @ManyToMany
    private List<Kategorija> kategorijaList;

    public Audio() {
    }

    public Audio(Integer idAudio) {
        this.idAudio = idAudio;
    }

    public Audio(Integer idAudio, String naziv, BigDecimal trajanje, Date datumPostavljanja) {
        this.idAudio = idAudio;
        this.naziv = naziv;
        this.trajanje = trajanje;
        this.datumPostavljanja = datumPostavljanja;
    }

    public Integer getIdAudio() {
        return idAudio;
    }

    public void setIdAudio(Integer idAudio) {
        this.idAudio = idAudio;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public BigDecimal getTrajanje() {
        return trajanje;
    }

    public void setTrajanje(BigDecimal trajanje) {
        this.trajanje = trajanje;
    }

    public Integer getIdKorisnik() {
        return idKorisnik;
    }

    public void setIdKorisnik(Integer idKorisnik) {
        this.idKorisnik = idKorisnik;
    }

    public Date getDatumPostavljanja() {
        return datumPostavljanja;
    }

    public void setDatumPostavljanja(Date datumPostavljanja) {
        this.datumPostavljanja = datumPostavljanja;
    }

    @XmlTransient
    public List<Kategorija> getKategorijaList() {
        return kategorijaList;
    }

    public void setKategorijaList(List<Kategorija> kategorijaList) {
        this.kategorijaList = kategorijaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idAudio != null ? idAudio.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Audio)) {
            return false;
        }
        Audio other = (Audio) object;
        if ((this.idAudio == null && other.idAudio != null) || (this.idAudio != null && !this.idAudio.equals(other.idAudio))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Audio[ idAudio=" + idAudio + " ]";
    }
    
}
