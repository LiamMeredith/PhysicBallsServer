/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map_generator;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author Miquel Ginés
 */
public class MapDesigner extends JFrame {
    
    public MapDesigner() throws IOException {
        super("PhysicsBalls - Map designer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new Designer(this));
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }
    

}
