/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.uzdiz.Strategy;

import hr.foi.uzdiz.classes.UrlGlobal;
import java.util.List;

/**
 *
 * @author Winner
 */
public class Context {
    private Strategy strategy;
// Constructor
    public Context(Strategy strategy) {
        this.strategy = strategy;
    }

    public double executeStrategy(List<UrlGlobal> lista) {
        return strategy.vratiVelicinuSpremnika(lista);
    }
}
