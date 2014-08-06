/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.uzdiz.main;

import hr.foi.uzdiz.mvc.Controller;
import hr.foi.uzdiz.mvc.Model;
import hr.foi.uzdiz.mvc.View;

/**
 *
 * @author Winner
 */
public class Dretva implements Runnable{
    
    private Controller controller;
    private Model model;

    public Dretva(Controller controller, Model model) {
        this.controller = controller;
        this.model = model;
    }
    
    
    
    @Override
    public void run() {
        View view = new View();
        view.addModel(model);
        model.addObserver(view);
        controller.addView(view);
        controller.processInput();
    }
    
}
