package podsistem2Entities;

import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import podsistem2Entities.Kategorija;

@Generated(value="EclipseLink-2.7.12.v20230209-rNA", date="2025-01-10T18:18:22")
@StaticMetamodel(Audio.class)
public class Audio_ { 

    public static volatile ListAttribute<Audio, Kategorija> kategorijaList;
    public static volatile SingularAttribute<Audio, Integer> idAudio;
    public static volatile SingularAttribute<Audio, Date> datumPostavljanja;
    public static volatile SingularAttribute<Audio, BigDecimal> trajanje;
    public static volatile SingularAttribute<Audio, String> naziv;
    public static volatile SingularAttribute<Audio, Integer> idKorisnik;

}