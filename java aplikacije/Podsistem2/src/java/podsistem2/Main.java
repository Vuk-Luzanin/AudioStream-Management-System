
package podsistem2;

import entities.Kategorija;
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
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("Podsistem2PU");
    private static EntityManager em = emf.createEntityManager();
    
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
