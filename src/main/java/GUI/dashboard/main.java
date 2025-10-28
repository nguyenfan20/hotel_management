package GUI.dashboard;

import GUI.account.Role;
import GUI.account.UserAccount;
import GUI.billing.discount.Discount;
import GUI.billing.invoice.Invoice;
import GUI.billing.payment.Payment;
import GUI.operation.housekeeping.HousekeepingTask;
import GUI.operation.maintenance.MaintenanceTicket;
import GUI.service.ServiceForm;
import GUI.booking.BookingGUI;
import GUI.custom.CustomGUI;
import GUI.room.Amenity;
import GUI.room.Room;
import GUI.room.RoomType;
import GUI.service.ServiceOrdersform;
import event.EventMenuSelected;
import java.awt.Color;
import javax.swing.JComponent;

public class main extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(main.class.getName());
    private Form_Home home;

    private Role role;
    private UserAccount userAccount;
    
    private CustomGUI customGUI;
    private BookingGUI bookingGUI;

    private RoomType roomType;
    private Room room;
    private Amenity amenity;

    private ServiceForm serviceForm;
    private ServiceOrdersform serviceOrdersform;

    private Invoice invoice;
    private Discount discount;
    private Payment payment;

    private HousekeepingTask housekeepingTask;
    private MaintenanceTicket maintenanceTicket;
    
    public main() {
        initComponents();
        setBackground(new Color(0, 0, 0, 0));
        home = new Form_Home();

        role = new Role();
        userAccount = new UserAccount();

        customGUI = new CustomGUI();
        bookingGUI = new BookingGUI();

        roomType = new RoomType();
        room = new Room();
        amenity = new Amenity();

        serviceForm = new ServiceForm();
        serviceOrdersform = new ServiceOrdersform();
        
        invoice = new Invoice();
        discount = new Discount();
        payment = new Payment();
        
        housekeepingTask = new HousekeepingTask();
        maintenanceTicket = new MaintenanceTicket();

        Menu menu = new Menu();
        add(menu);
        menu.initMoving(main.this);
        setLocationRelativeTo(null);
        menu2.addEventMenuSelected(new EventMenuSelected() {
            @Override
            public void selected(int index) {
                if (index == 0) {
                    setForm(home);
                } else if (index == 2) {
                    setForm(role);
                } else if (index == 3) {
                    setForm(userAccount);
                } else if (index == 5) {
                    setForm(customGUI);
                } else if (index == 6) {
                    setForm(bookingGUI);
                } else if (index == 8) {
                    setForm(roomType);
                } else if (index == 9) {
                    setForm(room); 
                } else if (index == 10) {
                    setForm(amenity);
                } else if (index == 12) {
                    setForm(serviceForm);
                } else if (index == 13) {
                    setForm(serviceOrdersform);
                } else if (index == 15) {
                    setForm(invoice);
                } else if (index == 16) {
                    setForm(discount);
                } else if (index == 17) {
                    setForm(payment);
                } else if (index == 19) {
                    setForm(housekeepingTask);
                } else if (index == 20) {
                    setForm(maintenanceTicket);
                } else if (index == 22) {
                    
                } else if (index == 23) {
                    
                } else if (index == 24) {
                    
                }
            }
        });
        //set when system open start with form_home
        setForm(new Form_Home());
    }
    
    private void setForm(JComponent com){
        mainPanel.removeAll();
        mainPanel.add(com);
        mainPanel.repaint();
        mainPanel.revalidate();
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        paneBorder1 = new GUI.dashboard.PaneBorder();
        menu2 = new GUI.dashboard.Menu();
        mainPanel = new javax.swing.JPanel();
        header2 = new GUI.dashboard.Header();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        paneBorder1.setOpaque(true);

        menu2.setOpaque(true);

        mainPanel.setOpaque(false);
        mainPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout paneBorder1Layout = new javax.swing.GroupLayout(paneBorder1);
        paneBorder1.setLayout(paneBorder1Layout);
        paneBorder1Layout.setHorizontalGroup(
            paneBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneBorder1Layout.createSequentialGroup()
                .addComponent(menu2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(paneBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 965, Short.MAX_VALUE)
                    .addGroup(paneBorder1Layout.createSequentialGroup()
                        .addGap(0, 0, 0)
                        .addComponent(header2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        paneBorder1Layout.setVerticalGroup(
            paneBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(menu2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(paneBorder1Layout.createSequentialGroup()
                .addComponent(header2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 577, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(paneBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(paneBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new main().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private GUI.dashboard.Header header2;
    private javax.swing.JPanel mainPanel;
    private GUI.dashboard.Menu menu2;
    private GUI.dashboard.PaneBorder paneBorder1;
    // End of variables declaration//GEN-END:variables
}
