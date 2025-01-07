
package podsistem3;

import entities.Paket;
import java.math.BigDecimal;
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
    // curKorisnikId - TRENUTNO ULOGOVAN KORISNIK
    private static Reply kreirajPretplatu(String nazivPaketa, String datum, int curKorisnikId)
    { 
        // NASTAVITI
        return new Reply(0, "USPESNO KREIRANA PRETPLATA: " + nazivPaketa, null);
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
