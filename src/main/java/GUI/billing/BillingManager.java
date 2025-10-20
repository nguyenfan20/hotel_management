package GUI.billing;

import GUI.billing.discount.Discount;
import GUI.billing.invoice.Invoice;
import GUI.billing.payment.Payment;

import javax.swing.*;

public class BillingManager extends JFrame {
    private JTabbedPane tabbedPane;

    public BillingManager() {
        setTitle("Quản lý thanh toán và hóa đơn");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);

        tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.TOP);

        // Add tabs
//        tabbedPane.addTab("Quản lý dịch vụ", new Service());
        tabbedPane.addTab("Quản lý hóa đơn", new Invoice());
        tabbedPane.addTab("Quản lý chiết khấu", new Discount());
        tabbedPane.addTab("Quản lý thanh toán", new Payment());

        add(tabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BillingManager frame = new BillingManager();
            frame.setVisible(true);
        });
    }
}
