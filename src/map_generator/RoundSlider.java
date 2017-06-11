/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map_generator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JComponent;

/**
 *
 * @author PC_15
 */
public class RoundSlider extends JComponent implements MouseListener, MouseMotionListener {

    private static final int radius = 25;
    private static final int spotRadius = 5;

    private double theta;
    private Color knobColor;
    private Color spotColor;

    private boolean pressedOnSpot;

    /**
     * No-Arg constructor that initializes the position of the knob to 0 radians
     * (Up).
     */
    public RoundSlider() {
        this(0);
    }

    /**
     * Constructor that initializes the position of the knob to the specified
     * angle in radians.
     *
     * @param initTheta
     */
    public RoundSlider(double initTheta) {
        this(initTheta, Color.gray, Color.black);
    }

    /**
     * Constructor that initializes the position of the knob to the specified
     * position and also allows the colors of the knob and spot to be specified.
     *
     * @param initTheta
     * @param initKnobColor
     * @param initSpotColor the color of the spot.
     */
    public RoundSlider(double initTheta, Color initKnobColor, Color initSpotColor) {
        theta = initTheta;
        pressedOnSpot = false;
        knobColor = initKnobColor;
        spotColor = initSpotColor;

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    /**
     * Paint the JKnob on the graphics context given. The knob is a filled
     * circle with a small filled circle offset within it to show the current
     * angular position of the knob.
     *
     * @param g The graphics context on which to paint the knob.
     */
    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw the knob.
        g2d.setColor(knobColor);
        g2d.fillOval(0, 0, 2 * radius, 2 * radius);

        // Find the center of the spot.
        Point pt = getSpotCenter();
        int xc = (int) pt.getX();
        int yc = (int) pt.getY();

        // Draw the spot.
        g2d.setColor(spotColor);
        g2d.fillOval(xc - spotRadius, yc - spotRadius, 2 * spotRadius, 2 * spotRadius);
        
        g2d.drawLine(radius, radius, xc, yc);
    }

    /**
     * Return the ideal size that the knob would like to be.
     *
     * @return the preferred size of the JKnob.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(2 * radius, 2 * radius);
    }

    /**
     * Return the minimum size that the knob would like to be. This is the same
     * size as the preferred size so the knob will be of a fixed size.
     *
     * @return the minimum size of the JKnob.
     */
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(2 * radius, 2 * radius);
    }

    /**
     * Get the current anglular position of the knob.
     *
     * @return the current anglular position of the knob.
     */
    public double getAngle() {
        double ret = 0;
        if (theta >= 0 && theta < 1.5708001) {
            ret = 1.5708001 - theta;
        } else if (theta < 0 && theta >= -1.5708001) {
            ret = 1.5708001 + (theta * -1);
        } else if (theta >= 1.5708001 && theta <= 3.14159001) {
            ret = 6.28319 - (theta - 1.5708001);
        } else if (theta < -1.5708001 && theta >= -3.14159001) {
            ret = 3.14159001 + ((theta * -1) - 1.5708001);
        }
        //return theta;
        return ret;
    }

    /**
     * Calculate the x, y coordinates of the center of the spot.
     *
     * @return a Point containing the x,y position of the center of the spot.
     */
    private Point getSpotCenter() {

        // Calculate the center point of the spot RELATIVE to the
        // center of the of the circle.
        int r = radius - spotRadius;

        int xcp = (int) (r * Math.sin(theta));
        int ycp = (int) (r * Math.cos(theta));

        // Adjust the center point of the spot so that it is offset
        // from the center of the circle.  This is necessary becasue
        // 0,0 is not actually the center of the circle, it is  the 
        // upper left corner of the component!
        int xc = radius + xcp;
        int yc = radius - ycp;

        // Create a new Point to return since we can't  
        // return 2 values!
        return new Point(xc, yc);
    }

    /**
     * Comprueba si se ha hecho click en el el indicador o no.
     * @return True sí / False no
     */
    private boolean isOnSpot(Point pt) {
        return (pt.distance(getSpotCenter()) < spotRadius);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point mouseLoc = e.getPoint();
        pressedOnSpot = isOnSpot(mouseLoc);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressedOnSpot = false;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    /**
     * Calcula el ángulo nuevo cuando se arrastra el mouse
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (pressedOnSpot) {

            int mx = e.getX();
            int my = e.getY();

            int mxp = mx - radius;
            int myp = radius - my;

            theta = Math.atan2(mxp, myp);

            repaint();
        }
    }
}
