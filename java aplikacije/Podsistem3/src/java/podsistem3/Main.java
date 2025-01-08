
package podsistem3;

import entities.Paket;
import entities.Pretplata;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
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
import podsistem1Entities.Korisnik;


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
                        
                    case DOHVATI_PAKETE:
                        System.out.println("Zahtev od servera za dohvatanje svih paketa...");
                        reply = dohvatiPakete();
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case DOHVATI_PRETPLATE:
                        System.out.println("Zahtev od servera za dohvatanje svih pretplata...");
                        curKorisnikId = (int) request.getParametri().get(0);
                        reply = dohvatiPretplateZaKorisnika(curKorisnikId);
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
