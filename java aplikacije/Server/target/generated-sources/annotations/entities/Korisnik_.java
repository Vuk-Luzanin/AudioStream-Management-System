package entities;

import entities.Mesto;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.12.v20230209-rNA", date="2025-01-12T00:00:55")
@StaticMetamodel(Korisnik.class)
public class Korisnik_ { 

    public static volatile SingularAttribute<Korisnik, String> ime;
    public static volatile SingularAttribute<Korisnik, Mesto> idMesto;
    public static volatile SingularAttribute<Korisnik, Integer> godiste;
    public static volatile SingularAttribute<Korisnik, String> sifra;
    public static volatile SingularAttribute<Korisnik, Character> pol;
    public static volatile SingularAttribute<Korisnik, String> email;
    public static volatile SingularAttribute<Korisnik, Integer> idKorisnik;

}