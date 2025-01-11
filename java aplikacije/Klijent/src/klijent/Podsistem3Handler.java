
package klijent;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Podsistem3Handler {
    
    private static String URL = "http://localhost:8080/Server/resources/podsistem3";        // current url
    private static String URL_START = URL;                                                  // saves start url
    private static int count = 0;                                                           // number of added parameters in url
    private static int OK = 200;                                                            // ok status
    
    public static String sendHttpRequest(String link, String method) {
        System.out.println("sendHttpRequest started...");
        System.out.println(link);
        System.out.println(method);
        try {
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);           // sets method
           
            int status = con.getResponseCode();     // calls server to get status code
            if (status == OK) 
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));        // otvaramo ulazni tok podataka sa servera, liniju po liniju
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);                  // dodajemo sve linije koje nam server salje
                }
                in.close();
                
                String response = content.toString();

                // REPLY FROM SERVER
//                System.out.println(response);

                // PARSE RESPONSE
                try {
                    // Check if response is a number
                    int r = Integer.parseInt(response);
                    System.out.println("Response is a number: " + r);
                } catch (NumberFormatException e) {
                    try {
                        // Try parsing the response as JSON
                        JsonElement jsonElement = JsonParser.parseString(response);

                        // Check if is a JSON array
                        if (jsonElement.isJsonArray()) {
                            Gson gson = new Gson();
                            JsonArray jsonArray = jsonElement.getAsJsonArray();
                            ArrayList<String> elements = new ArrayList<>();

                            for (JsonElement element : jsonArray) {
                                elements.add(gson.toJson(element));
                            }

                            for (String elem : elements) {
                                System.out.println(elem.replace("\"", ""));
                            }
                        } else {
                            System.out.println(response);
                        }
                    } catch (Exception jsonException) {
                        System.out.println(response);
                    }
                }    
                
                return response;
            } else {
                System.out.println("Failed to get response, status code: " + status + "\n");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        return "-1";
    }
    
    private static void dodajNaURL(String paramRest, String value) {
        try {
            if(count == 0)      
                URL = URL + paramRest + "=" + URLEncoder.encode(value, "UTF-8");        // enkoduje se u UTF-8, jer korisnik direktno unosi, pa da ne bi pogresio
            else
                // adds separator & for multiple params
                URL = URL + "&" + paramRest + "=" + URLEncoder.encode(value, "UTF-8");
            count++;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Podsistem1Handler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void unesiParametar(String nazivParam, String paramRest) {
        System.out.println("Unesite " + nazivParam + ": ");
        String s = null;
        Scanner in = new Scanner(System.in);
        s = in.nextLine();
        dodajNaURL(paramRest, s);
    }
    
    // one REST API request
    public static void zahtev9Handler() {
        URL = URL_START;
        URL = URL + "/zahtev9";
        URL = URL + "?";
        count = 0;
        
        unesiParametar("naziv paketa", "naziv");
        unesiParametar("cenu paketa", "cena");
        sendHttpRequest(URL, "POST");
    }
    
    public static void zahtev10Handler() {
        URL = URL_START;
        URL = URL + "/zahtev10";
        URL = URL + "?";
        count = 0;
        
        unesiParametar("naziv paketa", "naziv");
        unesiParametar("novu cenu paketa", "cena");
        sendHttpRequest(URL, "POST");
    }
    
    public static void zahtev11Handler(Integer curKorisnikId) {
        URL = URL_START;
        URL = URL + "/zahtev11";
        URL = URL + "?";
        count = 0;
        unesiParametar("naziv paketa", "nazivPaketa");
        
        System.out.println("Unesite datum i vreme postavljanja audio snimka (format: yyyy-MM-dd HH:mm:ss): ");
        Scanner in = new Scanner(System.in);
        String datumString = in.nextLine();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date datumPostavljanja = null;
        try
        {
            datumPostavljanja = dateFormat.parse(datumString);
            
        } catch (ParseException ex)
        {
            System.out.println("Datum nije u ispravnom formatu. Pokušajte ponovo.");
            return;
        }
        
        // date must be encoded
        String encodedDatumString = datumString;
        
        try
        {
            encodedDatumString = URLEncoder.encode(datumString, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(Klijent.class.getName()).log(Level.SEVERE, null, ex);
        }
        dodajNaURL("datum", encodedDatumString);
        
        dodajNaURL("curKorisnikId", curKorisnikId.toString());
        sendHttpRequest(URL, "POST");
    }
    
    public static void zahtev12Handler(Integer curKorisnikId) {
        URL = URL_START;
        URL = URL + "/zahtev12";
        URL = URL + "?";
        count = 0;
        
        dodajNaURL("curKorisnikId", curKorisnikId.toString());
        unesiParametar("naziv audio snimka", "nazivSnimka");
        unesiParametar("ime korisnika koji je vlasnik snimka", "imeVlasnika");
        
        System.out.println("Unesite datum i vreme pocetka slusanja snimka (format: yyyy-MM-dd HH:mm:ss): ");
        Scanner in = new Scanner(System.in);
        String datumString = in.nextLine();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date datumPostavljanja = null;
        try
        {
            datumPostavljanja = dateFormat.parse(datumString);
            
        } catch (ParseException ex)
        {
            System.out.println("Datum nije u ispravnom formatu. Pokušajte ponovo.");
            return;
        }
        
        // date must be encoded
        String encodedDatumString = datumString;
        
        try
        {
            encodedDatumString = URLEncoder.encode(datumString, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(Klijent.class.getName()).log(Level.SEVERE, null, ex);
        }
        dodajNaURL("datumPocetka", encodedDatumString);
        
        unesiParametar("sekund od koga je slusanje zapoceto", "sekundPocetka");
        unesiParametar("broj sekundi koliko je snimak slusan", "sekundOdslusano");        
        sendHttpRequest(URL, "POST");
    }
    
    public static void zahtev13Handler(Integer curKorisnikId) {
        URL = URL_START;
        URL = URL + "/zahtev13";
        URL = URL + "?";
        count = 0;
        
        dodajNaURL("curKorisnikId", curKorisnikId.toString());
        unesiParametar("naziv audio snimka", "nazivSnimka");
        unesiParametar("ime korisnika koji je vlasnik snimka", "imeVlasnika");
        sendHttpRequest(URL, "POST");
    }
    
    public static void zahtev14Handler(Integer curKorisnikId) {
        URL = URL_START;
        URL = URL + "/zahtev14";
        URL = URL + "?";
        count = 0;
        
        dodajNaURL("curKorisnikId", curKorisnikId.toString());
        unesiParametar("naziv audio snimka", "nazivSnimka");
        unesiParametar("ime korisnika koji je vlasnik snimka", "imeVlasnika");
        unesiParametar("ocenu za audio snimak (OCENA JE OD 1 DO 5)", "ocena");
        
        System.out.println("Unesite datum i vreme pocetka slusanja snimka (format: yyyy-MM-dd HH:mm:ss): ");
        Scanner in = new Scanner(System.in);
        String datumString = in.nextLine();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date datumPostavljanja = null;
        try
        {
            datumPostavljanja = dateFormat.parse(datumString);
            
        } catch (ParseException ex)
        {
            System.out.println("Datum nije u ispravnom formatu. Pokušajte ponovo.");
            return;
        }
        
        // date must be encoded
        String encodedDatumString = datumString;
        
        try
        {
            encodedDatumString = URLEncoder.encode(datumString, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(Klijent.class.getName()).log(Level.SEVERE, null, ex);
        }
        dodajNaURL("datum", encodedDatumString);
        
        sendHttpRequest(URL, "POST");
    }
    
    public static void zahtev15Handler(Integer curKorisnikId) {
        URL = URL_START;
        URL = URL + "/zahtev15";
        URL = URL + "?";
        count = 0;
        
        dodajNaURL("curKorisnikId", curKorisnikId.toString());
        unesiParametar("naziv audio snimka", "nazivSnimka");
        unesiParametar("ime korisnika koji je vlasnik snimka", "imeVlasnika");
        unesiParametar("NOVU ocenu audio snimka (OCENA JE OD 1 DO 5)", "ocena");
        
        System.out.println("Unesite datum i vreme pocetka slusanja snimka (format: yyyy-MM-dd HH:mm:ss): ");
        Scanner in = new Scanner(System.in);
        String datumString = in.nextLine();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date datumPostavljanja = null;
        try
        {
            datumPostavljanja = dateFormat.parse(datumString);
            
        } catch (ParseException ex)
        {
            System.out.println("Datum nije u ispravnom formatu. Pokušajte ponovo.");
            return;
        }
        
        // date must be encoded
        String encodedDatumString = datumString;
        
        try
        {
            encodedDatumString = URLEncoder.encode(datumString, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(Klijent.class.getName()).log(Level.SEVERE, null, ex);
        }
        dodajNaURL("datum", encodedDatumString);
        
        sendHttpRequest(URL, "POST");
    }
    
    public static void zahtev16Handler(Integer curKorisnikId) {
        URL = URL_START;
        URL = URL + "/zahtev16";
        URL = URL + "?";
        count = 0;
        
        dodajNaURL("curKorisnikId", curKorisnikId.toString());
        unesiParametar("naziv audio snimka", "nazivSnimka");
        unesiParametar("ime korisnika koji je vlasnik snimka", "imeVlasnika");
        sendHttpRequest(URL, "POST");
    }
    
    public static void zahtev23Handler() {
        URL = URL_START;
        URL = URL + "/zahtev23";
//        URL = URL + "?";
        count = 0;
        
        sendHttpRequest(URL, "GET");
    }
    
    public static void zahtev24Handler(Integer curKorisnikId) {
        URL = URL_START;
        URL = URL + "/zahtev24";
        URL = URL + "?";
        count = 0;
        
        dodajNaURL("curKorisnikId", curKorisnikId.toString());
        sendHttpRequest(URL, "GET");
    }
    public static void zahtev25Handler() {
        URL = URL_START;
        URL = URL + "/zahtev25";
        URL = URL + "?";
        count = 0;
        
        unesiParametar("naziv audio snimka", "nazivSnimka");
        unesiParametar("ime korisnika koji je vlasnik snimka", "imeVlasnika");
        sendHttpRequest(URL, "GET");
    }
    
    public static void zahtev26Handler() {
        URL = URL_START;
        URL = URL + "/zahtev26";
        URL = URL + "?";
        count = 0;
        
        unesiParametar("naziv audio snimka", "nazivSnimka");
        unesiParametar("ime korisnika koji je vlasnik snimka", "imeVlasnika");
        sendHttpRequest(URL, "GET");
    }
    
    public static void zahtev27Handler(Integer curKorisnikId) {
        URL = URL_START;
        URL = URL + "/zahtev27";
        URL = URL + "?";
        count = 0;
        
        dodajNaURL("curKorisnikId", curKorisnikId.toString());
        sendHttpRequest(URL, "GET");
    }
}
