
package podsistemi;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import komunikacija.Reply;
import komunikacija.Request;


@Path("podsistem2")
public class Podsistem2Resource {
    
    @Resource(lookup="projectConnFactory")
    private ConnectionFactory connectionFactory;
    
    @Resource(lookup="projectTopicServer")
    private Topic topic;
    
    JMSContext context = null;
    JMSConsumer consumer=null;
    JMSProducer producer = null;
    
    private static final int KREIRAJ_KATEGORIJU = 5;
    private static final int KREIRAJ_AUDIO_SNIMAK = 6;
    private static final int PROMENA_NAZIVA_SNIMKA = 7;
    private static final int DODAJ_KATEGORIJU_SNIMKU = 8;
    private static final int BRISANJE_SNIMKA = 17;
    private static final int DOHVATI_KATEGORIJE = 20;
    private static final int DOHVATI_SNIMKE = 21;
    private static final int DOHVATI_KATEGORIJE_SNIMKA = 22;
    
    private static final int PODSISTEM_ID = 2;
    
    
    private Response sendRequest(Request request) {
        try {
            if(context == null || consumer == null || producer == null){
                context = connectionFactory.createContext();
                consumer=context.createConsumer(topic, "id=0");
                producer = context.createProducer();
            }
            
            System.out.println("\nServer salje zahtev broj " + Integer.toString(request.getIdZahteva()) + " podsistemu2\n");
            
            ObjectMessage objMsg = context.createObjectMessage(request);
            objMsg.setIntProperty("id", PODSISTEM_ID);
            
            producer.send(topic, objMsg);
            
            System.out.println("Poslat zahtev broj " + Integer.toString(request.getIdZahteva()) + " podsistemu2\n");
            
            ObjectMessage objMsgRec = (ObjectMessage)consumer.receive();        // blocking call
            Reply reply = (Reply) objMsgRec.getObject();
            System.out.println("\nPrimio odgovor od podsistema 2\n");
            System.out.println(reply.getMessage());
            
            // close communication
            if(context != null || consumer != null || producer != null) {
                consumer.close();
                context.close();
                context = null;
            }
            
            System.out.println("------------------------------------------------------------------------");
            
            if(reply.getObject() == null)
                return Response.status(Response.Status.OK).entity(reply.getMessage()).build();
            else 
                return Response.status(Response.Status.OK).entity(reply.getObject()).build();
                        
        } catch (JMSException ex) {
            Logger.getLogger(Podsistem1Resource.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // reply didn't succeed
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("EXCEPTION").build();
    }
    
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)       // jer radi sa JMS koji nema transakcije
    @POST
    @Path("/zahtev5")
    public Response kreirajGrad(@QueryParam("nazivKategorije") String nazivKategorije) {
        Request request = new Request();
        request.setIdZahteva(KREIRAJ_KATEGORIJU);
        request.dodajParametar(nazivKategorije);
        return sendRequest(request);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)       // jer radi sa JMS koji nema transakcije
    @POST
    @Path("/zahtev6")
    public Response kreirajAudio(@QueryParam("naziv") String naziv, @QueryParam("trajanje") String trajanje,
            @QueryParam("imeKorisnika") String imeKorisnika, @QueryParam("datum") String datum) {
        Request request = new Request();
        request.setIdZahteva(KREIRAJ_AUDIO_SNIMAK);
        request.dodajParametar(naziv);
        request.dodajParametar(trajanje);
        request.dodajParametar(imeKorisnika);
        request.dodajParametar(datum);
        return sendRequest(request);
    }
}
