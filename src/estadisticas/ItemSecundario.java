/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Toni
 */
package estadisticas;

import java.awt.Paint;
import java.awt.event.MouseAdapter;
import javax.swing.ImageIcon;

public class ItemSecundario extends Item {

    public ItemSecundario(String text) {
        super(text);
    }

    @Override
    public MouseAdapter getDefaultMouseActions() {
        return new MouseAdapter() {
        };
    }

    @Override
    public ImageIcon getDefaultNormalIcon() {
        return new ImageIcon(this.getClass().getResource("resources/disconected.png"));
    }

    @Override
    public ImageIcon getDefaultSelectedIcon() {
        return new ImageIcon(this.getClass().getResource("resources/disconected.png"));
    }

    @Override
    public Paint getDefaultBackgroundPaint() {
        return null;
    }
}
