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
public class ConcreteStrategyBE implements Strategy{

    @Override
    public double vratiVelicinuSpremnika(List<UrlGlobal> lista) {
        return lista.size();
    }

    
    
}
