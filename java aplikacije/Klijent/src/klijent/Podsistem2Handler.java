
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


public class Podsistem2Handler {
    
    private static String URL = "http://localhost:8080/Server/resources/podsistem2";        // current url
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
    public static void zahtev5Handler() {
        URL = URL_START;
        URL = URL + "/zahtev5";
        URL = URL + "?";
        count = 0;
        
        unesiParametar("naziv kategorije", "nazivKategorije");
        sendHttpRequest(URL, "POST");
    }
    
    public static void zahtev6Handler(Integer curKorisnikId) {
        URL = URL_START;
        URL = URL + "/zahtev6";
        URL = URL + "?";
        count = 0;
        
        unesiParametar("naziv audio snimka", "naziv");
        unesiParametar("trajanje audio snimka u minutima (primer: 3.4)", "trajanje");
        dodajNaURL("curKorisnikId", curKorisnikId.toString());
        
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
        
        sendHttpRequest(URL, "POST");
    }
    
    // one REST API request
    public static void zahtev7Handler(Integer curKorisnikId) {
        URL = URL_START;
        URL = URL + "/zahtev7";
        URL = URL + "?";
        count = 0;
        
        unesiParametar("naziv audio snimka", "naziv");
        dodajNaURL("curKorisnikId", curKorisnikId.toString());
        unesiParametar("NOVI naziv audio snimka", "noviNaziv");
        sendHttpRequest(URL, "POST");
    }
    
    public static void zahtev8Handler(Integer curKorisnikId) {
        URL = URL_START;
        URL = URL + "/zahtev8";
        URL = URL + "?";
        count = 0;
        
        unesiParametar("naziv audio snimka", "naziv");
        dodajNaURL("curKorisnikId", curKorisnikId.toString());
        unesiParametar("naziv kategorije", "nazivKategorije");
        sendHttpRequest(URL, "POST");
    }
    
    // curKorisnikId - trenutno ulogovani korisnik
    public static void zahtev17Handler(Integer curKorisnikId) {
        URL = URL_START;
        URL = URL + "/zahtev17";
        URL = URL + "?";
        count = 0;
        
        unesiParametar("naziv audio snimka", "naziv");
        dodajNaURL("curKorisnikId", curKorisnikId.toString());
        sendHttpRequest(URL, "POST");
    }
    
    public static void zahtev20Handler() {
        URL = URL_START;
        URL = URL + "/zahtev20";
//        URL = URL + "?";
        count = 0;
        
        sendHttpRequest(URL, "GET");
    }
    
    public static void zahtev21Handler() {
        URL = URL_START;
        URL = URL + "/zahtev21";
//        URL = URL + "?";
        count = 0;
        
        sendHttpRequest(URL, "GET");
    }
    
    // moze dohvatiti i ulogovani korisnik koji nije vlasnik snimka
    public static void zahtev22Handler() {
        URL = URL_START;
        URL = URL + "/zahtev22";
        URL = URL + "?";
        count = 0;
        
        unesiParametar("naziv audio snimka", "naziv");
        unesiParametar("naziv korisnika koji je vlasnik audio snimka", "imeKorisnika");
        sendHttpRequest(URL, "GET");
    }
}
