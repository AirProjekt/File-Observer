/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.uzdiz.main;

import hr.foi.uzdiz.mvc.Controller;
import hr.foi.uzdiz.mvc.Model;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Winner
 */
public class Aplikacija {
    
    public static void main(String[] args){
        Model model = new Model(args);
        Controller controller = new Controller(args[0]);
        controller.addModel(model);
        Thread dretva = new Thread(new Dretva(controller,model));
        dretva.start();
        while (true) {            
            
            try {
                Thread.sleep(Integer.parseInt(args[2])*1000);
            } catch (InterruptedException ex) {
                System.out.println("Morate unijeti broj!");
                Logger.getLogger(Aplikacija.class.getName()).log(Level.SEVERE, null, ex);
            }
            controller.initModel();
        }
    }
    
}
