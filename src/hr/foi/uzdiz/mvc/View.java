/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.uzdiz.mvc;

import hr.foi.uzdiz.classes.UrlGlobal;
import hr.foi.uzdiz.classes.UrlSingle;
import java.util.Observable;

/**
 *
 * @author Winner
 */
public class View implements java.util.Observer{
    
    private Model model;

    public View() {
        System.out.println("-J n - prijelaz na poveznicu s rednim brojem n te "
                + "učitavanje web stranice koja time postaje vežeća");
        System.out.println("-S - Statistika spremnika");
        System.out.println("-Q - prekid rada programa");
        System.out.println("-C - Brisanje spremnika na zahtjev");
    }
    
    public void addModel(Model m){
        this.model = m;
        ispisPoveznica();
    }
    

    
    public void ispisPoveznica(){
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 70; i++) {
            str.append("-");
        }
        System.out.println(str);
        System.out.printf("|%7s | %-55s|\n", "Redni broj","Web adrese");
        System.out.println(str);
        for (UrlSingle object : model.getLista()) {
            System.out.printf("|%10s | %-55s|\n", object.getRedniBroj()+".",object.getUrl());
            System.out.println(str);
        }
    }
    
    public void ispisZaSite(){
        System.out.println("Učitan je site "+model.getCurrentUrl());
    }
    
    public void krivaKomanda(){
        System.out.println("Unijeli ste nepostojeću komandu! Pokušajte ponovno.");
    }
    
    public void ispisStatistike(){
        
        for (UrlGlobal urlGlobal : model.vratiListuWebStranica()) {
            System.out.println("---------------------------------------------------------");
            System.out.println("Naziv datoteke: "+urlGlobal.getNazivDatoteke());
            System.out.println("Broj korištenja: "+urlGlobal.getBrojKorištenja());
            long diff = urlGlobal.getZadnjeKorištenje();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            String vrijeme = diffHours+":"+diffMinutes+":"+diffSeconds;
            System.out.println("Zadnje korištenje : "+vrijeme);
            System.out.println("---------------------------------------------------------");
        }
        
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println(arg);
    }
    
}
