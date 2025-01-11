package entities;

import entities.Paket;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.12.v20230209-rNA", date="2025-01-12T00:00:55")
@StaticMetamodel(Pretplata.class)
public class Pretplata_ { 

    public static volatile SingularAttribute<Pretplata, Date> datumPocetka;
    public static volatile SingularAttribute<Pretplata, Paket> idPaket;
    public static volatile SingularAttribute<Pretplata, BigDecimal> cena;
    public static volatile SingularAttribute<Pretplata, Integer> idPretplata;
    public static volatile SingularAttribute<Pretplata, Integer> idKorisnik;

}