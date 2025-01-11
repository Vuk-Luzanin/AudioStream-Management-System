package entities;

import entities.Kategorija;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.12.v20230209-rNA", date="2025-01-12T00:00:55")
@StaticMetamodel(Audio.class)
public class Audio_ { 

    public static volatile ListAttribute<Audio, Kategorija> kategorijaList;
    public static volatile SingularAttribute<Audio, Integer> idAudio;
    public static volatile SingularAttribute<Audio, Date> datumPostavljanja;
    public static volatile SingularAttribute<Audio, BigDecimal> trajanje;
    public static volatile SingularAttribute<Audio, String> naziv;
    public static volatile SingularAttribute<Audio, Integer> idKorisnik;

}