
package klijent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.ArrayList;


public class Podsistem1Handler {
    
    private static String URL = "http://localhost:8080/Server/resources/podsistem1";        // current url
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
    public static void zahtev1Handler() {
        URL = URL_START;
        URL = URL + "/zahtev1";
        URL = URL + "?";
        count = 0;
        
        unesiParametar("naziv grada", "nazivGrada");
        sendHttpRequest(URL, "POST");
    }
    
    public static void zahtev2Handler() {
        URL = URL_START;
        URL = URL + "/zahtev2";
        URL = URL + "?";
        count = 0;
        
        unesiParametar("ime korisnika", "imeKorisnika");
        unesiParametar("email korisnika", "email");
        unesiParametar("godiste korisnika - godina", "godiste");
        unesiParametar("pol korisnika - (M / Z)", "pol");
        unesiParametar("naziv mesta korisnika", "mesto");
        unesiParametar("sifru za korisnika", "sifra");
        sendHttpRequest(URL, "POST");
    }
    
    public static void zahtev3Handler(Integer curKorisnikId) {
        URL = URL_START;
        URL = URL + "/zahtev3";
        URL = URL + "?";
        count = 0;
        
        dodajNaURL("curKorisnikId", curKorisnikId.toString());
        unesiParametar("novu email adresu", "email");
        sendHttpRequest(URL, "POST");
    }
    
    public static void zahtev4Handler(Integer curKorisnikId) {
        URL = URL_START;
        URL = URL + "/zahtev4";
        URL = URL + "?";
        count = 0;
        
        dodajNaURL("curKorisnikId", curKorisnikId.toString());
        unesiParametar("naziv novog mesta korisnika", "mesto");
        sendHttpRequest(URL, "POST");
    }
    
    public static void zahtev18Handler() {
        URL = URL_START;
        URL = URL + "/zahtev18";
//        URL = URL + "?";
        count = 0;
        
        sendHttpRequest(URL, "GET");
    }
    
    public static void zahtev19Handler() {
        URL = URL_START;
        URL = URL + "/zahtev19";
//        URL = URL + "?";
        count = 0;
        
        sendHttpRequest(URL, "GET");
    }

     public static int zahtevLogin() {
        URL = URL_START;
        URL = URL + "/zahtevLogin";
        URL = URL + "?";
        count = 0;
        
        unesiParametar("ime korisnika", "imeKorisnika");   
        unesiParametar("sifru", "sifra");
        String resp = sendHttpRequest(URL, "GET");
        return Integer.parseInt(resp);
    }
     
}
