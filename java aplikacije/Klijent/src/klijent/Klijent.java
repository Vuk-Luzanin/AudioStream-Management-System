
package klijent;

import java.util.Scanner;

public class Klijent {

    
    private static String menu = "\n1. Kreiranje grada\n" +
        "2. Kreiranje korisnika\n" + 
        "3. Promena email adrese za korisnika\n"+ 
        "4. Promena mesta za korisnika\n"+ 
        "5. Kreiranje kategorije\n"+ 
        "6. Kreiranje audio snimka\n"+ 
        "7. Promena naziva audio snimka\n"+ 
        "8. Dodavanje kategorije audio snimku\n"+ 
        "9. Kreiranje paketa\n"+ 
        "10. Promena mesečne cene za paket\n"+ 
        "11. Kreiranje pretplate korisnika na paket\n"+ 
        "12. Kreiranje slušanja audio snimka od strane korisnika\n"+ 
        "13. Dodavanje audio snimka u omiljene od strane korisnika\n"+ 
        "14. Kreiranje ocene korisnika za audio snimak\n"+ 
        "15. Menjanje ocene korisnika za audio snimak\n"+ 
        "16. Brisanje ocene korisnika za audio snimak\n"+ 
        "17. Brisanje audio snimka od strane korisnika koji ga je kreirao\n"+ 
        "18. Dohvatanje svih mesta\n"+ 
        "19. Dohvatanje svih korisnika\n"+ 
        "20. Dohvatanje svih kategorija\n"+ 
        "21. Dohvatanje svih audio snimaka\n"+ 
        "22. Dohvatanje kategorija za određeni audio snimak\n"+ 
        "23. Dohvatanje svih paketa\n"+ 
        "24. Dohvatanje svih pretplata za korisnika\n"+ 
        "25. Dohvatanje svih slušanja za audio snimak\n"+ 
        "26. Dohvatanje svih ocena za audio snimak\n"+ 
        "27. Dohvatanje liste omiljenih audio snimaka za korisnika\n"+
        "100. Izlogujte se\n";
    
    private static final int KREIRAJ_GRAD = 1;
    private static final int KREIRAJ_KORISNIKA = 2;
    private static final int PROMENA_EMAIL_ADRESE = 3;
    private static final int PROMENA_MESTA = 4;
    private static final int KREIRAJ_KATEGORIJU = 5;
    private static final int KREIRAJ_AUDIO_SNIMAK = 6;
    private static final int PROMENA_NAZIVA_SNIMKA = 7;
    private static final int DODAJ_KATEGORIJU_SNIMKU = 8;
    private static final int KREIRAJ_PAKET = 9;
    private static final int PROMENA_CENE_PAKETA = 10;
    private static final int KREIRAJ_PRETPLATU = 11;
    private static final int KREIRAJ_SLUSANJE = 12;
    private static final int DODAJ_OMILJENI_SNIMAK = 13;
    private static final int KREIRAJ_OCENU = 14;
    private static final int PROMENA_OCENE = 15;
    private static final int BRISANJE_OCENE = 16;
    private static final int BRISANJE_SNIMKA = 17;
    private static final int DOHVATI_MESTA = 18;
    private static final int DOHVATI_KORISNIKE = 19;
    private static final int DOHVATI_KATEGORIJE = 20;
    private static final int DOHVATI_SNIMKE = 21;
    private static final int DOHVATI_KATEGORIJE_SNIMKA = 22;
    private static final int DOHVATI_PAKETE = 23;
    private static final int DOHVATI_PRETPLATE = 24;
    private static final int DOHVATI_SLUSANJA_SNIMKA = 25;
    private static final int DOHVATI_OCENE_SNIMKA = 26;
    private static final int DOHVATI_OMILJENE_SNIMKE = 27;
    private static final int LOGOUT = 100;
    private static int curKorisnikId = -1;

    public static void main(String[] args) {
        System.out.println("Klijent pokrenut...");
        while (true) {
            
            // DODATI DA SERVER PO POKRETANJU DODAJE KORISNIKA KOJI SE ZOVE NPR. ADMIN!
            while(curKorisnikId == -1)
            {
                curKorisnikId = Podsistem1Handler.zahtevLogin();
                if(curKorisnikId == -1) 
                    System.out.println("NEUSPESNO LOGOVANJE");
                else
                    System.out.println("USPESNO STE SE ULOGOVALI");
            }
            
            System.out.println(menu);
            System.out.println("Izaberite jedan od zahteva unosom broja zahteva: ");
            int izbor = -1;
            Scanner in = new Scanner(System.in);
            izbor = in.nextInt();
            
            switch (izbor) {
                case KREIRAJ_GRAD:
                    Podsistem1Handler.zahtev1Handler();
                    break;
                case KREIRAJ_KORISNIKA:
                    Podsistem1Handler.zahtev2Handler();
                    break;
                case PROMENA_EMAIL_ADRESE:
                    Podsistem1Handler.zahtev3Handler(curKorisnikId);
                    break;
                case PROMENA_MESTA:
                    Podsistem1Handler.zahtev4Handler(curKorisnikId);
                    break;
                case KREIRAJ_KATEGORIJU:
                    Podsistem2Handler.zahtev5Handler();
                    break;
                case KREIRAJ_AUDIO_SNIMAK:
                    Podsistem2Handler.zahtev6Handler(curKorisnikId);
                    break;
                case PROMENA_NAZIVA_SNIMKA:
                    Podsistem2Handler.zahtev7Handler(curKorisnikId);
                    break;
                case DODAJ_KATEGORIJU_SNIMKU:
                    Podsistem2Handler.zahtev8Handler(curKorisnikId);
                    break;
                case KREIRAJ_PAKET:
                    Podsistem3Handler.zahtev9Handler();
                    break;
                case PROMENA_CENE_PAKETA:
                    Podsistem3Handler.zahtev10Handler();
                    break;
                case KREIRAJ_PRETPLATU:
                    Podsistem3Handler.zahtev11Handler(curKorisnikId);
                    break;
                case KREIRAJ_SLUSANJE:
                    Podsistem3Handler.zahtev12Handler(curKorisnikId);
                    break;
                case DODAJ_OMILJENI_SNIMAK:
                    Podsistem3Handler.zahtev13Handler(curKorisnikId);
                    break;
                case KREIRAJ_OCENU:
                    Podsistem3Handler.zahtev14Handler(curKorisnikId);
                    break;
                case PROMENA_OCENE:
                    Podsistem3Handler.zahtev15Handler(curKorisnikId);
                    break;
                case BRISANJE_OCENE:
                    Podsistem3Handler.zahtev16Handler(curKorisnikId);
                    break;
                case BRISANJE_SNIMKA:
                    Podsistem2Handler.zahtev17Handler(curKorisnikId);
                    break;
                case DOHVATI_MESTA:
                    Podsistem1Handler.zahtev18Handler();
                    break;
                case DOHVATI_KORISNIKE:
                    Podsistem1Handler.zahtev19Handler();
                    break;
                case DOHVATI_KATEGORIJE:
                    Podsistem2Handler.zahtev20Handler();
                    break;
                case DOHVATI_SNIMKE:
                    Podsistem2Handler.zahtev21Handler();
                    break;
                case DOHVATI_KATEGORIJE_SNIMKA:
                    Podsistem2Handler.zahtev22Handler();
                    break;
                case DOHVATI_PAKETE:
                    Podsistem3Handler.zahtev23Handler();
                    break;
                case DOHVATI_PRETPLATE:
                    Podsistem3Handler.zahtev24Handler(curKorisnikId);
                    break;
                case DOHVATI_SLUSANJA_SNIMKA:
                    Podsistem3Handler.zahtev25Handler();
                    break;
                case DOHVATI_OCENE_SNIMKA:
                    Podsistem3Handler.zahtev26Handler();
                    break;
                case DOHVATI_OMILJENE_SNIMKE:
                    Podsistem3Handler.zahtev27Handler(curKorisnikId);
                    break;
                  case LOGOUT:
                    System.out.println("Logging out...");
                    curKorisnikId = -1;
                    System.out.println("IZLOGOVANI STE\nPONOVO UNESITE VASE PODATKE");
                    break;
                default:
                    System.out.println("Nepoznata opcija. Molimo pokušajte ponovo.");
                    break;
            }
            System.out.println("------------------------------------------------------------------------");
        }
    }
}
