
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


@Path("podsistem3")
public class Podsistem3Resource {
    
    @Resource(lookup="projectConnFactory")
    private ConnectionFactory connectionFactory;
    
    @Resource(lookup="projectTopicServer")
    private Topic topic;
    
    JMSContext context = null;
    JMSConsumer consumer=null;
    JMSProducer producer = null;
    
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
    
    private static final int PODSISTEM_ID = 3;
    
    private Response sendRequest(Request request) {
        try {
            if(context == null || consumer == null || producer == null){
                context = connectionFactory.createContext();
                consumer=context.createConsumer(topic, "id=0");
                producer = context.createProducer();
            }
            
            System.out.println("\nServer salje zahtev broj " + Integer.toString(request.getIdZahteva()) + " podsistemu3\n");
            
            ObjectMessage objMsg = context.createObjectMessage(request);
            objMsg.setIntProperty("id", PODSISTEM_ID);
            
            producer.send(topic, objMsg);
            
            System.out.println("Poslat zahtev broj " + Integer.toString(request.getIdZahteva()) + " podsistemu3\n");
            
            ObjectMessage objMsgRec = (ObjectMessage)consumer.receive();        // blocking call
            Reply reply = (Reply) objMsgRec.getObject();
            System.out.println("\nPrimio odgovor od podsistema 3\n");
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
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)       // jer radi sa JMS koji nema transakcije (mozda radi i bez?!)
    @POST
    @Path("/zahtev9")
    public Response kreirajPaket(@QueryParam("naziv") String naziv, @QueryParam("cena") String cena) {
        Request request = new Request();
        request.setIdZahteva(KREIRAJ_PAKET);
        request.dodajParametar(naziv);
        request.dodajParametar(cena);
        return sendRequest(request);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)       // jer radi sa JMS koji nema transakcije (mozda radi i bez?!)
    @POST
    @Path("/zahtev10")
    public Response promeniCenu(@QueryParam("naziv") String naziv, @QueryParam("cena") String cena) {
        Request request = new Request();
        request.setIdZahteva(PROMENA_CENE_PAKETA);
        request.dodajParametar(naziv);
        request.dodajParametar(cena);
        return sendRequest(request);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)       // jer radi sa JMS koji nema transakcije (mozda radi i bez?!)
    @POST
    @Path("/zahtev11")
    public Response kreirajPretplatu(@QueryParam("nazivPaketa") String nazivPaketa, @QueryParam("datum") String datum,
            @QueryParam("curKorisnikId") int curKorisnikId) {
        Request request = new Request();
        request.setIdZahteva(KREIRAJ_PRETPLATU);
        request.dodajParametar(nazivPaketa);
        request.dodajParametar(datum);
        request.dodajParametar(curKorisnikId);
        return sendRequest(request);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)       // jer radi sa JMS koji nema transakcije (mozda radi i bez?!)
    @POST
    @Path("/zahtev12")
    public Response kreirajSlusanje(@QueryParam("curKorisnikId") int curKorisnikId, @QueryParam("nazivSnimka") String nazivSnimka,
            @QueryParam("imeVlasnika") String imeVlasnika, @QueryParam("datumPocetka") String datumPocetka,
            @QueryParam("sekundPocetka") int sekundPocetka, @QueryParam("sekundOdslusano") int sekundOdslusano) {
        Request request = new Request();
        request.setIdZahteva(KREIRAJ_SLUSANJE);
        request.dodajParametar(curKorisnikId);
        request.dodajParametar(nazivSnimka);
        request.dodajParametar(imeVlasnika);
        request.dodajParametar(datumPocetka);
        request.dodajParametar(sekundPocetka);
        request.dodajParametar(sekundOdslusano);
        return sendRequest(request);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)       // jer radi sa JMS koji nema transakcije (mozda radi i bez?!)
    @POST
    @Path("/zahtev13")
    public Response dodajOmiljene(@QueryParam("curKorisnikId") int curKorisnikId, @QueryParam("nazivSnimka") String nazivSnimka,
            @QueryParam("imeVlasnika") String imeVlasnika) {
        Request request = new Request();
        request.setIdZahteva(DODAJ_OMILJENI_SNIMAK);
        request.dodajParametar(curKorisnikId);
        request.dodajParametar(nazivSnimka);
        request.dodajParametar(imeVlasnika);
        return sendRequest(request);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)       // jer radi sa JMS koji nema transakcije (mozda radi i bez?!)
    @POST
    @Path("/zahtev14")
    public Response kreirajOcenu(@QueryParam("curKorisnikId") int curKorisnikId, @QueryParam("nazivSnimka") String nazivSnimka,
                                @QueryParam("imeVlasnika") String imeVlasnika, @QueryParam("ocena") int ocena, 
                                @QueryParam("datum") String datum) {
        Request request = new Request();
        request.setIdZahteva(KREIRAJ_OCENU);
        request.dodajParametar(curKorisnikId);
        request.dodajParametar(nazivSnimka);
        request.dodajParametar(imeVlasnika);
        request.dodajParametar(ocena);
        request.dodajParametar(datum);
        return sendRequest(request);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)       // jer radi sa JMS koji nema transakcije (mozda radi i bez?!)
    @POST
    @Path("/zahtev15")
    public Response promeniOcenu(@QueryParam("curKorisnikId") int curKorisnikId, @QueryParam("nazivSnimka") String nazivSnimka,
                                @QueryParam("imeVlasnika") String imeVlasnika, @QueryParam("ocena") int ocena,
                                @QueryParam("datum") String datum) {
        Request request = new Request();
        request.setIdZahteva(PROMENA_OCENE);
        request.dodajParametar(curKorisnikId);
        request.dodajParametar(nazivSnimka);
        request.dodajParametar(imeVlasnika);
        request.dodajParametar(ocena);
        request.dodajParametar(datum);
        return sendRequest(request);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)       // jer radi sa JMS koji nema transakcije (mozda radi i bez?!)
    @POST
    @Path("/zahtev16")
    public Response obrisiOcenu(@QueryParam("curKorisnikId") int curKorisnikId, @QueryParam("nazivSnimka") String nazivSnimka,
                                @QueryParam("imeVlasnika") String imeVlasnika) {
        Request request = new Request();
        request.setIdZahteva(BRISANJE_OCENE);
        request.dodajParametar(curKorisnikId);
        request.dodajParametar(nazivSnimka);
        request.dodajParametar(imeVlasnika);
        return sendRequest(request);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)       // jer radi sa JMS koji nema transakcije
    @GET
    @Path("/zahtev23")
    public Response dohvatiPakete() {
        Request request = new Request();
        request.setIdZahteva(DOHVATI_PAKETE);
        return sendRequest(request);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)       // jer radi sa JMS koji nema transakcije
    @GET
    @Path("/zahtev24")
    public Response dohvatiPretplateZaKorisnika(@QueryParam("curKorisnikId") int curKorisnikId) {
        Request request = new Request();
        request.setIdZahteva(DOHVATI_PRETPLATE);
        request.dodajParametar(curKorisnikId);
        return sendRequest(request);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)       // jer radi sa JMS koji nema transakcije
    @GET
    @Path("/zahtev25")
    public Response dohvatiSlusanjaZaSnimak(@QueryParam("nazivSnimka") String nazivSnimka, @QueryParam("imeVlasnika") String imeVlasnika) {
        Request request = new Request();
        request.setIdZahteva(DOHVATI_SLUSANJA_SNIMKA);
        request.dodajParametar(nazivSnimka);
        request.dodajParametar(imeVlasnika);
        return sendRequest(request);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)       // jer radi sa JMS koji nema transakcije
    @GET
    @Path("/zahtev26")
    public Response dohvatiOceneZaSnimak(@QueryParam("nazivSnimka") String nazivSnimka, @QueryParam("imeVlasnika") String imeVlasnika) {
        Request request = new Request();
        request.setIdZahteva(DOHVATI_OCENE_SNIMKA);
        request.dodajParametar(nazivSnimka);
        request.dodajParametar(imeVlasnika);
        return sendRequest(request);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)       // jer radi sa JMS koji nema transakcije
    @GET
    @Path("/zahtev27")
    public Response dohvatiOmiljene(@QueryParam("curKorisnikId") int curKorisnikId) {
        Request request = new Request();
        request.setIdZahteva(DOHVATI_OMILJENE_SNIMKE);
        request.dodajParametar(curKorisnikId);
        return sendRequest(request);
    }
}
