
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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import komunikacija.Reply;
import komunikacija.Request;


@Path("podsistem1")
public class Podsistem1Resource {
    
    @Resource(lookup="projectConnFactory")
    private ConnectionFactory connectionFactory;
    
    @Resource(lookup="projectTopicServer")
    private Topic topic;
    
    JMSContext context = null;
    JMSConsumer consumer=null;
    JMSProducer producer = null;
    
    private static final int KREIRAJ_GRAD = 1;
    private static final int KREIRAJ_KORISNIKA = 2;
    private static final int PROMENA_EMAIL_ADRESE = 3;
    private static final int PROMENA_MESTA = 4;
    private static final int DOHVATI_MESTA = 18;
    private static final int DOHVATI_KORISNIKE = 19;
    
    private static final int PODSISTEM_ID = 1;
    
    private static final int LOGIN = 100;
    
    
    private Response sendRequest(Request request) {
        try {
            if(context == null || consumer == null || producer == null){
                context = connectionFactory.createContext();
                consumer=context.createConsumer(topic, "id=0");
                producer = context.createProducer();
            }
            
            System.out.println("\nServer salje zahtev broj " + Integer.toString(request.getIdZahteva()) + " podsistemu1\n");
            
            ObjectMessage objMsg = context.createObjectMessage(request);
            objMsg.setIntProperty("id", PODSISTEM_ID);
            
            producer.send(topic, objMsg);
            
            System.out.println("Poslat zahtev broj " + Integer.toString(request.getIdZahteva()) + " podsistemu1\n");
            
            ObjectMessage objMsgRec = (ObjectMessage)consumer.receive();        // blocking call
            Reply reply = (Reply) objMsgRec.getObject();
            System.out.println("\nPrimio odgovor od podsistema 1\n");
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
    @Path("/zahtev1")
    public Response kreirajGrad(@QueryParam("nazivGrada") String nazivGrada) {
        Request request = new Request();
        request.setIdZahteva(KREIRAJ_GRAD);
        request.dodajParametar(nazivGrada);
        return sendRequest(request);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)       // jer radi sa JMS koji nema transakcije
    @POST
    @Path("/zahtev2")
    public Response kreirajKorisnik(@QueryParam("imeKorisnika") String imeKorisnika, @QueryParam("email") String email,
            @QueryParam("godiste") int godiste, @QueryParam("pol") String pol, @QueryParam("mesto") String mesto,
            @QueryParam("sifra") String sifra) {
        Request request = new Request();
        request.setIdZahteva(KREIRAJ_KORISNIKA);
        request.dodajParametar(imeKorisnika);
        request.dodajParametar(email);
        request.dodajParametar(godiste);
        request.dodajParametar(pol);
        request.dodajParametar(mesto);
        request.dodajParametar(sifra);
        return sendRequest(request);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)       // jer radi sa JMS koji nema transakcije
    @POST
    @Path("/zahtev3")
    public Response updateEmail(@QueryParam("curKorisnikId") int curKorisnikId, @QueryParam("email") String email) {
        Request request = new Request();
        request.setIdZahteva(PROMENA_EMAIL_ADRESE);
        request.dodajParametar(curKorisnikId);
        request.dodajParametar(email);
        return sendRequest(request);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)       // jer radi sa JMS koji nema transakcije
    @POST
    @Path("/zahtev4")
    public Response updateMesto(@QueryParam("curKorisnikId") int curKorisnikId, @QueryParam("mesto") String mesto) {
        Request request = new Request();
        request.setIdZahteva(PROMENA_MESTA);
        request.dodajParametar(curKorisnikId);
        request.dodajParametar(mesto);
        return sendRequest(request);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)       // jer radi sa JMS koji nema transakcije
    @GET
    @Path("/zahtev18")
    public Response getSvaMesta() {
        Request request = new Request();
        request.setIdZahteva(DOHVATI_MESTA);
        return sendRequest(request);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)       // jer radi sa JMS koji nema transakcije
    @GET
    @Path("/zahtev19")
    public Response getSviKorisnici() {
        Request request = new Request();
        request.setIdZahteva(DOHVATI_KORISNIKE);
        return sendRequest(request);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @GET
    @Path("/zahtevLogin")
    public Response login(@QueryParam("imeKorisnika")String imeKorisnika, @QueryParam("sifra") String sifra)
    {
        Request request = new Request();
        request.setIdZahteva(LOGIN);
        request.dodajParametar(imeKorisnika);
        request.dodajParametar(sifra);
        return sendRequest(request);
    }
    
}
