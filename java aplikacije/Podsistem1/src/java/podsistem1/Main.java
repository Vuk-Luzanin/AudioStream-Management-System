
package podsistem1;

import entities.Mesto;
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
    
    private static int ID_SEND = 0;
    
    
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
            return new Reply(-1, "VEC POSTOJI GRAD SA ZADATIM NAZIVOM", null);
        
        Mesto g = new Mesto(0, naziv);
        persistObject(g);
        return new Reply(0, "USPESNO KREIRAN GRAD", null);
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
                
                switch (request.getIdZahteva()) {
                    case KREIRAJ_GRAD:
                        System.out.println("Zahtev od servera za kreiranje grada...");
                        String nazivGrada = (String)request.getParametri().get(0);
                        Reply reply = kreirajGrad(nazivGrada);
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
