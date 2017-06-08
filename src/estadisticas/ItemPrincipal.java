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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;

public class ItemPrincipal extends Item {

    private Branch branchPanel;

    public ItemPrincipal(String text) {
        super(text);
        this.branchPanel = new Branch();
    }

    @Override
    public MouseAdapter getDefaultMouseActions() {
        return new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        };
    }

    @Override
    public ImageIcon getDefaultNormalIcon() {
        return new ImageIcon(this.getClass().getResource("resources/list_plus.png"));
    }
    
    @Override
    public ImageIcon getDefaultSelectedIcon() {
        return new ImageIcon(this.getClass().getResource("resources/list_minus.png"));
    }

    @Override
    public Paint getDefaultBackgroundPaint() {
        Color c1, c2;
        if (isSelected()) {
            c2 = getBackground();
            c1 = c2.darker();
        } else {
            c1 = getBackground();
            c2 = c1.darker();
        }
        return new GradientPaint(0, 0, c1, 0, getHeight(), c2);
    }

    public Branch getBranchPanel() {
        return branchPanel;
    }
}
