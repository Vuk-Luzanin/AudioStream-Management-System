
package podsistem1;

import entities.Korisnik;
import entities.Mesto;
import java.util.ArrayList;
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
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("Podsistem1PU");
    private static EntityManager em = emf.createEntityManager();
    
    private static final int KREIRAJ_GRAD = 1;
    private static final int KREIRAJ_KORISNIKA = 2;
    private static final int PROMENA_EMAIL_ADRESE = 3;
    private static final int PROMENA_MESTA = 4;
    private static final int DOHVATI_MESTA = 18;
    private static final int DOHVATI_KORISNIKE = 19;
    
    private static int ID_SEND = 0;
    
    private static final int LOGIN = 100;
    
    
    private static void persistObject(Object o) {
        em.getTransaction().begin();
        em.persist(o);
        em.flush();
        em.getTransaction().commit();
    }
    
    //zahtev 1
    private static Reply kreirajGrad(String naziv)
    {
        List<Mesto> gradovi = em.createNamedQuery("Mesto.findByNaziv").setParameter("naziv", naziv).getResultList();
        if(!gradovi.isEmpty())
            return new Reply(-1, "VEC POSTOJI MESTO: " + naziv, null);
        
        Mesto g = new Mesto(0, naziv);
        persistObject(g);
        return new Reply(0, "USPESNO KREIRANO MESTO: " + naziv, null);
    }
    
    //zahtev 2
    private static Reply kreirajKorisnika(String imeKorisnika, String email, int godiste, String pol, String nazivMesta, String sifra)
    {
        List<Mesto> mesta = em.createNamedQuery("Mesto.findByNaziv").setParameter("naziv", nazivMesta).getResultList();
        if(mesta.isEmpty())
            return new Reply(-1, "NE POSTOJI MESTO: " + nazivMesta, null);
        Mesto m = mesta.get(0);
        
        List<Korisnik> korisnici = em.createNamedQuery("Korisnik.findByIme").setParameter("ime", imeKorisnika).getResultList();
        if(!korisnici.isEmpty())
            return new Reply(-1, "VEC POSTOJI KORISNIK: " + imeKorisnika, null);
        
        Korisnik korisnik = new Korisnik();
        korisnik.setIme(imeKorisnika); korisnik.setEmail(email); korisnik.setGodiste(godiste); 
        korisnik.setPol(pol.charAt(0)); korisnik.setIdMesto(m); korisnik.setSifra(sifra);
        persistObject(korisnik);
        return new Reply(0, "USPESNO KREIRAN KORISNIK: " + imeKorisnika, null);  
    }
    
    //zahtev 3
    private static Reply updateEmail(int curKorisnikId, String email)
    {
        Korisnik korisnik = em.createNamedQuery("Korisnik.findByIdKorisnik", Korisnik.class)
                .setParameter("idKorisnik", curKorisnikId)
                .getSingleResult();

        em.getTransaction().begin();
        korisnik.setEmail(email);
        em.flush();
        em.getTransaction().commit();
        return new Reply(0, "USPESNO POSTAVLJENA EMAIL ADRESA KORISNIKU: " + korisnik.getIme(), null);  
    }
    
    //zahtev 4
    private static Reply updateMesto(int curKorisnikId, String nazivMesta)
    {
        Korisnik korisnik = em.createNamedQuery("Korisnik.findByIdKorisnik", Korisnik.class)
                .setParameter("idKorisnik", curKorisnikId)
                .getSingleResult();
        
        List<Mesto> mesta = em.createNamedQuery("Mesto.findByNaziv")
                .setParameter("naziv", nazivMesta)
                .getResultList();
        if(mesta.isEmpty())
            return new Reply(-1, "NE POSTOJI MESTO: " + nazivMesta, null);
        Mesto m = mesta.get(0);

        em.getTransaction().begin();
        korisnik.setIdMesto(m);
        em.flush();
        em.getTransaction().commit();
        return new Reply(0, "USPESNO POSTAVLJENO MESTO KORISNIKU: " + korisnik.getIme(), null);  
    }
    
    //zahtev 18
    private static List<Mesto> getSvaMesta()
    {
        List<Mesto> mesta =  em.createNamedQuery("Mesto.findAll").getResultList();
        for(Mesto m : mesta)
            m.setKorisnikList(null);
        return mesta;
    }
    
    //zahtev 19
    private static List<Korisnik> getSviKorisnici()
    {
        List<Korisnik> korisnici =  em.createNamedQuery("Korisnik.findAll").getResultList();
        for(Korisnik k : korisnici)
            k.getIdMesto().setKorisnikList(null);
        return korisnici;
    }

    // zahtevLogin
    private static Reply login(String imeKorisnika, String sifra)
    {
        List<Korisnik> korisnici = em.createNamedQuery("Korisnik.findByIme").setParameter("ime", imeKorisnika).getResultList();
        if(korisnici.isEmpty())
            return new Reply(-1, "-1", null);
        Korisnik k = korisnici.get(0);
        
        if(!k.getSifra().equals(sifra))
            return new Reply(-1, "-1", null);
        
        return new Reply(0, Integer.toString(k.getIdKorisnik()), null);
    }
    
    public static void main(String[] args) {
        System.out.println("Podsistem1 pokrenut...");
        
        if(context == null || consumer == null || producer == null){
            context = connectionFactory.createContext();
            consumer=context.createConsumer(topic, "id=1");
            producer = context.createProducer();
        }
        
        ObjectMessage objMsgSend = context.createObjectMessage();
        
        while (true) {
            
            try {
                ID_SEND = 0;
                System.out.println("Podsistem1 ceka na zahtev...");
                
                ObjectMessage objMsg = (ObjectMessage) consumer.receive();      // blocking call
                Request request = (Request) objMsg.getObject();
                System.out.println("Podsistem1 primio zahtev...");
                Reply reply;
                
                switch (request.getIdZahteva()) {
                    case KREIRAJ_GRAD:
                        System.out.println("Zahtev od servera za kreiranje grada...");
                        String nazivGrada = (String)request.getParametri().get(0);
                        reply = kreirajGrad(nazivGrada);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case KREIRAJ_KORISNIKA:
                        System.out.println("Zahtev od servera za kreiranje korisnika...");
                        ArrayList<Object> params = request.getParametri();
                        String imeKorisnika = (String) params.get(0);
                        String email        = (String) params.get(1);
                        int godiste         = (int) params.get(2);
                        String pol          = (String) params.get(3);
                        String nazivMesta   = (String) params.get(4);
                        String sifra   = (String) params.get(5);
                        reply = kreirajKorisnika(imeKorisnika, email, godiste, pol, nazivMesta, sifra);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case PROMENA_EMAIL_ADRESE:
                        System.out.println("Zahtev od servera za promenu email adrese korisnika...");
                        params = request.getParametri();
                        int curKorisnikId    = (int) params.get(0);
                        email           = (String) params.get(1);
                        reply = updateEmail(curKorisnikId, email);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case PROMENA_MESTA:
                        System.out.println("Zahtev od servera za promenu mesta korisnika...");
                        params = request.getParametri();
                        curKorisnikId    = (int) params.get(0);
                        nazivMesta      = (String) params.get(1);
                        reply = updateMesto(curKorisnikId, nazivMesta);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case DOHVATI_MESTA:
                        System.out.println("Zahtev od servera za dohvatanje svih mesta...");
                        List<Mesto> mesta = getSvaMesta();
                        reply = new Reply(0, "DOHVACENA SVA MESTA", mesta);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case DOHVATI_KORISNIKE:
                        System.out.println("Zahtev od servera za dohvatanje svih korisnika...");
                        List<Korisnik> korisnici = getSviKorisnici();
                        reply = new Reply(0, "DOHVACENI SVI KORISNICI", korisnici);
                        objMsgSend.setObject(reply);
                        System.out.println("Obradjen zahtev...");
                        break;
                        
                    case LOGIN:
                        System.out.println("Zahtev od servera za login...");
                        params = request.getParametri();
                        imeKorisnika = (String)params.get(0);
                        sifra = (String)params.get(1);
                        reply = login(imeKorisnika, sifra);
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
