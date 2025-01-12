
package podsistem3;

import entities.*;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import komunikacija.Reply;
import komunikacija.Request;


public class Main {
    
    //jms
    @Resource(lookup="projectConnFactory")
    private static ConnectionFactory connectionFactory;
    
    @Resource(lookup="projectTopicServer")
    private static Topic topic;
    
    private static JMSContext context = null;
    private static JMSConsumer consumer=null;
    private static JMSProducer producer = null;
    
    //jpa
    // for podsistem3 database
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("Podsistem3PU");
    private static EntityManager em = emf.createEntityManager();
    
    // for podsistem2 database
    private static EntityManagerFactory emfpodsistem2 = Persistence.createEntityManagerFactory("Podsistem2PU");
    private static EntityManager empodsistem2 = emfpodsistem2.createEntityManager();
    
    // for podsistem1 database
    private static EntityManagerFactory emfpodsistem1 = Persistence.createEntityManagerFactory("Podsistem1PU");
    private static EntityManager empodsistem1 = emfpodsistem1.createEntityManager();
    
    private static final int KREIRAJ_PAKET = 9;
    private static final int PROMENA_CENE_PAKETA = 10;
    private static final int KREIRAJ_PRETPLATU = 11;
    private static final int KREIRAJ_SLUSANJE = 12;
    private static final int DODAJ_OMILJENI_SNIMAK = 13;
    private static final int KREIRAJ_OCENU = 14;
    private static final int PROMENA_OCENE = 15;
    private static final int BRISANJE_OCENE = 16;
    private static final int DOHVATI_PAKETE = 23;
    private static final int DOHVATI_PRETPLATE = 24;
    private static final int DOHVATI_SLUSANJA_SNIMKA = 25;
    private static final int DOHVATI_OCENE_SNIMKA = 26;
    private static final int DOHVATI_OMILJENE_SNIMKE = 27;
    
    private static int ID_SEND = 0;
    
    private static void persistObject(Object o)
    {
        em.getTransaction().begin();
        em.persist(o);
        em.flush();
        em.getTransaction().commit();
    }
    
    private static void removeObject(Object o)
    {
        em.getTransaction().begin();
        em.remove(o);
        em.flush();
        em.getTransaction().commit();
    }
    
    //zahtev 9
    private static Reply kreirajPaket(String naziv, String cena)
    {
        List<Paket> paketi = em.createNamedQuery("Paket.findByNaziv").setParameter("naziv", naziv).getResultList();
        if(!paketi.isEmpty())
            return new Reply(-1, "VEC POSTOJI PAKET: " + naziv, null);
        
        Paket p = new Paket();
        p.setNaziv(naziv);
        p.setCena(new BigDecimal(cena));
        persistObject(p);
        return new Reply(0, "USPESNO KREIRAN PAKET: " + naziv, null);
    }
    
    //zahtev 10
    private static Reply promeniCenu(String naziv, String cena)
    {
        List<Paket> paketi = em.createNamedQuery("Paket.findByNaziv").setParameter("naziv", naziv).getResultList();
        if(paketi.isEmpty())
            return new Reply(-1, "NE POSTOJI PAKET: " + naziv, null);
        Paket p = paketi.get(0);
        
        p.setCena(new BigDecimal(cena));
        persistObject(p);
        return new Reply(0, "USPESNO PROMENJENA CENA PAKETU: " + naziv, null);
    }
    
    //zahtev 11
    // curKorisnikId - TRENUTNO ULOGOVAN KORISNIK -> sigurno postoji u sistemu
    private static Reply kreirajPretplatu(String nazivPaketa, String datum, int curKorisnikId)
    { 
        List<Paket> paketi = em.createNamedQuery("Paket.findByNaziv").setParameter("naziv", nazivPaketa).getResultList();
        if(paketi.isEmpty())
            return new Reply(-1, "NE POSTOJI PAKET: " + nazivPaketa, null);
        Paket p = paketi.get(0);
        
        // Dekodiraj datum
        String decodedDatum = null;
        try {
            decodedDatum = URLDecoder.decode(datum, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date datumPostavljanja = null;
        try 
        {
            datumPostavljanja = dateFormat.parse(decodedDatum);
        } catch (ParseException e) 
        {
            System.out.println("DATUM NIJE U ISPRAVNOM FORMATU: " + datum);
        }
        
        //datumIsteka
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(datumPostavljanja); 
        calendar.add(Calendar.MONTH, 1);    
        Date datumIsteka = calendar.getTime();   
        
        // Proveri da li korisnik već ima važeću pretplatu
        List<Pretplata> aktivnePretplate = em.createQuery(
            "SELECT p FROM Pretplata p WHERE p.idKorisnik = :korisnikId AND :datum BETWEEN p.datumPocetka AND :datumIsteka",
            Pretplata.class)
            .setParameter("korisnikId", curKorisnikId)
            .setParameter("datum", datumPostavljanja)
            .setParameter("datumIsteka", datumIsteka)
            .getResultList();

        if (!aktivnePretplate.isEmpty()) {
            return new Reply(-1, "KORISNIK VEC IMA VAZECU PRETPLATU", null);
        }
        
        Pretplata pretplata = new Pretplata();
        pretplata.setIdKorisnik(curKorisnikId); pretplata.setCena(p.getCena());
        pretplata.setDatumPocetka(datumPostavljanja); pretplata.setIdPaket(p);
        persistObject(pretplata);
        return new Reply(0, "USPESNO KREIRANA PRETPLATA: " + nazivPaketa, null);
    }
    
    //zahtev 12
    // curKorisnikId - TRENUTNO ULOGOVAN KORISNIK -> sigurno postoji u sistemu
    private static Reply kreirajSlusanje(int curKorisnikId, String nazivSnimka, String imeVlasnika,
                                        String datumPocetka, int sekundPocetka, int sekundOdslusano)
    { 
       List<Korisnik> vlasnici = empodsistem1.createNamedQuery("Korisnik.findByIme").setParameter("ime", imeVlasnika).getResultList();
        if(vlasnici.isEmpty())
            return new Reply(-1, "NE POSTOJI VLASNIK SNIMKA: " + imeVlasnika, null);
        Korisnik vlasnik = vlasnici.get(0);
        
        // da li dati vlasnik ima audio snimak sa tim imenom
        // Audio.findByIDKorNaziv
        List<Audio> snimci = empodsistem2.createQuery("SELECT a FROM Audio a WHERE a.naziv = :naziv and a.idKorisnik = :idKorisnik")
                .setParameter("idKorisnik", vlasnik.getIdKorisnik())
                .setParameter("naziv", nazivSnimka)
                .getResultList();
        if (snimci.isEmpty()) 
            return new Reply(-1, "ZADATI VLASNIK NEMA AUDIO SNIMAK SA NAZIVOM: " + nazivSnimka, null);
        Audio a = snimci.get(0);
        
        if (sekundPocetka < 0 || sekundOdslusano < 0) 
            return new Reply(-1, "BROJ SEKUDNI NE MOZE BITI NEGATIVAN", null);
        
        // Dekodiraj datum
        String decodedDatum = null;
        try {
            decodedDatum = URLDecoder.decode(datumPocetka, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date datumPostavljanja = null;
        try 
        {
            datumPostavljanja = dateFormat.parse(decodedDatum);
        } catch (ParseException e) 
        {
            System.out.println("DATUM NIJE U ISPRAVNOM FORMATU: " + datumPocetka);
        }
        if (datumPostavljanja.before(a.getDatumPostavljanja()))
        {
            return new Reply(-1, "SLUSANJE MORA BITI NAKON POSTAVLJANJA SNIMKA", null);
        }
        
        Slusanje s = new Slusanje();
        s.setIdKorisnik(curKorisnikId); s.setIdAudio(a.getIdAudio());
        s.setDatumPocetka(datumPostavljanja); s.setSekundPocetka(sekundPocetka); s.setSekundOdslusano(sekundOdslusano);
        persistObject(s);
        return new Reply(0, "USPESNO KREIRANO SLUSANJE", null);
    }
    
    //zahtev 13
    // curKorisnikId - TRENUTNO ULOGOVAN KORISNIK -> sigurno postoji u sistemu
    private static Reply dodajOmiljene(int curKorisnikId, String nazivSnimka, String imeVlasnika)
    { 
       List<Korisnik> vlasnici = empodsistem1.createNamedQuery("Korisnik.findByIme").setParameter("ime", imeVlasnika).getResultList();
        if(vlasnici.isEmpty())
            return new Reply(-1, "NE POSTOJI VLASNIK SNIMKA: " + imeVlasnika, null);
        Korisnik vlasnik = vlasnici.get(0);
        
        // da li dati vlasnik ima audio snimak sa tim imenom
        // Audio.findByIDKorNaziv
        List<Audio> snimci = empodsistem2.createQuery("SELECT a FROM Audio a WHERE a.naziv = :naziv and a.idKorisnik = :idKorisnik")
                .setParameter("idKorisnik", vlasnik.getIdKorisnik())
                .setParameter("naziv", nazivSnimka)
                .getResultList();
        if (snimci.isEmpty()) 
            return new Reply(-1, "ZADATI VLASNIK NEMA AUDIO SNIMAK SA NAZIVOM: " + nazivSnimka, null);
        Audio a = snimci.get(0);
        
        // da li je vec dodao taj snimak u listu omiljenih
        List<OmiljeniSnimci> omiljeni = em.createQuery("SELECT o FROM OmiljeniSnimci o WHERE o.omiljeniSnimciPK.idKorisnik = :idKorisnik AND o.omiljeniSnimciPK.idAudio = :idAudio")
                .setParameter("idKorisnik", curKorisnikId)
                .setParameter("idAudio", a.getIdAudio())
                .getResultList();
        if (!omiljeni.isEmpty()) 
            return new Reply(-1, "KORISNIK JE VEC DODAO U OMILJENE AUDIO SNIMAK SA NAZIVOM: " + nazivSnimka, null);
        
        OmiljeniSnimci om = new OmiljeniSnimci(curKorisnikId, a.getIdAudio());
        persistObject(om);
        return new Reply(0, "USPESNO SNIMAK DODAT U LISTU OMILJENIH", null);
    }
    
    //zahtev 14
    // curKorisnikId - TRENUTNO ULOGOVAN KORISNIK -> sigurno postoji u sistemu
    private static Reply kreirajOcenu(int curKorisnikId, String nazivSnimka, String imeVlasnika, int ocena, String datum)
    { 
       List<Korisnik> vlasnici = empodsistem1.createNamedQuery("Korisnik.findByIme").setParameter("ime", imeVlasnika).getResultList();
        if(vlasnici.isEmpty())
            return new Reply(-1, "NE POSTOJI VLASNIK SNIMKA: " + imeVlasnika, null);
        Korisnik vlasnik = vlasnici.get(0);
        
        // da li dati vlasnik ima audio snimak sa tim imenom
        // Audio.findByIDKorNaziv
        List<Audio> snimci = empodsistem2.createQuery("SELECT a FROM Audio a WHERE a.naziv = :naziv and a.idKorisnik = :idKorisnik")
                .setParameter("idKorisnik", vlasnik.getIdKorisnik())
                .setParameter("naziv", nazivSnimka)
                .getResultList();
        if (snimci.isEmpty()) 
            return new Reply(-1, "ZADATI VLASNIK NEMA AUDIO SNIMAK SA NAZIVOM: " + nazivSnimka, null);
        Audio a = snimci.get(0);
        
        if (ocena < 1 || ocena > 5)
            return new Reply(-1, "OCENA MORA BITI IZMEDJU 1 I 5", null);
        
        // Dekodiraj datum
        String decodedDatum = null;
        try {
            decodedDatum = URLDecoder.decode(datum, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date datumPostavljanja = null;
        try 
        {
            datumPostavljanja = dateFormat.parse(decodedDatum);
        } catch (ParseException e) 
        {
            System.out.println("DATUM NIJE U ISPRAVNOM FORMATU: " + datum);
        }
        
        if (datumPostavljanja.before(a.getDatumPostavljanja()))
        {
            return new Reply(-1, "SNIMAK SE MOZE OCENITI NAKON POSTAVLJANJA", null);
        }
        
        List<Ocena> ocene = em.createQuery("SELECT O FROM Ocena o WHERE o.idKorisnik = :idKorisnik and o.idAudio = :idAudio")
                .setParameter("idKorisnik", curKorisnikId)
                .setParameter("idAudio", a.getIdAudio())
                .getResultList();
        if (!ocene.isEmpty())
            return new Reply(-1, "VEC STE OCENILI DATI SNIMAK", null);
        
        Ocena o = new Ocena();
        o.setIdKorisnik(curKorisnikId); o.setIdAudio(a.getIdAudio());
        o.setOcena(ocena); o.setDatum(datumPostavljanja);
        persistObject(o);
        return new Reply(0, "USPESNO KREIRANA OCENA ZA SNIMAK: " + nazivSnimka, null);
    }
    
    //zahtev 15
    // curKorisnikId - TRENUTNO ULOGOVAN KORISNIK -> sigurno postoji u sistemu
    private static Reply promeniOcenu(int curKorisnikId, String nazivSnimka, String imeVlasnika, int ocena, String datum)
    { 
       List<Korisnik> vlasnici = empodsistem1.createNamedQuery("Korisnik.findByIme").setParameter("ime", imeVlasnika).getResultList();
        if(vlasnici.isEmpty())
            return new Reply(-1, "NE POSTOJI VLASNIK SNIMKA: " + imeVlasnika, null);
        Korisnik vlasnik = vlasnici.get(0);
        
        // da li dati vlasnik ima audio snimak sa tim imenom
        // Audio.findByIDKorNaziv
        List<Audio> snimci = empodsistem2.createQuery("SELECT a FROM Audio a WHERE a.naziv = :naziv and a.idKorisnik = :idKorisnik")
                .setParameter("idKorisnik", vlasnik.getIdKorisnik())
                .setParameter("naziv", nazivSnimka)
                .getResultList();
        if (snimci.isEmpty()) 
            return new Reply(-1, "ZADATI VLASNIK NEMA AUDIO SNIMAK SA NAZIVOM: " + nazivSnimka, null);
        Audio a = snimci.get(0);
        
        // Ocena.findByIDKorIDAudio
        List<Ocena> ocene = em.createQuery("SELECT o FROM Ocena o WHERE o.idKorisnik = :idKorisnik and o.idAudio = :idAudio")
                .setParameter("idKorisnik", curKorisnikId)
                .setParameter("idAudio", a.getIdAudio())
                .getResultList();
        if(ocene.isEmpty())
            return new Reply(-1, "NE POSTOJI VASA OCENA ZA SNIMAK: " + nazivSnimka, null);
        Ocena o = ocene.get(0);
        
        // Dekodiraj datum
        String decodedDatum = null;
        try {
            decodedDatum = URLDecoder.decode(datum, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date datumPostavljanja = null;
        try 
        {
            datumPostavljanja = dateFormat.parse(decodedDatum);
        } catch (ParseException e) 
        {
            System.out.println("DATUM NIJE U ISPRAVNOM FORMATU: " + datum);
        }
        
        if (datumPostavljanja.before(a.getDatumPostavljanja()))
        {
            return new Reply(-1, "SNIMAK SE MOZE OCENITI NAKON POSTAVLJANJA", null);
        }
        
        em.getTransaction().begin();
        o.setOcena(ocena);
        o.setDatum(datumPostavljanja);
        em.flush();
        em.getTransaction().commit();
        return new Reply(0, "USPESNO PROMENJENA OCENA SNIMKU: " + nazivSnimka, null);
    }
    
    //zahtev 16
    // curKorisnikId - TRENUTNO ULOGOVAN KORISNIK -> sigurno postoji u sistemu
    private static Reply obrisiOcenu(int curKorisnikId, String nazivSnimka, String imeVlasnika)
    { 
       List<Korisnik> vlasnici = empodsistem1.createNamedQuery("Korisnik.findByIme").setParameter("ime", imeVlasnika).getResultList();
        if(vlasnici.isEmpty())
            return new Reply(-1, "NE POSTOJI VLASNIK SNIMKA: " + imeVlasnika, null);
        Korisnik vlasnik = vlasnici.get(0);
        
        // da li dati vlasnik ima audio snimak sa tim imenom
        // Audio.findByIDKorNaziv
        List<Audio> snimci = empodsistem2.createQuery("SELECT a FROM Audio a WHERE a.naziv = :naziv and a.idKorisnik = :idKorisnik")
                .setParameter("idKorisnik", vlasnik.getIdKorisnik())
                .setParameter("naziv", nazivSnimka)
                .getResultList();
        if (snimci.isEmpty()) 
            return new Reply(-1, "ZADATI VLASNIK NEMA AUDIO SNIMAK SA NAZIVOM: " + nazivSnimka, null);
        Audio a = snimci.get(0);
        
        // Ocena.findByIDKorIDAudio
        List<Ocena> ocene = em.createQuery("SELECT o FROM Ocena o WHERE o.idKorisnik = :idKorisnik and o.idAudio = :idAudio")
                .setParameter("idKorisnik", curKorisnikId)
                .setParameter("idAudio", a.getIdAudio())
                .getResultList();
        if(ocene.isEmpty())
            return new Reply(-1, "NE POSTOJI VASA OCENA ZA SNIMAK: " + nazivSnimka, null);
        Ocena o = ocene.get(0);
        
        removeObject(o);
        return new Reply(0, "USPESNO OBRISANA OCENA SNIMKU: " + nazivSnimka, null);
    }
    
    //zahtev 23
    private static Reply dohvatiPakete()
    {
        List<Paket> paketi =  em.createNamedQuery("Paket.findAll").getResultList();
        for(Paket p : paketi)
            p.setPretplataList(null);
        return new Reply(0, "DOHVACENI SVI PAKETI", paketi);
    }
    
    //zahtev 24
    private static Reply dohvatiPretplateZaKorisnika(int curKorisnikId)
    {
        // da li dati korisnik ima audio snimak sa tim imenom
        // Pretplata.findByIDKor
        List<Pretplata> pretplate = em.createNamedQuery("Pretplata.findByIdKorisnik")
                .setParameter("idKorisnik", curKorisnikId)
                .getResultList();
        if (pretplate.isEmpty()) 
            return new Reply(-1, "KORISNIK NEMA PRETPLATE", null);
        
        // return value
        List<Pretplata> pretplataListReturn = new ArrayList<>();
        for(Pretplata p : pretplate)
            p.getIdPaket().setPretplataList(null);
        
        
        return new Reply(0, "DOHVACENE SVE PRETPLATE", pretplate);
    }
    
    //zahtev 25
    private static Reply dohvatiSlusanjaZaSnimak(String nazivSnimka, String imeVlasnika)
    {
        List<Korisnik> vlasnici = empodsistem1.createNamedQuery("Korisnik.findByIme").setParameter("ime", imeVlasnika).getResultList();
        if(vlasnici.isEmpty())
            return new Reply(-1, "NE POSTOJI VLASNIK SNIMKA: " + imeVlasnika, null);
        Korisnik vlasnik = vlasnici.get(0);
        
        // da li dati vlasnik ima audio snimak sa tim imenom
        // Audio.findByIDKorNaziv
        List<Audio> snimci = empodsistem2.createQuery("SELECT a FROM Audio a WHERE a.naziv = :naziv and a.idKorisnik = :idKorisnik")
                .setParameter("idKorisnik", vlasnik.getIdKorisnik())
                .setParameter("naziv", nazivSnimka)
                .getResultList();
        if (snimci.isEmpty()) 
            return new Reply(-1, "ZADATI VLASNIK NEMA AUDIO SNIMAK SA NAZIVOM: " + nazivSnimka, null);
        Audio a = snimci.get(0);
        
        List<Slusanje> slusanja = em.createNamedQuery("Slusanje.findByIdAudio")
                .setParameter("idAudio", a.getIdAudio())
                .getResultList();
        if (slusanja.isEmpty()) 
            return new Reply(-1, "AUDIO SNIMAK NEMA NIJEDNO SLUSANJE", null);

        return new Reply(0, "DOHVACENA SVA SLUSANJA ZA SNIMAK: " + nazivSnimka + ", VLASNIKA: " + imeVlasnika, slusanja);
    }
    
    //zahtev 26
    private static Reply dohvatiOceneZaSnimak(String nazivSnimka, String imeVlasnika)
    {
        List<Korisnik> vlasnici = empodsistem1.createNamedQuery("Korisnik.findByIme").setParameter("ime", imeVlasnika).getResultList();
        if(vlasnici.isEmpty())
            return new Reply(-1, "NE POSTOJI VLASNIK SNIMKA: " + imeVlasnika, null);
        Korisnik vlasnik = vlasnici.get(0);
        
        // da li dati vlasnik ima audio snimak sa tim imenom
        // Audio.findByIDKorNaziv
        List<Audio> snimci = empodsistem2.createQuery("SELECT a FROM Audio a WHERE a.naziv = :naziv and a.idKorisnik = :idKorisnik")
                .setParameter("idKorisnik", vlasnik.getIdKorisnik())
                .setParameter("naziv", nazivSnimka)
                .getResultList();
        if (snimci.isEmpty()) 
            return new Reply(-1, "ZADATI VLASNIK NEMA AUDIO SNIMAK SA NAZIVOM: " + nazivSnimka, null);
        Audio a = snimci.get(0);
        
        List<Ocena> ocene = em.createNamedQuery("Ocena.findByIdAudio")
                .setParameter("idAudio", a.getIdAudio())
                .getResultList();
        if (ocene.isEmpty()) 
            return new Reply(-1, "AUDIO NEMA NIJEDNU OCENU", null);

        return new Reply(0, "DOHVACENE SVE OCENE ZA SNIMAK: " + nazivSnimka + ", VLASNIKA: " + imeVlasnika, ocene);
    }
    
    //zahtev 27
    private static Reply dohvatiOmiljene(int curKorisnikId)
    {
        List<Integer> omiljeniAudioIds = em
        .createQuery("SELECT o.omiljeniSnimciPK.idAudio FROM OmiljeniSnimci o WHERE o.omiljeniSnimciPK.idKorisnik = :idKorisnik", Integer.class)
        .setParameter("idKorisnik", curKorisnikId) 
        .getResultList();
        
        if (omiljeniAudioIds.isEmpty())
        {
            return new Reply(-1, "NEMA OMILJENIH SNIMAKA", null);
        }
        
        List<Audio> omiljeniAudio = empodsistem2
        .createQuery("SELECT a FROM Audio a WHERE a.idAudio IN :idAudioList", Audio.class)
        .setParameter("idAudioList", omiljeniAudioIds)
        .getResultList();

        for(Audio a : omiljeniAudio)
        {
            for(Kategorija k : a.getKategorijaList())
            {
                k.setAudioList(null);
            }
        }
        System.out.println(omiljeniAudio);
        return new Reply(0, "DOHVACENA LISTA OMILJENIH AUDIO SNIMAKA", omiljeniAudio);  
    }
    
    
    public static void main(String[] args) {
        System.out.println("Podsistem3 pokrenut...");
        
        if(context == null || consumer == null || producer == null){
            context = connectionFactory.createContext();
            consumer=context.createConsumer(topic, "id=3");
            producer = context.createProducer();
        }
        
        ObjectMessage objMsgSend = context.createObjectMessage();
        
        while (true) {
            
            try {
                ID_SEND = 0;
                System.out.println("Podsistem3 ceka na zahtev...");
                
                ObjectMessage objMsg = (ObjectMessage) consumer.receive();      // blocking call
                Request request = (Request) objMsg.getObject();
                System.out.println("Podsistem3 primio zahtev...");
                Reply reply;
                
                switch (request.getIdZahteva()) {
                    
                    case KREIRAJ_PAKET:
                        System.out.println("Zahtev od servera za kreiranje paketa...");
                        String naziv = (String) request.getParametri().get(0);
                        String cena = (String) request.getParametri().get(1);
                        reply = kreirajPaket(naziv, cena);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case PROMENA_CENE_PAKETA:
                        System.out.println("Zahtev od servera za promenu cene paketa...");
                        naziv = (String) request.getParametri().get(0);
                        cena = (String) request.getParametri().get(1);
                        reply = promeniCenu(naziv, cena);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case KREIRAJ_PRETPLATU:
                        System.out.println("Zahtev od servera za kreiranje pretplate...");
                        String nazivPaketa = (String) request.getParametri().get(0);
                        String datum = (String) request.getParametri().get(1);
                        int curKorisnikId = (int) request.getParametri().get(2);
                        reply = kreirajPretplatu(nazivPaketa, datum, curKorisnikId);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case KREIRAJ_SLUSANJE:
                        System.out.println("Zahtev od servera za kreiranje slusanja...");
                        ArrayList<Object> params = request.getParametri();
                        curKorisnikId = (int) params.get(0);
                        String nazivSnimka = (String) params.get(1);
                        String imeVlasnika = (String) params.get(2);
                        String datumPocetka = (String) params.get(3);
                        int sekundPocetka = (int) params.get(4);
                        int sekundOdslusano = (int) params.get(5);
                        reply = kreirajSlusanje(curKorisnikId, nazivSnimka, imeVlasnika, datumPocetka, sekundPocetka, sekundOdslusano);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case DODAJ_OMILJENI_SNIMAK:
                        System.out.println("Zahtev od servera za dodavanje audio snimka u listu omiljenih...");
                        params = request.getParametri();
                        curKorisnikId = (int) params.get(0);
                        nazivSnimka = (String) params.get(1);
                        imeVlasnika = (String) params.get(2);
                        reply = dodajOmiljene(curKorisnikId, nazivSnimka, imeVlasnika);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case KREIRAJ_OCENU:
                        System.out.println("Zahtev od servera za kreiranje ocene...");
                        params = request.getParametri();
                        curKorisnikId = (int) params.get(0);
                        nazivSnimka = (String) params.get(1);
                        imeVlasnika = (String) params.get(2);
                        int ocena = (int) params.get(3);
                        datum = (String) params.get(4);
                        reply = kreirajOcenu(curKorisnikId, nazivSnimka, imeVlasnika, ocena, datum);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case PROMENA_OCENE:
                        System.out.println("Zahtev od servera za promene ocene audio snimku...");
                        params = request.getParametri();
                        curKorisnikId = (int) params.get(0);
                        nazivSnimka = (String) params.get(1);
                        imeVlasnika = (String) params.get(2);
                        ocena = (int) params.get(3);
                        datum = (String) params.get(4);
                        reply = promeniOcenu(curKorisnikId, nazivSnimka, imeVlasnika, ocena, datum);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case BRISANJE_OCENE:
                        System.out.println("Zahtev od servera za brisanje ocene audio snimku...");
                        params = request.getParametri();
                        curKorisnikId = (int) params.get(0);
                        nazivSnimka = (String) params.get(1);
                        imeVlasnika = (String) params.get(2);
                        reply = obrisiOcenu(curKorisnikId, nazivSnimka, imeVlasnika);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case DOHVATI_PAKETE:
                        System.out.println("Zahtev od servera za dohvatanje svih paketa...");
                        reply = dohvatiPakete();
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case DOHVATI_PRETPLATE:
                        System.out.println("Zahtev od servera za dohvatanje svih pretplata za ulogovanog korisnika...");
                        curKorisnikId = (int) request.getParametri().get(0);
                        reply = dohvatiPretplateZaKorisnika(curKorisnikId);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case DOHVATI_SLUSANJA_SNIMKA:
                        System.out.println("Zahtev od servera za dohvatanje svih slusanja za zadati audio snimak...");
                        nazivSnimka = (String) request.getParametri().get(0);
                        imeVlasnika = (String) request.getParametri().get(1);
                        reply = dohvatiSlusanjaZaSnimak(nazivSnimka, imeVlasnika);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case DOHVATI_OCENE_SNIMKA:
                        System.out.println("Zahtev od servera za dohvatanje svih ocena za zadati audio snimak...");
                        nazivSnimka = (String) request.getParametri().get(0);
                        imeVlasnika = (String) request.getParametri().get(1);
                        reply = dohvatiOceneZaSnimak(nazivSnimka, imeVlasnika);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case DOHVATI_OMILJENE_SNIMKE:
                        System.out.println("Zahtev od servera za dohvatanje liste omiljenih audio snimaka...");
                        curKorisnikId = (int) request.getParametri().get(0);
                        reply = dohvatiOmiljene(curKorisnikId);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                }
                
                objMsgSend.setIntProperty("id", ID_SEND);       // salje odgovor serveru
                producer.send(topic, objMsgSend);
                System.out.println("Poslao odgovor...");
                System.out.println("------------------------------------------------------------------------");
                
            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
}
