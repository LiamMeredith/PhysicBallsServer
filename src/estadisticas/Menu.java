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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.Border;

public final class Menu extends JPanel {

    // Heigth de cada menu
    private final int menusSize = 35;
    // Numero de menus 
    private int menuCount;
    // Espacio vertical libre para mostrar una rama de menu
    private int branchEspacioDisponible;
    // Cuenta cuántas filas estan libres en una rama de menú
    private int filasDisponibles;
    private ItemPrincipal lastSelectedMenu;
    // Es el tamaño vertical de la rama que se abre
    private int tamañoAbierto;
    // Es el tamaño vertical de la rama que se cierra
    private int tamañoCerrado;
    // Arbol de menús con el identificador de cada elemento de menú.
    private TreeMap<ItemPrincipal, List<ItemSecundario>> leafMap;
    private TreeMap<ItemPrincipal, List<ProgresItemSecundatio>> leafProgresMap;

    public Menu() {
        this.addComponentListener(getDefaultComponentAdapter());
        this.setLayout(null);
        this.leafMap = new TreeMap<ItemPrincipal, List<ItemSecundario>>();
        this.leafProgresMap = new TreeMap<ItemPrincipal, List<ProgresItemSecundatio>>();
    }
    
    public MouseAdapter getDefaultLeafMouseAdapter() {
        return new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                ItemSecundario item = (ItemSecundario) e.getSource();
                for (ItemSecundario leaf : Menu.this.getSecundarios()) {
                    leaf.setSelected(false);
                    leaf.repaint();
                }
                
                item.setSelected(true);
            }
        };
    }
    
    private MouseAdapter getDefaultMenuMouseAdapter() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                boolean lastSelected=false;
                ItemPrincipal item = (ItemPrincipal) e.getSource();
                if (item.isSelected()) {
                    lastSelectedMenu=item;
                    item.setSelected(false);
                }else{
                    for (ItemPrincipal menu : getMenus()) {
                        if (menu.isSelected()) {
                            lastSelectedMenu = menu;
                            menu.setSelected(false);
                            lastSelected=true;
                        }
                    }
                    if(!lastSelected){
                        lastSelectedMenu=null;
                    }
                    item.setSelected(true);
                }
                startAnimation();
            }
        };
    }

    private void startAnimation() {
        Thread thread = new Thread(new Runnable() {

            public void run() {
                tamañoAbierto = 0;
                tamañoCerrado = branchEspacioDisponible;
                int x = 30;
                while (tamañoCerrado > 0) {
                    tamañoAbierto += x;
                    tamañoCerrado -= x;
                    update();
                    repaint();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                tamañoAbierto = branchEspacioDisponible;
                tamañoCerrado = 0;
                    
                update();
                repaint();
            }
        });
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    public void update() {
        for (ItemPrincipal menu : getMenus()) {
            menu.getBranchPanel().updateUI();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int y = 0;
        for (ItemPrincipal menu : getMenus()) {
            menu.setSize(this.getWidth(), this.menusSize);
            menu.setLocation(0, y);

            if (menu == lastSelectedMenu && !menu.isSelected()) {
                y += this.menusSize;
                menu.getBranchPanel().setSize(this.getWidth(), this.tamañoCerrado);
                menu.getBranchPanel().setLocation(0, y);
                y += this.tamañoCerrado;
            }
            if (menu.isSelected()) {
                y += this.menusSize;
                menu.getBranchPanel().ajustarItems(filasDisponibles);
                menu.getBranchPanel().setSize(this.getWidth(), this.tamañoAbierto);
                menu.getBranchPanel().setLocation(0, y);
                y += this.tamañoAbierto;
            } else if (!menu.isSelected() && menu != lastSelectedMenu) {
                menu.getBranchPanel().setSize(0, 0);
                y += this.menusSize;
            }
        }
        update();
    }

    public List<ItemPrincipal> getMenus() {
        return new ArrayList<ItemPrincipal>(leafMap.keySet());
    }

    public ItemPrincipal getMenu(String name) {
        for (ItemPrincipal menu : leafMap.keySet()) {
            if (menu.getName().equals(name)) {
                return menu;
            }
        }
        return null;
    }

    public List<ItemSecundario> getSecundarios() {
        List<ItemSecundario> leafs = new ArrayList<ItemSecundario>();
        for (ItemPrincipal menu : leafMap.keySet()) {
            leafs.addAll(leafMap.get(menu));
        }
        return leafs;
    }
    
    public List<ProgresItemSecundatio> getProgresSecundarios() {
        List<ProgresItemSecundatio> leafs = new ArrayList<ProgresItemSecundatio>();
        for (ItemPrincipal menu : leafProgresMap.keySet()) {
            leafs.addAll(leafProgresMap.get(menu));
        }
        return leafs;
    }

    public List<ItemSecundario> getSecundariosDe(String menuName) {
        List<ItemSecundario> leafs = new ArrayList<ItemSecundario>();
        for (ItemPrincipal menu : leafMap.keySet()) {
            if (menu.getName().equals(menuName)) {
                leafs.addAll(leafMap.get(menu));
            }
        }
        return leafs;
    }

    public ItemSecundario getSecundarios(String name) {
        for (ItemSecundario leaf : getSecundarios()) {
            if (leaf.getName().equals(name)) {
                return leaf;
            }
        }
        return null;
    }
    
    public ProgresItemSecundatio getProgersSedundario(String name) {
        for (ProgresItemSecundatio leaf : getProgresSecundarios()) {
            if (leaf.getName().equals(name)) {
                return leaf;
            }
        }
        return null;
    }
    
    public void setProgresMaxValue(int value) {
        for (ProgresItemSecundatio leaf : getProgresSecundarios()) {
                leaf.setMaximum(value);
        }
    }

    public ComponentAdapter getDefaultComponentAdapter() {
        return new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                calcularEspacio();
            }
        };
    }

    public void calcularEspacio() {
        int height = getHeight();
        double scale = menusSize / 20;
        branchEspacioDisponible = height - (menuCount * menusSize);
        filasDisponibles = (int) (Math.ceil(height / (menusSize)) * scale) - menuCount + 3;
        tamañoAbierto = branchEspacioDisponible;
        tamañoCerrado = 0;
        update();
    }

    private ItemPrincipal crearItemPrincipal(String title, String name) {
        ItemPrincipal menu = new ItemPrincipal(title);
        menu.setName(name);
        add(menu);
        return menu;
    }

    private ItemSecundario crearItemSecundario(String title, String name) {
        ItemSecundario leaf = new ItemSecundario(title);
        leaf.setName(name);
        return leaf;
    }
    
    private ProgresItemSecundatio crearItemSecundarioPregres(int val, int min,int max, String name) {
        ProgresItemSecundatio leaf = new ProgresItemSecundatio(min, max, name);
        leaf.setValue(val);
        return leaf;
    }

    public void añadirIremSecundario(String menuName, String leafName, String leafTitle) {
        for (ItemPrincipal menu : getMenus()) {
            if (menu.getName().equals(menuName)) {
                ItemSecundario item = crearItemSecundario(leafTitle, leafName);
                this.leafMap.get(menu).add(item);
                menu.getBranchPanel().addItem(item);
                return;
            }
        }
    }
    public void añadirItemSecundarioProgre(String menuName, int val, int min,int max, String name) {
        for (ItemPrincipal menu : getMenus()) {
            if (menu.getName().equals(menuName)) {
                ProgresItemSecundatio item = crearItemSecundarioPregres(val, min, max, name);
                item.setStringPainted(true);
                item.setForeground(Color.GREEN);
                this.leafProgresMap.get(menu).add(item);
                menu.getBranchPanel().addItem(item);
                return;
            }
        }
    }

    public void añadirNuevoMenu(String menuName, String menuTitle) {
        List<ItemSecundario> leafs = new ArrayList<>();
        List<ProgresItemSecundatio> progresLeafs = new ArrayList<>();
        ItemPrincipal menu = crearItemPrincipal(menuTitle, menuName);
        if(!"menuGeneral".equals(menuName)){
            menu.addMouseListener(getDefaultMenuMouseAdapter());
        }
        menu.setIndex(menuCount);
        if (menuCount == 0) {
            menu.setSelected(true);
        }
        menuCount++;
        this.leafMap.put(menu, leafs);
        this.leafProgresMap.put(menu, progresLeafs);
        this.add(menu.getBranchPanel());
    }

    @Override
    public void setBackground(Color back) {
        if (this.leafMap == null) {
            return;
        }
        for (ItemPrincipal menu : leafMap.keySet()) {
            menu.setBackground(back);
            menu.getBranchPanel().setBackground(back);
            for (ItemSecundario leaf : leafMap.get(menu)) {
                leaf.setBackground(back);
            }
        }
    }

    public void setText(String leafName, String leafText) {
            getSecundarios(leafName).setText(leafText);
    }
    
    public void setProgres(String leafName, int value) {
        ProgresItemSecundatio progres= getProgersSedundario(leafName);
        progres.setValue(value);
        progres.repaint();
    }
    
    public void setMenuBorders(Border border) {
        for (ItemPrincipal menu : getMenus()) {
            menu.setBorder(border);
        }
    }

    public void setFont(Font font) {
        if (this.leafMap == null) {
            return;
        }
        getMenus().stream().map((menu) -> {
            menu.setFont(font);
            return menu;
        }).forEachOrdered((menu) -> {
            getSecundariosDe(menu.getName()).forEach((leaf) -> {
                leaf.setFont(font);
            });
        });
    }
    
    public void setIconDisconected(String name) {
        getMenus().stream().filter((menu) -> (menu.getName().equals(name))).forEachOrdered((menu) -> {
            ImageIcon i = new ImageIcon(this.getClass().getResource("resources/disconected.png"));
            menu.setNormalIcon(i);
            menu.setSelectedIcon(i);
        });
    }
    
    public void setIcon(String name, String url) {
        getMenus().stream().filter((menu) -> (menu.getName().equals(name))).forEachOrdered((menu) -> {
            ImageIcon i = new ImageIcon(this.getClass().getResource(url));
            menu.setNormalIcon(i);
            menu.setSelectedIcon(i);
        });
    }
    
    public void setIcons(String name) {
        ImageIcon i;
        for (ItemSecundario leaf : getSecundariosDe(name)) {
            if(leaf.getName().contains("velocitat")){
                i=new ImageIcon(this.getClass().getResource("resources/speed.png"));
                leaf.setIcon(i);
            }else if(leaf.getName().contains("massa")){
                i = new ImageIcon(this.getClass().getResource("resources/masa.png"));
                leaf.setIcon(i);
            }else if(leaf.getName().contains("accel")){
                i = new ImageIcon(this.getClass().getResource("resources/accel.png"));
                leaf.setIcon(i);
            }else if(leaf.getName().contains("boll")){
                i = new ImageIcon(this.getClass().getResource("resources/ball.png"));
                leaf.setIcon(i);
            }else if(leaf.getName().contains("EC")){
                i = new ImageIcon(this.getClass().getResource("resources/Ec.png"));
                leaf.setIcon(i);
            }
        }
    }

    @Override
    public void setForeground(Color fg) {
        if (this.leafMap == null) {
            return;
        }
        for (ItemPrincipal menu : getMenus()) {
            menu.setForeground(fg);
            for (ItemSecundario leaf : getSecundariosDe(menu.getName())) {
                leaf.setForeground(fg);
            }
        }
    }
}
