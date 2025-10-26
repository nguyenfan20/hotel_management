
package GUI.dashboard;

import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import model.Model_Card;
import model.StatusType;


public class Form_Home extends javax.swing.JPanel {


    public Form_Home() {
        initComponents();
        card1.setData(new Model_Card(new ImageIcon(getClass().getResource("/icon/stock.png")), "Stock Total", "20000$", "Increased by 60%"));
        card2.setData(new Model_Card(new ImageIcon(getClass().getResource("/icon/profit.png")), "Total Profit", "150000$", "Increased by 60%"));
        card3.setData(new Model_Card(new ImageIcon(getClass().getResource("/icon/flag.png")), "Unique Visitors", "30000$", "Increased by 60%"));
        //add row table
        spTable.setVerticalScrollBar(new ScrollBar());
        spTable.getVerticalScrollBar().setBackground(Color.WHITE);
        spTable.getViewport().setBackground(Color.WHITE);
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        spTable.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);
        table.addRow (new Object []{"Mike Bhand", "mikehussy@gmail.com", "Admin","25 Apr, 2018",StatusType.PENDING});
        table.addRow (new Object []{"Andrew Strauss", "andrewstrauss@gmail.com", "Editor", "25 Apr, 2018", StatusType.APPROVED});
        table.addRow (new Object []{"Ross Kopelman", "rosskopelman@gmail.com", "Subscriber", "25 Apr, 2018", StatusType.APPROVED});
        table.addRow (new Object []{"Mike Hussy", "mikehussy@gmail.com", "Admin", "25 Apr, 2018", StatusType.REJECT});
        table.addRow (new Object []{"Kevin Pietersen", "kevinpietersen@gmail.com", "Admin", "25 Apr, 2018", StatusType.PENDING});
        table.addRow (new Object []{"Andrew Strauss", "andrewstrauss@gmail.com", "Editor", "25 Apr, 2018", StatusType.APPROVED});
        table.addRow (new Object []{"Ross Kopelman", "rosskopelman@gmail.com", "Subscriber", "25 Apr, 2018", StatusType.APPROVED});
        table.addRow (new Object []{"Mike Hussy", "mikehussy@gmail.com", "Admin", "25 Apr, 2018", StatusType.REJECT});
        table.addRow (new Object []{"Kevin Pietersen", "kevinpietersen@gmail.com", "Admin", "25 Apr, 2018", StatusType.PENDING});
        table.addRow (new Object []{"Andrew Strauss", "andrewstrauss@gmail.com", "Editor", "25 Apr, 2018", StatusType.APPROVED});
        table.addRow (new Object []{"Ross Kopelman", "rosskopelman@gmail.com", "Subscriber", "25 Apr, 2018", StatusType.APPROVED});
        table.addRow (new Object []{"Mike Hussy", "mikehussy@gmail.com", "Admin", "25 Apr, 2018", StatusType.REJECT});
        table.addRow (new Object []{"Kevin Pietersen", "kevinpietersen@gmail.com", "Admin", "25 Apr, 2018", StatusType.PENDING});
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        card1 = new GUI.dashboard.Card();
        card2 = new GUI.dashboard.Card();
        card3 = new GUI.dashboard.Card();
        paneBorder1 = new GUI.dashboard.PaneBorder();
        jLabel1 = new javax.swing.JLabel();
        spTable = new javax.swing.JScrollPane();
        table = new GUI.dashboard.Table();

        panel.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        card1.setColor1(new java.awt.Color(142, 142, 250));
        card1.setColor2(new java.awt.Color(123, 123, 245));
        panel.add(card1);

        card2.setColor1(new java.awt.Color(186, 123, 247));
        card2.setColor2(new java.awt.Color(167, 94, 236));
        panel.add(card2);

        card3.setColor1(new java.awt.Color(241, 208, 62));
        card3.setColor2(new java.awt.Color(211, 184, 61));
        panel.add(card3);

        paneBorder1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(127, 127, 127));
        jLabel1.setText("Standard Table Design");

        spTable.setBorder(null);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Email", "User Type", "Joined", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        spTable.setViewportView(table);

        javax.swing.GroupLayout paneBorder1Layout = new javax.swing.GroupLayout(paneBorder1);
        paneBorder1.setLayout(paneBorder1Layout);
        paneBorder1Layout.setHorizontalGroup(
            paneBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneBorder1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(paneBorder1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spTable))
        );
        paneBorder1Layout.setVerticalGroup(
            paneBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneBorder1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spTable, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(paneBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, 875, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(paneBorder1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private GUI.dashboard.Card card1;
    private GUI.dashboard.Card card2;
    private GUI.dashboard.Card card3;
    private javax.swing.JLabel jLabel1;
    private GUI.dashboard.PaneBorder paneBorder1;
    private javax.swing.JPanel panel;
    private javax.swing.JScrollPane spTable;
    private GUI.dashboard.Table table;
    // End of variables declaration//GEN-END:variables
}
