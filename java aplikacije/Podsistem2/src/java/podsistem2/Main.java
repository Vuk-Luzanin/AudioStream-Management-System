
package podsistem2;

import entities.Audio;
import entities.Kategorija;
import entities.Korisnik;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    // for podsistem2 database
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("Podsistem2PU");
    private static EntityManager em = emf.createEntityManager();
    
    // for podsistem1 database
    private static EntityManagerFactory emfpodsistem1 = Persistence.createEntityManagerFactory("Podsistem1PU");
    private static EntityManager empodsistem1 = emfpodsistem1.createEntityManager();
    
    
    private static final int KREIRAJ_KATEGORIJU = 5;
    private static final int KREIRAJ_AUDIO_SNIMAK = 6;
    private static final int PROMENA_NAZIVA_SNIMKA = 7;
    private static final int DODAJ_KATEGORIJU_SNIMKU = 8;
    private static final int BRISANJE_SNIMKA = 17;
    private static final int DOHVATI_KATEGORIJE = 20;
    private static final int DOHVATI_SNIMKE = 21;
    private static final int DOHVATI_KATEGORIJE_SNIMKA = 22;
    
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
    
    //zahtev 5
    private static Reply kreirajKategoriju(String nazivKategorije)
    {
        List<Kategorija> kategorije = em.createNamedQuery("Kategorija.findByNaziv").setParameter("naziv", nazivKategorije).getResultList();
        if(!kategorije.isEmpty())
            return new Reply(-1, "VEC POSTOJI KATEGORIJA: " + nazivKategorije, null);
        
        Kategorija k = new Kategorija();
        k.setNaziv(nazivKategorije);
        persistObject(k);
        return new Reply(0, "USPESNO KREIRANA KATEGORIJA: " + nazivKategorije, null);
    }
    
    //zahtev 6
    private static Reply kreirajAudio(String naziv, BigDecimal trajanje, int curKorisnikId, String datum)
    {
        Korisnik korisnik = empodsistem1.createNamedQuery("Korisnik.findByIdKorisnik", Korisnik.class)
                .setParameter("idKorisnik", curKorisnikId)
                .getSingleResult();
        
        // da li dati korisnik vec ima audio snimak sa tim imenom
        // Audio.findByIDKorNaziv
        List<Audio> snimci = em.createQuery("SELECT a FROM Audio a WHERE a.naziv = :naziv and a.idKorisnik = :idKorisnik")
                .setParameter("idKorisnik", curKorisnikId)
                .setParameter("naziv", naziv)
                .getResultList();
        if (!snimci.isEmpty()) 
            return new Reply(-1, "KORISNIK VEC IMA AUDIO SNIMAK SA NAZIVOM: " + naziv, null);
        
        BigDecimal zero = new BigDecimal("0");
        if (trajanje.compareTo(zero) < 0) 
        {
            return new Reply(-1, "TRAJANJE NE MOZE BITI NEGATIVNO", null);
        }
        
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
        
        Audio a = new Audio();
        a.setNaziv(naziv);
        a.setTrajanje(trajanje);
        a.setIdKorisnik(curKorisnikId);
        a.setDatumPostavljanja(datumPostavljanja);
        persistObject(a);
        return new Reply(0, "USPESNO KREIRAN AUDIO SNIMAK: " + naziv, null);
    }
    
    //zahtev 7
    private static Reply promeniNaziv(String naziv, int curKorisnikId, String noviNaziv)
    {
        Korisnik korisnik = empodsistem1.createNamedQuery("Korisnik.findByIdKorisnik", Korisnik.class)
                .setParameter("idKorisnik", curKorisnikId)
                .getSingleResult();
        
        // da li dati korisnik ima audio snimak sa tim imenom
        // Audio.findByIDKorNaziv
        List<Audio> snimci = em.createQuery("SELECT a FROM Audio a WHERE a.naziv = :naziv and a.idKorisnik = :idKorisnik")
                .setParameter("idKorisnik", curKorisnikId)
                .setParameter("naziv", naziv)
                .getResultList();
        if (snimci.isEmpty()) 
            return new Reply(-1, "KORISNIK NEMA AUDIO SNIMAK SA NAZIVOM: " + naziv, null);
        Audio a = snimci.get(0);
        
        em.getTransaction().begin();
        a.setNaziv(noviNaziv);
        em.flush();
        em.getTransaction().commit();
        return new Reply(0, "USPESNO PROMENJEN NAZIV SNIMKU: " + naziv, null);
    }
    
    //zahtev 8
    private static Reply dodajKategorijuSnimku(String naziv, int curKorisnikId, String nazivKategorije)
    {
        Korisnik korisnik = empodsistem1.createNamedQuery("Korisnik.findByIdKorisnik", Korisnik.class)
                .setParameter("idKorisnik", curKorisnikId)
                .getSingleResult();
        
        // da li dati korisnik ima audio snimak sa tim imenom
        // Audio.findByIDKorNaziv
        List<Audio> snimci = em.createQuery("SELECT a FROM Audio a WHERE a.naziv = :naziv and a.idKorisnik = :idKorisnik")
                .setParameter("idKorisnik", curKorisnikId)
                .setParameter("naziv", naziv)
                .getResultList();
        if (snimci.isEmpty()) 
            return new Reply(-1, "KORISNIK NEMA AUDIO SNIMAK SA NAZIVOM: " + naziv, null);
        Audio a = snimci.get(0);
        
        List<Kategorija> sveKategorije = em.createNamedQuery("Kategorija.findByNaziv")
                .setParameter("naziv", nazivKategorije)
                .getResultList();
        if(sveKategorije.isEmpty())
            return new Reply(-1, "NE POSTOJI KATEGORIJA: " + nazivKategorije, null);
        Kategorija k = sveKategorije.get(0);
        
        // dohvati kategorije za dati snimak
        if (a.getKategorijaList().contains(k)) {
            return new Reply(-1, "AUDIO SNIMAK VEĆ POSEDUJE KATEGORIJU: " + nazivKategorije, null);
        }

        em.getTransaction().begin();
        if (k.getAudioList() == null) {
            k.setAudioList(new ArrayList<>());
        }
        if (a.getKategorijaList() == null) {
            a.setKategorijaList(new ArrayList<>());
        }
        a.getKategorijaList().add(k);
        k.getAudioList().add(a);
        em.flush();
        em.getTransaction().commit();
        return new Reply(0, "USPESNO DODANA KATEGORIJA SNIMKU: " + naziv, null);
    }
    
    //zahtev 17
    private static Reply obrisiSnimak(String naziv, int curKorisnikId)
    {
        Korisnik korisnik = empodsistem1.createNamedQuery("Korisnik.findByIdKorisnik", Korisnik.class)
                .setParameter("idKorisnik", curKorisnikId)
                .getSingleResult();               // korisnik koji zeli da obrise snimak - trentno ulogovan
        
        // da li ulogovani korisnik ima audio snimak sa tim imenom
        // Audio.findByIDKorNaziv
        List<Audio> snimci = em.createQuery("SELECT a FROM Audio a WHERE a.naziv = :naziv and a.idKorisnik = :idKorisnik")
                .setParameter("idKorisnik", curKorisnikId)
                .setParameter("naziv", naziv)
                .getResultList();
        if (snimci.isEmpty()) 
            return new Reply(-1, "ULOGOVANI KORISNIK NEMA AUDIO SNIMAK SA NAZIVOM: " + naziv, null);
        Audio a = snimci.get(0);
        
        removeObject(a);
        return new Reply(0, "USPESNO OBRISAM SNIMAK: " + naziv + ", VLASNIKA: " + korisnik.getIme(), null);
    }
  
    //zahtev 20
    private static List<Kategorija> dohvatiKategorije()
    {
        List<Kategorija> kategorije =  em.createNamedQuery("Kategorija.findAll").getResultList();
        for(Kategorija k : kategorije)
            k.setAudioList(null);
        return kategorije;
    }
    
    //zahtev 21
    private static List<Audio> dohvatiSnimke()
    {
        List<Audio> snimci =  em.createNamedQuery("Audio.findAll").getResultList();
        for(Audio a : snimci)
        {
            for(Kategorija k : a.getKategorijaList())
            {
                k.setAudioList(null);
            }
        }
        return snimci;
    }
    
    //zahtev 22
    private static Reply dohvatiKategorijeZaSnimak(String naziv, String imeKorisnika)
    {
        List<Korisnik> korisnici = empodsistem1.createNamedQuery("Korisnik.findByIme").setParameter("ime", imeKorisnika).getResultList();
        if(korisnici.isEmpty())
            return new Reply(-1, "NE POSTOJI KORISNIK: " + imeKorisnika, null);
        Korisnik korisnik = korisnici.get(0);
        int idKorisnik = korisnik.getIdKorisnik();
        
        // da li dati korisnik ima audio snimak sa tim imenom
        // Audio.findByIDKorNaziv
        List<Audio> snimci = em.createQuery("SELECT a FROM Audio a WHERE a.naziv = :naziv and a.idKorisnik = :idKorisnik")
                .setParameter("idKorisnik", idKorisnik)
                .setParameter("naziv", naziv)
                .getResultList();
        if (snimci.isEmpty()) 
            return new Reply(-1, "KORISNIK NEMA AUDIO SNIMAK SA NAZIVOM: " + naziv, null);
        Audio a = snimci.get(0);
        
        List<Kategorija> kategorije = em.createQuery(
        "SELECT k FROM Kategorija k JOIN k.audioList a WHERE a.idAudio = :idAudio", Kategorija.class).setParameter("idAudio", a.getIdAudio()).getResultList();
        if (kategorije.isEmpty()) 
            return new Reply(-1, "NE POSTOJE KATEGORIJE ZA AUDIO SNIMAK: " + naziv, null);
        
        for(Kategorija k : kategorije)
            k.setAudioList(null);
        return new Reply(-1, "DOHVACENE SVE KATEGORIJE ZA AUDIO SNIMAK: " + naziv, kategorije);
    }
    
    
    public static void main(String[] args) {
        System.out.println("Podsistem2 pokrenut...");
        
        if(context == null || consumer == null || producer == null){
            context = connectionFactory.createContext();
            consumer=context.createConsumer(topic, "id=2");
            producer = context.createProducer();
        }
        
        ObjectMessage objMsgSend = context.createObjectMessage();
        
        while (true) {
            
            try {
                ID_SEND = 0;
                System.out.println("Podsistem2 ceka na zahtev...");
                
                ObjectMessage objMsg = (ObjectMessage) consumer.receive();      // blocking call
                Request request = (Request) objMsg.getObject();
                System.out.println("Podsistem2 primio zahtev...");
                Reply reply;
                
                switch (request.getIdZahteva()) {
                    case KREIRAJ_KATEGORIJU:
                        System.out.println("Zahtev od servera za kreiranje kategorije...");
                        String nazivKategorije = (String) request.getParametri().get(0);
                        reply = kreirajKategoriju(nazivKategorije);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case KREIRAJ_AUDIO_SNIMAK:
                        System.out.println("Zahtev od servera za kreiranje audio snimka...");
                        String naziv = (String) request.getParametri().get(0);
                        BigDecimal trajanje = new BigDecimal((String) request.getParametri().get(1));
                        int curKorisnikId = (int) request.getParametri().get(2);
                        String datum = (String) request.getParametri().get(3);
                        reply = kreirajAudio(naziv, trajanje, curKorisnikId, datum);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case PROMENA_NAZIVA_SNIMKA:
                        System.out.println("Zahtev od servera za promenu naziva audio snimka...");
                        naziv = (String) request.getParametri().get(0);
                        curKorisnikId = (int) request.getParametri().get(1);
                        String noviNaziv = (String) request.getParametri().get(2);
                        reply = promeniNaziv(naziv, curKorisnikId, noviNaziv);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case DODAJ_KATEGORIJU_SNIMKU:
                        System.out.println("Zahtev od servera za dodavanje kategorije snimku...");
                        naziv = (String) request.getParametri().get(0);
                        curKorisnikId = (int) request.getParametri().get(1);
                        nazivKategorije = (String) request.getParametri().get(2);
                        reply = dodajKategorijuSnimku(naziv, curKorisnikId, nazivKategorije);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case BRISANJE_SNIMKA:
                        System.out.println("Zahtev od servera za brisanje audio snimka od strane korisnika koji ga je postavio...");
                        naziv = (String) request.getParametri().get(0);
                        curKorisnikId = (int) request.getParametri().get(1);
                        reply = obrisiSnimak(naziv, curKorisnikId);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case DOHVATI_KATEGORIJE:
                        System.out.println("Zahtev od servera za dohvatanje svih kategorija...");
                        List<Kategorija> kategorije = dohvatiKategorije();
                        reply = new Reply(0, "DOHVACENE SVE KATEGORIJE", kategorije);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case DOHVATI_SNIMKE:
                        System.out.println("Zahtev od servera za dohvatanje svih snimaka...");
                        List<Audio> snimci = dohvatiSnimke();
                        reply = new Reply(0, "DOHVACENI SVI SNIMCI", snimci);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                    
                    case DOHVATI_KATEGORIJE_SNIMKA:
                        System.out.println("Zahtev od servera za dohvatanje svih kategorija za odredjeni audio snimak...");
                        naziv = (String) request.getParametri().get(0);
                        String imeKorisnika = (String) request.getParametri().get(1);
                        reply = dohvatiKategorijeZaSnimak(naziv, imeKorisnika);
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
