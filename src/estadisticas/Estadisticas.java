package estadisticas;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Toni
 */
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.border.BevelBorder;
import org.physicballs.items.StatisticsData;

public final class Estadisticas extends javax.swing.JFrame {

    private final Menu menu, menuGeneral;
    private ArrayList<StatisticsData> listDataStatistics = new ArrayList();
    
    private float velocitat = 0;
    private int totalBolles = 0, ultimNBolles = 0;
    private final ArrayList<Integer> pantalles = new ArrayList<>();


    public Estadisticas() {
        initComponents();
        setSize(700, 600);
        menuGeneral = new Menu();
        crearEstructuraDelMenuGeneral(menuGeneral);
        menuGeneral.setBackground(new Color(111, 111, 111));
        menuGeneral.setForeground(Color.white);
        menuGeneral.setFont(new Font("monospaced", Font.PLAIN, 17));
        menuGeneral.setMenuBorders(new BevelBorder(BevelBorder.RAISED));
        menuGeneral.setIcon("menuGeneral", "resources/statistics.png");
        panel.add(menuGeneral);

        menu = new Menu();
        panel.add(menu);
    }

    public void setData(StatisticsData data) {
        try {
            listDataStatistics.add(data);
            ItemPrincipal isMenu = menu.getMenu("menu" + data.nPantalla);
            if (isMenu == null) {
                addMenu(menu, data);
                menu.setBackground(new Color(111, 111, 111));
                menu.setForeground(Color.white);
                menu.setFont(new Font("monospaced", Font.PLAIN, 15));
                menu.setMenuBorders(new BevelBorder(BevelBorder.RAISED));
                menu.repaint();
                pantalles.add(data.nPantalla);
            }
            if(velocitat < data.velocitatM){
                velocitat = data.velocitatM;
                menu.setProgresMaxValue((int)velocitat*100);
            }
            float ec = (float) (0.5 * data.velocitat * data.velocitat * data.massa);
            menu.setText("velocitat" + data.nPantalla, "Velocitat: " + String.valueOf(data.velocitat) + " m/s");
            menu.setText("velocitatM" + data.nPantalla, "Velocitat mitjana: " + String.valueOf(data.velocitatM) + " m/s");
            menu.setProgres("bar" + data.nPantalla, (int) (data.velocitatM * 100));
            menu.setText("acceleracio" + data.nPantalla, "Acceleració: " + String.valueOf(data.acceleracio) + " m/s2");
            menu.setText("acceleracioM" + data.nPantalla, "Acceleració mitjana: " + String.valueOf(data.acceleracioM) + " m/s2");
            menu.setText("massa" + data.nPantalla, "Massa: " + String.valueOf(data.massa) + " Kg");
            menu.setText("massaM" + data.nPantalla, "Massa mitjana: " + String.valueOf(data.massaM) + " Kg");
            menu.setText("bolles" + data.nPantalla, "N Bolles: " + String.valueOf(data.nBolles));
            menu.setText("EC" + data.nPantalla, "Ec: " + String.valueOf(ec) + " J");
            if (data.nPantalla == pantalles.get(0)) {
                listDataStatistics = setGeneralStatistics(listDataStatistics);
            }
        } catch (Exception ex) {
            System.out.println("Error setData\n " + ex);
        }
    }

    public void addMenu(Menu target, StatisticsData data) {
        target.añadirNuevoMenu("menu" + data.nPantalla, "estadistiques " + data.nPantalla);
        target.añadirIremSecundario("menu" + data.nPantalla, "velocitat" + data.nPantalla, "Velocitat: " + data.velocitat);
        target.añadirIremSecundario("menu" + data.nPantalla, "velocitatM" + data.nPantalla, "Velocitat: " + data.velocitatM);
        target.añadirItemSecundarioProgre("menu" + data.nPantalla, 0, 0, (int)velocitat*100, "bar" + data.nPantalla);
        target.añadirIremSecundario("menu" + data.nPantalla, "acceleracio" + data.nPantalla, "Acceleració: " + data.acceleracio);
        target.añadirIremSecundario("menu" + data.nPantalla, "acceleracioM" + data.nPantalla, "Acceleració: " + data.acceleracioM);
        target.añadirIremSecundario("menu" + data.nPantalla, "massa" + data.nPantalla, "Massa: " + data.massa);
        target.añadirIremSecundario("menu" + data.nPantalla, "massaM" + data.nPantalla, "Massa: " + data.massaM);
        target.añadirIremSecundario("menu" + data.nPantalla, "bolles" + data.nPantalla, "N Bolles: " + data.nBolles);
        target.añadirIremSecundario("menu" + data.nPantalla, "EC" + data.nPantalla, "Ec: " + data.nBolles);

        target.setIcons("menu" + data.nPantalla);
        target.calcularEspacio();
    }
    
    public void crearEstructuraDelMenuGeneral(Menu target) {
        target.añadirNuevoMenu("menuGeneral", "Estadistiques generals");
        target.añadirIremSecundario("menuGeneral", "velocitat", "Velocitat maxima: 0 m/s");
        target.añadirIremSecundario("menuGeneral", "velocitatM", "Velocitat: 0 m/s");
        target.añadirIremSecundario("menuGeneral", "acceleracio", "Acceleració total: 0 m/s2");
        target.añadirIremSecundario("menuGeneral", "acceleracioM", "Acceleració: 0 m/s2");
        target.añadirIremSecundario("menuGeneral", "massa", "Massa: 0");
        target.añadirIremSecundario("menuGeneral", "massaM", "Massa mitjana: 0");
        target.añadirIremSecundario("menuGeneral", "bollesTotal", "N Bolles Totals: 0");
        target.añadirIremSecundario("menuGeneral", "bolles", "N Bolles: 0");
        target.añadirIremSecundario("menuGeneral", "EC", "Ec: 0");

        target.setIcons("menuGeneral");
        target.calcularEspacio();
    }

    private ArrayList<StatisticsData> setGeneralStatistics(ArrayList<StatisticsData> data) {
        try {
            StatisticsData generalData = new StatisticsData(0, 0, 0, 0, 0, 0, 0);
            float ec = 0;
            for (StatisticsData statisticsData : data) {
                generalData.velocitat += statisticsData.velocitat;
                generalData.velocitatM += statisticsData.velocitatM;
                generalData.acceleracio += statisticsData.acceleracio;
                generalData.acceleracioM += statisticsData.acceleracioM;
                generalData.massa += statisticsData.massa;
                generalData.massaM += statisticsData.massaM;
                generalData.nBolles += statisticsData.nBolles;
                ec += (float) (0.5 * statisticsData.velocitat * statisticsData.velocitat * statisticsData.massa);
            }
            menuGeneral.setText("velocitat", "Velocitat maxima: " + /*String.valueOf(generalData.velocitat / data.size())*/velocitat + " m/s");
            menuGeneral.setText("velocitatM", "Velocitat mitjana: " + String.format("%.2f", generalData.velocitatM / data.size()) + " m/s");
            menuGeneral.setText("acceleracio", "Acceleració: " + String.valueOf(generalData.acceleracio / data.size()) + " m/s2");
            menuGeneral.setText("acceleracioM", "Acceleració total: " + String.valueOf(generalData.acceleracioM / data.size()) + " m/s2");
            menuGeneral.setText("massa", "Massa total: " + String.format("%.2f",generalData.massa) + " Kg");
            menuGeneral.setText("massaM", "Massa mitjana: " + String.format("%.2f",generalData.massaM / data.size()) + " Kg");
            menuGeneral.setText("bolles", "N Bolles: " + String.valueOf(generalData.nBolles));
            menuGeneral.setText("EC", "Ec: " + String.valueOf(ec / data.size())+" J");
            if(ultimNBolles < generalData.nBolles){
                totalBolles += generalData.nBolles - ultimNBolles;
            }
            ultimNBolles = generalData.nBolles;
            menuGeneral.setText("bollesTotal", "N Bolles Totals: " + totalBolles);
        } catch (Exception e) {
            System.err.println("Error en el setGeneral Statistics\n " + e);
        }

        return new ArrayList<>();
    }
    
    public void Disconect(int i){
        try {
            menu.setIconDisconected("menu"+i);
            for (int x = 0; x<pantalles.size(); x++) {
                if(i == pantalles.get(x)){
                    pantalles.remove(x);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro en el metodo disconecte de la clase estadisticas \n" +e);
        }
        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        panel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 204, 204));

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridLayout(1, 3, 30, 0));

        panel.setBackground(new java.awt.Color(153, 153, 153));
        panel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.LINE_AXIS));
        jPanel1.add(panel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 678, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new Estadisticas().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
}
