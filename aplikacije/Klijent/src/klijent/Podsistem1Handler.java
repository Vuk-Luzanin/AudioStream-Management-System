/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

/**
 *
 * @author vukluzanin
 */
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
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));        // 
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                
                String response = content.toString();
                
                System.out.println(response);
                return response;
            } else {
                System.out.println("Failed to get response, status code: " + status);
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
                // adds separator &
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

    }
    
    public static void zahtev3Handler() {

    }
    
    public static void zahtev4Handler() {

    }
    
    public static void zahtev5Handler() {

    }

    
    
    
}
