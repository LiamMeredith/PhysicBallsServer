/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map_generator;

import database.DBHandler;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Stack;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.text.NumberFormatter;
import org.physicballs.items.*;


/**
 *
 * @author Miquel Ginés
 */
public class Designer extends JSplitPane {

    // Listas de objetos
    private ArrayList<Obstacle> obstacles = new ArrayList<>();
    private ArrayList<StopItem> stopItems = new ArrayList<>();
    private ArrayList<Ball> balls = new ArrayList<>();

    private String mapName;

    private StopItem stopItem;
    private ArrayList<Point[]> speedVecs = new ArrayList<>();
    private Ball ball;
    private Obstacle obstacle;

    private String item = "BALL";

    private boolean saved = false;

    private DBHandler db;

    // Valores máximos y mínimos permitidos
    private final int MAX_HEIGHT = 200;
    private final int MAX_WIDTH = 200;
    private final int MIN_SPEED = 0;
    private final int MAX_SPEED = 90;
    private final int MIN_RADIUS = 5;
    private final int MAX_RADIUS = 40;
    private final int MIN_MARGIN = 20;
    private final int MAPPANEL_WIDTH = 970;
    private final int MAPPANEL_HEIGHT = 546;

    private double screenProp;

    // Pila usada para el botón de deshacer
    private Stack<String> undoList = new Stack<>();

    private JFrame frame;

    // Los dos paneles en los que se separa la ventana, el de los controles y
    // el del mapa
    private JPanel controlPanel;
    private JPanel mapPanel;

    // Objetos de labels de información
    private JLabel mapNameLb;
    private JLabel ballLb;
    private JLabel obsLb;
    private JLabel stopItemLb;
    private JLabel mapOptLb;
    private JLabel selectedItemLb;
    private JLabel posXLb;
    private JLabel posYLb;
    private JLabel widthLb;
    private JLabel heightLb;
    private JLabel radiusLb;
    private JLabel radiusInfoLb;
    private JLabel angleLb;
    private JLabel angleInfoLb;
    private JLabel speedLb;
    private JLabel speedInfoLb;
    private JLabel ballTypeLb;

    private JTextField posXTf;
    private JTextField posYTf;
    private JTextField widthTf;
    private JTextField heightTf;

    private JSlider radiusSl;
    private JSlider speedSl;
    private RoundSlider angleSl;

    // Botones del panel de controles
    private JButton playBtn;
    private JButton saveBtn;
    private JButton undoBtn;
    private JButton ballBtn;
    private JButton obsBtn;
    private JButton eraseBtn;
    private JButton stopItemBtn;
    private JButton configBtn;

    private JMenuBar menuBar;
    private JMenu optionsMenu;
    private JMenuItem deleteMapIm;
    private JMenuItem newMapIm;
    private JMenuItem changeMapnameIm;
    private JMenuItem runPhysicBallsIm;
    private JMenuItem saveIm;
    private JMenuItem undoIm;
    private JMenuItem clearIm;

    private JComboBox ballTypeCb;

    /**
     * Constructor inicial, llama a los métodos de creación de los paneles, y
     * crea la barra de menú
     *
     * @param frame
     * @throws IOException
     */
    public Designer(JFrame frame) throws IOException {
        super(JSplitPane.VERTICAL_SPLIT);
        setDividerSize(0);

        this.frame = frame;
        screenProp = MAPPANEL_WIDTH / 1280;

        // Crea la barra de menú superior y la añade al frame
        menuBar = new JMenuBar();
        optionsMenu = new JMenu("Opciones");
        newMapIm = new JMenuItem("Crear mapa nuevo");
        newMapIm.addActionListener((java.awt.event.ActionEvent evt) -> {
            clearMap();
            askMapName();
        });
        deleteMapIm = new JMenuItem("Eliminar un mapa de la base de datos");
        deleteMapIm.addActionListener((java.awt.event.ActionEvent evt) -> {
            deleteMap();
        });
        changeMapnameIm = new JMenuItem("Cambiar nombre del mapa");
        changeMapnameIm.addActionListener((java.awt.event.ActionEvent evt) -> {
            changeMapName();
        });
        runPhysicBallsIm = new JMenuItem("Arrancar simulador");
        runPhysicBallsIm.addActionListener((java.awt.event.ActionEvent evt) -> {
            runPhysicBalls();
        });
        saveIm = new JMenuItem("Guardar mapa");
        saveIm.addActionListener((java.awt.event.ActionEvent evt) -> {
            saveMap();
        });
        undoIm = new JMenuItem("Deshacer último objeto");
        undoIm.addActionListener((java.awt.event.ActionEvent evt) -> {
            undoObj();
        });
        clearIm = new JMenuItem("Vaciar mapa");
        clearIm.addActionListener((java.awt.event.ActionEvent evt) -> {
            clearMap();
        });
        optionsMenu.add(newMapIm);
        optionsMenu.add(deleteMapIm);
        optionsMenu.addSeparator();
        optionsMenu.add(changeMapnameIm);
        optionsMenu.addSeparator();
        optionsMenu.add(runPhysicBallsIm);
        optionsMenu.add(saveIm);
        optionsMenu.add(undoIm);
        optionsMenu.add(clearIm);

        menuBar.add(optionsMenu);

        frame.setJMenuBar(menuBar);

        db = new DBHandler();

        // Llama a los métodos de creación de los paneles
        createControlPanel();
        createMapPanel();

        setLeftComponent(controlPanel);
        setRightComponent(mapPanel);
        
        // Solicita el nombre inicial del mapa
        askMapName();
    }

    /**
     * Elimina un mapa de la base de datos
     */
    private void deleteMap() {
        String[] options = db.getSpaceList1();
        String delMapName = (String) JOptionPane.showInputDialog(
                frame,
                "Seleccione el mapa que desea eliminar",
                "Eliminar mapa",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);
        if ((delMapName != null) && (delMapName.length() > 0)) {
            Object[] confirm = {"Sí", "No"};
            int selected = JOptionPane.showOptionDialog(frame,
                    "¿Seguro que quiere eliminar el mapa '" + delMapName + "'?",
                    "Eliminar",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    confirm,
                    confirm[0]);
            if (selected == 0) {
                db.deleteSpaceCascade(delMapName);
                JOptionPane.showMessageDialog(this, "Mapa eliminado correctamente", "Mapa eliminado", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Slicita el nombre inicial del mapa
     */
    private void askMapName() {
        boolean validName = true;
        do {
            mapName = JOptionPane.showInputDialog(this, "Introduce el nombre del mapa:", "");
            validName = db.checkSpaceName(mapName);
            if (mapName == null || mapName.length() < 1) {
                System.exit(0);
            }
            if (!validName) {
                JOptionPane.showMessageDialog(this, "Ya existe un mapa con ese nombre en la base de datos.", "Error en la creación del mapa", JOptionPane.ERROR_MESSAGE);
            }
        } while (!validName);
        mapNameLb.setText(mapName);
    }

    /**
     * Cambia el nombre del mapa en curso
     */
    private void changeMapName() {
        boolean validName = true;
        String newMapName = "";
        if (!saved) {
            do {
                newMapName = JOptionPane.showInputDialog(this, "Introduce el nuevo nombre del mapa:", "");
                if (newMapName != null && newMapName.length() > 0) {
                    validName = db.checkSpaceName(newMapName);
                    if (!validName) {
                        JOptionPane.showMessageDialog(this, "Ya existe un mapa con ese nombre en la base de datos.", "Error en la creación del mapa", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } while (!validName);
            mapNameLb.setText(newMapName);
        } else {
            JOptionPane.showMessageDialog(this, "No se puede cambiar el nombre a un mapa ya guardado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** 
     * Crea el panel superior
     * @throws IOException 
     */
    private void createControlPanel() throws IOException {
        // Botón para cambiar de item
        controlPanel = new JPanel() {
            // Este método pinta las líneas que hacen de separadores de los paneles
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.setColor(Color.GRAY);
                g.fillRect(0, 140, 1150, 3);
                g.fillRect(220, 0, 3, 140);
                g.fillRect(364, 0, 3, 140);
            }
        };
        controlPanel.setPreferredSize(new Dimension(MAPPANEL_WIDTH, 143));
        controlPanel.setLayout(null);

        createMapControlForm();
        createObjectParamForm();
    }

    /**
     * Crea el panel de control del mapa y objetos
     */
    private void createMapControlForm() {

        ballLb = new JLabel("Bola", SwingConstants.CENTER);
        ballLb.setSize(40, 15);
        ballLb.setLocation(14, 5);
        controlPanel.add(ballLb);

        // Botón para seleccionar la bola
        ballBtn = new JButton(new ImageIcon("img/ball.png"));
        ballBtn.setSize(40, 40);
        ballBtn.setLocation(15, 20);
        ballBtn.setEnabled(false);
        ballBtn.setBorder(new RoundedBorder(10));
        ballBtn.setForeground(Color.GRAY);
        ballBtn.setToolTipText("Colocar bolas");
        ballBtn.addActionListener((ActionEvent e) -> {
            if (!item.equals("BALL")) {
                item = "BALL";
                ballBtn.setEnabled(false);
                obsBtn.setEnabled(true);
                stopItemBtn.setEnabled(true);
                radiusLb.setVisible(true);
                radiusInfoLb.setVisible(true);
                radiusSl.setVisible(true);
                widthLb.setVisible(false);
                widthTf.setVisible(false);
                heightLb.setVisible(false);
                heightTf.setVisible(false);
                speedLb.setVisible(true);
                speedSl.setVisible(true);
                speedInfoLb.setVisible(true);
                angleSl.setVisible(true);
                angleLb.setVisible(true);
                angleInfoLb.setVisible(true);
                ballTypeLb.setVisible(true);
                ballTypeCb.setVisible(true);
                selectedItemLb.setText("Bola");
            }
        });
        controlPanel.add(ballBtn);

        obsLb = new JLabel("Obstáculo", SwingConstants.CENTER);
        obsLb.setSize(60, 15);
        obsLb.setLocation(79, 5);
        controlPanel.add(obsLb);

        // Botón para seleccionar el obstáculo
        obsBtn = new JButton(new ImageIcon("img/obs.png"));
        obsBtn.setSize(40, 40);
        obsBtn.setLocation(88, 20);
        obsBtn.setBorder(new RoundedBorder(10));
        obsBtn.setForeground(Color.GRAY);
        obsBtn.setToolTipText("Colocar obstáculos");
        obsBtn.addActionListener((ActionEvent e) -> {
            if (!item.equals("OBSTACLE")) {
                item = "OBSTACLE";
                obsBtn.setEnabled(false);
                ballBtn.setEnabled(true);
                stopItemBtn.setEnabled(true);
                radiusLb.setVisible(false);
                radiusInfoLb.setVisible(false);
                radiusSl.setVisible(false);
                widthLb.setVisible(true);
                widthTf.setVisible(true);
                heightLb.setVisible(true);
                heightTf.setVisible(true);
                speedLb.setVisible(false);
                speedSl.setVisible(false);
                speedInfoLb.setVisible(false);
                angleSl.setVisible(false);
                angleLb.setVisible(false);
                angleInfoLb.setVisible(false);
                ballTypeLb.setVisible(false);
                ballTypeCb.setVisible(false);
                selectedItemLb.setText("Obstáculo");
            }
        });
        controlPanel.add(obsBtn);

        stopItemLb = new JLabel("Semáforo", SwingConstants.CENTER);
        stopItemLb.setSize(60, 15);
        stopItemLb.setLocation(150, 5);
        controlPanel.add(stopItemLb);

        // Botón para seleccionar el semáforo
        stopItemBtn = new JButton(new ImageIcon("img/bottle.png"));
        stopItemBtn.setSize(40, 40);
        stopItemBtn.setLocation(161, 20);
        stopItemBtn.setBorder(new RoundedBorder(10));
        stopItemBtn.setForeground(Color.GRAY);
        stopItemBtn.setToolTipText("Colocar semáforos");
        stopItemBtn.addActionListener((ActionEvent e) -> {
            item = "BOTTLENECK";
            stopItemBtn.setEnabled(false);
            ballBtn.setEnabled(true);
            obsBtn.setEnabled(true);
            radiusLb.setVisible(false);
            radiusInfoLb.setVisible(false);
            radiusSl.setVisible(false);
            widthLb.setVisible(true);
            widthTf.setVisible(true);
            heightLb.setVisible(true);
            heightTf.setVisible(true);
            speedLb.setVisible(false);
            speedSl.setVisible(false);
            speedInfoLb.setVisible(false);
            angleSl.setVisible(false);
            angleLb.setVisible(false);
            angleInfoLb.setVisible(false);
            ballTypeLb.setVisible(false);
            ballTypeCb.setVisible(false);
            selectedItemLb.setText("Semáforo");
        });
        controlPanel.add(stopItemBtn);

        mapNameLb = new JLabel("", SwingConstants.CENTER);
        mapNameLb.setFont(new Font(mapNameLb.getFont().getName(), Font.BOLD, 20));
        mapNameLb.setSize(190, 30);
        mapNameLb.setLocation(15, 70);
        controlPanel.add(mapNameLb);

        // Botón para modificar la configuración general del mapa
        configBtn = new JButton();
        configBtn.setSize(186, 25);
        configBtn.setLocation(15, 110);
        configBtn.setBorder(new RoundedBorder(10));
        configBtn.setForeground(Color.BLACK);
        configBtn.setText("Cambiar nombre");
        configBtn.setHorizontalAlignment(SwingConstants.CENTER);
        configBtn.setToolTipText("Cambia el nombre del mapa");
        configBtn.addActionListener((ActionEvent e) -> {
            changeMapName();
        });
        controlPanel.add(configBtn);

        mapOptLb = new JLabel("Opc. del mapa", SwingConstants.CENTER);
        mapOptLb.setSize(102, 13);
        mapOptLb.setLocation(242, 5);
        controlPanel.add(mapOptLb);

        // Botón de arrancar la simulación
        playBtn = new JButton(new ImageIcon("img/play.png"));
        playBtn.setSize(40, 40);
        playBtn.setLocation(242, 20);
        playBtn.setBorder(new RoundedBorder(10));
        playBtn.setForeground(Color.GRAY);
        playBtn.setToolTipText("Arrancar la simulación");
        playBtn.addActionListener((ActionEvent e) -> {
            runPhysicBalls();
        });
        controlPanel.add(playBtn);

        // Botón de guardar
        saveBtn = new JButton(new ImageIcon("img/save.png"));
        saveBtn.setEnabled(false);
        saveBtn.setSize(40, 40);
        saveBtn.setLocation(304, 20);
        saveBtn.setBorder(new RoundedBorder(10));
        saveBtn.setForeground(Color.GRAY);
        saveBtn.setToolTipText("Guardar escenario");
        saveBtn.addActionListener((ActionEvent e) -> {
            saveMap();
        });
        controlPanel.add(saveBtn);

        // Botón de deshacer
        undoBtn = new JButton(new ImageIcon("img/undo.png"));
        undoBtn.setEnabled(false);
        undoBtn.setSize(40, 40);
        undoBtn.setLocation(242, 80);
        undoBtn.setBorder(new RoundedBorder(10));
        undoBtn.setForeground(Color.GRAY);
        undoBtn.setToolTipText("Deshacer última acción");
        undoBtn.addActionListener((ActionEvent e) -> {
            undoObj();
        });
        controlPanel.add(undoBtn);

        // Botón de borrar al completo
        eraseBtn = new JButton(new ImageIcon("img/erase.png"));
        eraseBtn.setEnabled(false);
        eraseBtn.setSize(40, 40);
        eraseBtn.setLocation(304, 80);
        eraseBtn.setBorder(new RoundedBorder(10));
        eraseBtn.setForeground(Color.GRAY);
        eraseBtn.setToolTipText("Borrar toda la información del escenario");
        eraseBtn.addActionListener((ActionEvent e) -> {
            clearMap();
        });
        controlPanel.add(eraseBtn);
    }

    /**
     * HAY QUE ELIMINAR ESTO
     */
    private void runPhysicBalls() {
        if (!saved && (balls.size() > 0 || obstacles.size() > 0 || stopItems.size() > 0)) {
            Object[] options = {"Sí", "No"};
            int selected = JOptionPane.showOptionDialog(frame,
                    "¿Quiere guardar el mapa antes de arrancar el simulador?",
                    "Guardar",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (selected == 0) {
                saveMap();
                frame.setVisible(false);
                
            }
        } else {
            frame.setVisible(false);
            
        }
    }

    /**
     * Guarda el mapa en la base de datos
     */
    private void saveMap() {
        Object[] options = {"Sí", "No"};
        if (balls.size() > 0 || obstacles.size() > 0 || stopItems.size() > 0) {
            int selected = JOptionPane.showOptionDialog(frame,
                    "¿Seguro que quiere guardar el mapa? No se podrá\nvolver a modificar el mismo.",
                    "Guardar",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (selected == 0) {
                db.insertSpace(mapName);
                db.insertBalls(balls);
                db.insertObstacles(obstacles);
                db.insertStopItems(stopItems);
                saved = true;
                repaint();
            }
        } else {
            JOptionPane.showMessageDialog(this, "No se puede guardar un mapa vacío.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deshace el último objeto añadido al mapa
     */
    private void undoObj() {
        if (!undoList.isEmpty()) {
            String toUndo = undoList.pop();
            switch (toUndo) {
                case "BALL":
                    balls.remove(balls.size() - 1);
                    speedVecs.remove(speedVecs.size() - 1);
                    break;
                case "OBSTACLE":
                    obstacles.remove(obstacles.size() - 1);
                    break;
                case "BOTTLENECK":
                    stopItems.remove(stopItems.size() - 1);
                    break;
                default:
                    // nada
                    break;
            }
        }
        if (undoList.isEmpty()) {
            undoBtn.setEnabled(false);
            eraseBtn.setEnabled(false);
            saveBtn.setEnabled(false);
        }
        repaint();
    }

    /**
     * Vacía todos los objetos del mapa
     */
    private void clearMap() {
        Object[] options = {"Sí", "No"};
        int selected = JOptionPane.showOptionDialog(frame,
                "¿Quiere eliminar completamente todos los objetos añadidos al mapa?",
                "Eliminar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        if (selected == 0) {
            obstacles.clear();
            stopItems.clear();
            balls.clear();
            speedVecs.clear();
            undoBtn.setEnabled(false);
            eraseBtn.setEnabled(false);
            saveBtn.setEnabled(false);
            saved = false;
            repaint();
        }
    }

    /**
     * Crea el panel de control de los parámetros de los objetos
     */
    private void createObjectParamForm() {
        selectedItemLb = new JLabel("Bola", SwingConstants.LEFT);
        selectedItemLb.setFont(new Font(selectedItemLb.getFont().getName(), Font.BOLD, 28));
        selectedItemLb.setSize(500, 30);
        selectedItemLb.setLocation(400, 5);
        controlPanel.add(selectedItemLb);

        posXLb = new JLabel("Pos. X: ", SwingConstants.RIGHT);
        posXLb.setSize(75, 20);
        posXLb.setLocation(357, 50);
        controlPanel.add(posXLb);

        posXTf = new JTextField("");
        posXTf.setSize(75, 20);
        posXTf.setLocation(435, 50);
        posXTf.setEditable(false);
        controlPanel.add(posXTf);

        posYLb = new JLabel("Pos. Y: ", SwingConstants.RIGHT);
        posYLb.setSize(75, 20);
        posYLb.setLocation(357, 90);
        controlPanel.add(posYLb);

        posYTf = new JTextField("");
        posYTf.setSize(75, 20);
        posYTf.setLocation(435, 90);
        posYTf.setEditable(false);
        controlPanel.add(posYTf);

        widthLb = new JLabel("Ancho: ", SwingConstants.RIGHT);
        widthLb.setSize(75, 20);
        widthLb.setLocation(500, 50);
        widthLb.setVisible(false);
        controlPanel.add(widthLb);

        widthTf = new JTextField("");
        widthTf.setSize(75, 20);
        widthTf.setLocation(578, 50);
        widthTf.setEditable(false);
        widthTf.setVisible(false);
        controlPanel.add(widthTf);

        heightLb = new JLabel("Alto: ", SwingConstants.RIGHT);
        heightLb.setSize(75, 20);
        heightLb.setLocation(500, 90);
        heightLb.setVisible(false);
        controlPanel.add(heightLb);

        heightTf = new JTextField("");
        heightTf.setSize(75, 20);
        heightTf.setLocation(578, 90);
        heightTf.setEditable(false);
        heightTf.setVisible(false);
        controlPanel.add(heightTf);

        NumberFormat intFormat = NumberFormat.getIntegerInstance();
        NumberFormatter numberFormatter = new NumberFormatter(intFormat);
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setAllowsInvalid(false);
        numberFormatter.setMinimum(0);

        radiusLb = new JLabel("Radio: ", SwingConstants.RIGHT);
        radiusLb.setSize(75, 20);
        radiusLb.setLocation(500, 50);
        controlPanel.add(radiusLb);

        radiusInfoLb = new JLabel("15", SwingConstants.RIGHT);
        radiusInfoLb.setSize(75, 20);
        radiusInfoLb.setLocation(615, 50);
        controlPanel.add(radiusInfoLb);

        radiusSl = new JSlider(JSlider.HORIZONTAL, MIN_RADIUS, MAX_RADIUS, 15);
        radiusSl.setSize(105, 20);
        radiusSl.setLocation(570, 52);
        radiusSl.addChangeListener((ChangeEvent e) -> {
            radiusInfoLb.setText("" + radiusSl.getValue());
        });
        controlPanel.add(radiusSl);

        angleLb = new JLabel("Ángulo: ", SwingConstants.RIGHT);
        angleLb.setSize(75, 50);
        angleLb.setLocation(700, 24);
        controlPanel.add(angleLb);

        angleInfoLb = new JLabel("0,00º", SwingConstants.RIGHT);
        angleInfoLb.setSize(75, 20);
        angleInfoLb.setLocation(757, 75);
        controlPanel.add(angleInfoLb);

        angleSl = new RoundSlider(Math.toRadians(90));
        angleSl.setSize(50, 50);
        angleSl.setLocation(735, 63);
        angleSl.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                DecimalFormat angleFormat = new DecimalFormat("0.0");
                angleInfoLb.setText(angleFormat.format(Math.toDegrees(angleSl.getAngle())) + "º");
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });
        controlPanel.add(angleSl);

        speedLb = new JLabel("Vel.: ", SwingConstants.RIGHT);
        speedLb.setSize(75, 20);
        speedLb.setLocation(500, 90);
        controlPanel.add(speedLb);

        speedInfoLb = new JLabel("15", SwingConstants.RIGHT);
        speedInfoLb.setSize(75, 20);
        speedInfoLb.setLocation(615, 90);
        controlPanel.add(speedInfoLb);

        speedSl = new JSlider(JSlider.HORIZONTAL, MIN_SPEED, MAX_SPEED, 5);
        speedSl.setSize(105, 20);
        speedSl.setLocation(570, 92);
        speedSl.addChangeListener((ChangeEvent e) -> {
            speedInfoLb.setText("" + speedSl.getValue());
        });
        controlPanel.add(speedSl);

        ballTypeLb = new JLabel("Tipo de bola: ", SwingConstants.RIGHT);
        ballTypeLb.setSize(75, 20);
        ballTypeLb.setLocation(870, 50);
        controlPanel.add(ballTypeLb);

        ballTypeCb = new JComboBox();
        ballTypeCb.addItem("Normal");
        ballTypeCb.addItem("Explosive");
        ballTypeCb.addItem("Bullet");
        ballTypeCb.setSize(75, 20);
        ballTypeCb.setLocation(870, 70);
        controlPanel.add(ballTypeCb);
    }

    /**
     * Crea el panel del lienzo del mapa
     */
    private void createMapPanel() {
        mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintMap(g);
            }
        };
        mapPanel.setPreferredSize(new Dimension(MAPPANEL_WIDTH, MAPPANEL_HEIGHT));
        mapPanel.setBackground(Color.BLACK);

        MouseAdapter ma;
        ma = new MouseAdapter() {

            private Point clickPoint;

            @Override
            public void mousePressed(MouseEvent e) {
                if (!saved) {
                    clickPoint = e.getPoint();
                    switch (item) {
                        case "BALL":
                            int ballRadius = radiusSl.getValue();
                            int speed = speedSl.getValue();
                            float angle = (float) Math.toDegrees(angleSl.getAngle());
                            String ballType = "N";
                            switch (ballTypeCb.getSelectedItem().toString()) {
                                case "Normal":
                                    ballType = "N";
                                    break;
                                case "Explosive":
                                    ballType = "E";
                                    break;
                                case "Bullet":
                                    ballType = "B";
                                    break;
                                default:
                                    break;
                            }
                            ball = new Ball(clickPoint.x, clickPoint.y, speed, 0, ballRadius, angle, ballType);
                            break;
                        case "OBSTACLE":
                            obstacle = null;
                            break;
                        case "BOTTLENECK":
                            stopItem = null;
                            break;
                        default:
                            // nada
                            break;
                    }
                    mapPanel.repaint();
                    posXTf.setText("" + clickPoint.x);
                    posYTf.setText("" + clickPoint.y);
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (!saved) {
                    Point dragPoint = e.getPoint();
                    int x, y, width, height;
                    switch (item) {
                        case "BALL":
                            //ball = dragPoint;
                            ball.setX(dragPoint.x);
                            ball.setY(dragPoint.y);
                            posXTf.setText("" + dragPoint.x);
                            posYTf.setText("" + dragPoint.y);
                            break;
                        case "OBSTACLE":
                            x = Math.min(clickPoint.x, dragPoint.x);
                            y = Math.min(clickPoint.y, dragPoint.y);
                            width = Math.max(clickPoint.x, dragPoint.x) - x;
                            if (width > MAX_WIDTH) {
                                width = MAX_WIDTH;
                            }
                            height = Math.max(clickPoint.y, dragPoint.y) - y;
                            if (height > MAX_HEIGHT) {
                                height = MAX_HEIGHT;
                            }
                            if (width > 0 && height > 0) {
                                if (obstacle == null) {
                                    obstacle = new Obstacle((float) x, (float) y, (float) width, (float) height);
                                } else {
                                    obstacle.setX(x);
                                    obstacle.setY(y);
                                    obstacle.setWidth(width);
                                    obstacle.setHeight(height);
                                }
                                widthTf.setText("" + width);
                                heightTf.setText("" + height);
                            }
                            break;
                        case "BOTTLENECK":
                            x = Math.min(clickPoint.x, dragPoint.x);
                            y = Math.min(clickPoint.y, dragPoint.y);
                            width = Math.max(clickPoint.x, dragPoint.x) - x;
                            if (width > MAX_WIDTH) {
                                width = MAX_WIDTH;
                            }
                            height = Math.max(clickPoint.y, dragPoint.y) - y;
                            if (height > MAX_HEIGHT) {
                                height = MAX_HEIGHT;
                            }
                            if (width > 0 && height > 0) {
                                if (stopItem == null) {
                                    stopItem = new StopItem(x, y, width, height);
                                } else {
                                    stopItem.setX(x);
                                    stopItem.setY(y);
                                    stopItem.setWidth(width);
                                    stopItem.setHeight(height);
                                }
                                widthTf.setText("" + width);
                                heightTf.setText("" + height);
                            }
                            break;
                        default:
                            // nada
                            break;
                    }
                    mapPanel.repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!saved) {
                    undoList.push(item);
                    switch (item) {
                        case "BALL":
                            if (isValidPosBall(ball)) {
                                balls.add(ball);
                                Point p1 = new Point(0, 0);
                                Point p2 = new Point(0, 0);
                                if (ball.getSpeed() != 0) {
                                    p1 = new Point((int) ball.getX(), (int) ball.getY());
                                    p2 = new Point((int) (ball.getX() + (ball.getSpeedx() * 3)), (int) (ball.getY() + (ball.getSpeedy() * 3)));
                                }
                                Point[] p = {p1, p2};
                                speedVecs.add(p);
                            } else {
                                JOptionPane.showMessageDialog(null, "Los objetos no pueden ser superpuestos ni colocados\ndemasiado cerca de los bordes de la pantalla.", "Error en la creación del objeto", JOptionPane.ERROR_MESSAGE);
                            }
                            ball = null;
                            break;
                        case "OBSTACLE":
                            if (obstacle != null) {
                                if (isValidPosObstacle(obstacle)) {
                                    obstacles.add(obstacle);
                                } else {
                                    JOptionPane.showMessageDialog(null, "Los objetos no pueden ser superpuestos ni colocados\ndemasiado cerca de los bordes de la pantalla.", "Error en la creación del objeto", JOptionPane.ERROR_MESSAGE);
                                }
                                obstacle = null;
                            }
                            break;
                        case "BOTTLENECK":
                            if (stopItem != null) {
                                if (isValidPosStopItem(stopItem)) {
                                    stopItems.add(stopItem);
                                } else {
                                    JOptionPane.showMessageDialog(null, "Los objetos no pueden ser superpuestos ni colocados\ndemasiado cerca de los bordes de la pantalla.", "Error en la creación del objeto", JOptionPane.ERROR_MESSAGE);
                                }
                                stopItem = null;
                            }
                            break;
                        default:
                            // nada
                            break;
                    }
                    undoBtn.setEnabled(true);
                    eraseBtn.setEnabled(true);
                    saveBtn.setEnabled(true);
                    mapPanel.repaint();
                }
            }
        };

        // Añade los escuchadores de clicks
        mapPanel.addMouseListener(ma);
        mapPanel.addMouseMotionListener(ma);
    }

    /**
     * Comprueba si la bola está siendo colocada en una posición válida
     * @param b
     * @return 
     */
    private boolean isValidPosBall(Ball b) {
        if (b.getX() - b.getRadius() <= 0 + MIN_MARGIN
                || b.getY() - b.getRadius() <= 0 + MIN_MARGIN
                || b.getX() + b.getRadius() >= MAPPANEL_WIDTH - MIN_MARGIN
                || b.getY() + b.getRadius() >= MAPPANEL_HEIGHT - MIN_MARGIN) {
            return false;
        }
        for (Ball ba : balls) {
            if (Math.sqrt((b.getX() - ba.getX()) * (b.getX() - ba.getX()) + (b.getY() - ba.getY()) * (b.getY() - ba.getY())) < (b.getRadius() + ba.getRadius())) {
                return false;
            }
        }
        for (Obstacle obs : obstacles) {
            if (obs.intersects(b)) {
                return false;
            }
        }
        for (StopItem stop : stopItems) {
            if (stop.intersects(b)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Comprueba si el obstáculo está siendo colocado en una posición válida
     * @param o
     * @return 
     */
    private boolean isValidPosObstacle(Obstacle o) {
        if (o.getX() <= 0 + MIN_MARGIN
                || o.getY() <= 0 + MIN_MARGIN
                || o.getX() + o.getWidth() >= MAPPANEL_WIDTH - MIN_MARGIN
                || o.getY() + o.getHeight() >= MAPPANEL_HEIGHT - MIN_MARGIN) {
            return false;
        }
        Rectangle oR = new Rectangle((int) o.getX(), (int) o.getY(), (int) o.getWidth(), (int) o.getHeight());
        for (Ball ba : balls) {
            if (o.intersects(ba)) {
                return false;
            }
        }
        for (Obstacle obs : obstacles) {
            Rectangle obsR = new Rectangle((int) obs.getX(), (int) obs.getY(), (int) obs.getWidth(), (int) obs.getHeight());
            if (oR.intersects(obsR)) {
                return false;
            }
        }
        for (StopItem stop : stopItems) {
            Rectangle stopR = new Rectangle((int) stop.getX(), (int) stop.getY(), (int) stop.getWidth(), (int) stop.getHeight());
            if (oR.intersects(stopR)) {
                return false;
            }
        }
        return true;
    }

    /** 
     * Comprueba si el semáforo está siendo colocado en una posición válida
     * @param si
     * @return 
     */
    private boolean isValidPosStopItem(StopItem si) {
        if (si.getX() <= 0 + MIN_MARGIN
                || si.getY() <= 0 + MIN_MARGIN
                || si.getX() + si.getWidth() >= MAPPANEL_WIDTH - MIN_MARGIN
                || si.getY() + si.getHeight() >= MAPPANEL_HEIGHT - MIN_MARGIN) {
            return false;
        }
        Rectangle siR = new Rectangle((int) si.getX(), (int) si.getY(), (int) si.getWidth(), (int) si.getHeight());
        for (Ball ba : balls) {
            if (si.intersects(ba)) {
                return false;
            }
        }
        for (Obstacle obs : obstacles) {
            Rectangle obsR = new Rectangle((int) obs.getX(), (int) obs.getY(), (int) obs.getWidth(), (int) obs.getHeight());
            if (siR.intersects(obsR)) {
                return false;
            }
        }
        for (StopItem stop : stopItems) {
            Rectangle stopR = new Rectangle((int) stop.getX(), (int) stop.getY(), (int) stop.getWidth(), (int) stop.getHeight());
            if (siR.intersects(stopR)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Dibuja los objetos colocados en el lienzo del mapa
     * @param g 
     */
    private void paintMap(Graphics g) {

        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(MIN_MARGIN, MIN_MARGIN, MAPPANEL_WIDTH - MIN_MARGIN, MAPPANEL_HEIGHT - MIN_MARGIN);

        // Pintado de los objetos ya existentes
        for (Ball b : balls) {
            b.draw(g);
        }
        for (Obstacle o : obstacles) {
            o.draw(g);
        }
        for (StopItem si : stopItems) {
            si.draw(g);
        }

        for (Point[] p : speedVecs) {
            if (p[0] != p[1]) {
                g.setColor(Color.white);
                g.drawLine(p[0].x, p[0].y, p[1].x, p[1].y);
                drawVecArrow((Graphics2D) g, p[1], p[0], Color.WHITE);
            }
        }

        // Pintado del objeto seleccionado
        if (ball != null) {
            ball.draw(g);
        }
        if (obstacle != null) {
            obstacle.draw(g);
        }
        if (stopItem != null) {
            stopItem.draw(g);
        }

        // Si el mapa ya ha sido guardado pinta el mensaje correspondiente
        if (saved) {
            g.setColor(Color.LIGHT_GRAY);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 25));
            g.drawString("El mapa ya está guardado, no puede modificarse", 200, 100);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 15));
            g.drawString("Seleccione 'Crear mapa nuevo' en el menú de 'Opciones', o arranque la simulación", 200, 120);
        }
    }

    /**
     * Dibuja la forma de punta de flecha en los vectores de velocidad de las bolas
     * @param g2
     * @param head
     * @param tail
     * @param color 
     */
    private void drawVecArrow(Graphics2D g2, Point head, Point tail, Color color) {
        double headAngle = Math.toRadians(30);
        double headLength = 12;
        g2.setPaint(color);
        double dy = head.y - tail.y;
        double dx = head.x - tail.x;
        double theta = Math.atan2(dy, dx);
        double x, y, rho = theta + headAngle;
        for (int j = 0; j < 2; j++) {
            x = head.x - headLength * Math.cos(rho);
            y = head.y - headLength * Math.sin(rho);
            g2.draw(new Line2D.Double(head.x, head.y, x, y));
            rho = theta - headAngle;
        }
    }

}
