/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.uzdiz.mvc;

import java.io.File;
import java.util.Scanner;

/**
 *
 * @author Winner
 */
public class Controller {
    
    private Model model;
    private View view;
    private String url;

    public Controller(String url) {
        this.url = url;
    }
    
    
    
    public void addModel(Model model){
        this.model = model;
    }
    
    public void addView(View v) {
        this.view = v;
    }

    
    public void initModel(){
        model.ucitajStranicu(url);
        view.ispisZaSite();
    }
    
    public void processInput(){
        String komande[];
        Scanner sc = new Scanner(System.in);
        while (true) {            
            String input = sc.nextLine();
            
            switch(input){
                case "S":
                    view.ispisStatistike();
                    break;
                case "Q":
                    model.SerijalizirajListu();
                    System.exit(1);
                    break;
                case "C":
                    model.ocistiDirektorij(new File(model.getPutanjaSpremista()));
                    break;
                default:
                    if (input.matches("J ([0-9]*)")) {
                        komande = input.split(" ");
                        url = model.vratiUrlStranice(Integer.parseInt(komande[1]));
                        if (url == null) {
                            view.krivaKomanda();
                        } else {
                            model.ucitajStranicu(url);
                            view.ispisZaSite();
                            view.ispisPoveznica();
                        }
                    }
                    else{
                        view.krivaKomanda();
                    }
                    
            }
        }
        
        
        
    }
    
}
