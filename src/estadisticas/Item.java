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

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.MouseAdapter;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public abstract class Item extends JLabel implements Comparable {

    protected boolean selected = false;
    protected ImageIcon normalIcon;
    protected ImageIcon selectedIcon;
    protected int index;

    public Item(String text) {
        super(text);
        setOpaque(false);
        addMouseListener(getDefaultMouseActions());
        setNormalIcon(getDefaultNormalIcon());
        setSelectedIcon(getDefaultSelectedIcon());
        setSelected(false);
    }

    public abstract MouseAdapter getDefaultMouseActions();

    public abstract ImageIcon getDefaultNormalIcon();

    public abstract ImageIcon getDefaultSelectedIcon();

    public abstract Paint getDefaultBackgroundPaint();

    public final void cambiarEstado() {
        setSelected(!isSelected());
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            setIcon(selectedIcon);
            setFont(getFont().deriveFont(Font.BOLD));
        } else {
            setIcon(normalIcon);
            setFont(getFont().deriveFont(Font.PLAIN));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getDefaultBackgroundPaint() != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(getDefaultBackgroundPaint());
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        super.paintComponent(g);
    }

    public ImageIcon getNormalIcon() {
        return normalIcon;
    }

    public void setNormalIcon(ImageIcon normalIcon) {
        this.normalIcon = normalIcon;
        setSelected(selected);
    }

    public ImageIcon getSelectedIcon() {
        return selectedIcon;
    }

    public void setSelectedIcon(ImageIcon selectedIcon) {
        this.selectedIcon = selectedIcon;
         setSelected(selected);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int compareTo(Object o) {
        Item target = (Item) o;
        if (getIndex() == target.getIndex()) {
            return 0;
        } else if (getIndex() > target.getIndex()) {
            return 1;
        } else {
            return -1;
        }
    }
}
