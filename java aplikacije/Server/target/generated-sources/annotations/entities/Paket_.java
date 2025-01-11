package entities;

import entities.Pretplata;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.12.v20230209-rNA", date="2025-01-12T00:00:55")
@StaticMetamodel(Paket.class)
public class Paket_ { 

    public static volatile ListAttribute<Paket, Pretplata> pretplataList;
    public static volatile SingularAttribute<Paket, String> naziv;
    public static volatile SingularAttribute<Paket, Integer> idPaket;
    public static volatile SingularAttribute<Paket, BigDecimal> cena;

}