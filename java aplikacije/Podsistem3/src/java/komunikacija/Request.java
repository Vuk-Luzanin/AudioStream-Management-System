/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package komunikacija;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author vukluzanin
 */
public class Request implements Serializable {
    
    private int idZahteva;
    private int brParametara = 0;
    ArrayList<Object> parametri = new ArrayList<>();

    public int getIdZahteva() {
        return idZahteva;
    }

    public void setIdZahteva(int idZahteva) {
        this.idZahteva = idZahteva;
    }

    public int getBrParametara() {
        return brParametara;
    }

    public void setBrParametara(int brParametara) {
        this.brParametara = brParametara;
    }

    public ArrayList<Object> getParametri() {
        return parametri;
    }

    public void setParametri(ArrayList<Object> parametri) {
        this.parametri = parametri;
        this.brParametara = parametri.size();
    }
    
    public void dodajParametar(Object o) {
        this.parametri.add(o);
        this.brParametara = parametri.size();
    }
    
    
}
