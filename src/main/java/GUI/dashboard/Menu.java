package GUI.dashboard;

import event.EventMenuSelected;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JFrame;
import model.Model_Menu;

public class Menu extends javax.swing.JPanel {

    private EventMenuSelected event;

    public void addEventMenuSelected(EventMenuSelected event) {
        this.event = event;
        listMenu1.addEventMenuSelected(event);
    }

    public Menu() {
        initComponents();
        setOpaque(false);
        init();
    }

    private void init() {
        // === Thêm toàn bộ menu (iconId = "1") ===
        listMenu1.addItem(new Model_Menu("1", "Trang chủ", Model_Menu.MenuType.MENU));

        listMenu1.addItem(new Model_Menu("", "Quản lý người dùng và phân quyền", Model_Menu.MenuType.TITLE));
        listMenu1.addItem(new Model_Menu("1", "Vai trò", Model_Menu.MenuType.MENU));
        listMenu1.addItem(new Model_Menu("1", "Tài khoản người dùng", Model_Menu.MenuType.MENU));

        listMenu1.addItem(new Model_Menu("", "Quản lý đặt phòng", Model_Menu.MenuType.TITLE));
        listMenu1.addItem(new Model_Menu("1", "Khách hàng", Model_Menu.MenuType.MENU));
        listMenu1.addItem(new Model_Menu("1", "Danh sách đặt phòng", Model_Menu.MenuType.MENU));

        listMenu1.addItem(new Model_Menu("", "Quản lý phòng và tiện nghi", Model_Menu.MenuType.TITLE));
        listMenu1.addItem(new Model_Menu("1", "Loại phòng", Model_Menu.MenuType.MENU));
        listMenu1.addItem(new Model_Menu("1", "Phòng", Model_Menu.MenuType.MENU));
        listMenu1.addItem(new Model_Menu("1", "Tiện nghi", Model_Menu.MenuType.MENU));

        listMenu1.addItem(new Model_Menu("", "Quản lý dịch vụ", Model_Menu.MenuType.TITLE));
        listMenu1.addItem(new Model_Menu("1", "Dịch vụ", Model_Menu.MenuType.MENU));
        listMenu1.addItem(new Model_Menu("1", "Đơn đặt dịch vụ", Model_Menu.MenuType.MENU));

        listMenu1.addItem(new Model_Menu("", "Quản lý hóa đơn & thanh toán", Model_Menu.MenuType.TITLE));
        listMenu1.addItem(new Model_Menu("1", "Hóa đơn", Model_Menu.MenuType.MENU));
        listMenu1.addItem(new Model_Menu("1", "Chiết khấu", Model_Menu.MenuType.MENU));
        listMenu1.addItem(new Model_Menu("1", "Thanh toán", Model_Menu.MenuType.MENU));

        listMenu1.addItem(new Model_Menu("", "Quản lý vận hành", Model_Menu.MenuType.TITLE));
        listMenu1.addItem(new Model_Menu("1", "Dọn dẹp phòng", Model_Menu.MenuType.MENU));
        listMenu1.addItem(new Model_Menu("1", "Bảo trì", Model_Menu.MenuType.MENU));

        listMenu1.addItem(new Model_Menu("", "Báo cáo & thống kê", Model_Menu.MenuType.TITLE));
        listMenu1.addItem(new Model_Menu("1", "Báo cáo doanh thu", Model_Menu.MenuType.MENU));
        listMenu1.addItem(new Model_Menu("1", "Báo cáo tình trạng phòng", Model_Menu.MenuType.MENU));
        listMenu1.addItem(new Model_Menu("1", "Báo cáo đặt phòng", Model_Menu.MenuType.MENU));

        // === Tự động điều chỉnh layout ===
        listMenu1.revalidate();
        listMenu1.repaint();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        panelMoving = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        listMenu1 = new GUI.dashboard.ListMenu();  // Đã là JScrollPane

        panelMoving.setOpaque(false);

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 18));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/logo.png")));
        jLabel1.setText("Application");

        javax.swing.GroupLayout panelMovingLayout = new javax.swing.GroupLayout(panelMoving);
        panelMoving.setLayout(panelMovingLayout);
        panelMovingLayout.setHorizontalGroup(
                panelMovingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelMovingLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                                .addContainerGap())
        );
        panelMovingLayout.setVerticalGroup(
                panelMovingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelMovingLayout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addComponent(jLabel1)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        // === Layout: listMenu1 chiếm toàn bộ không gian còn lại ===
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelMoving, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(listMenu1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(panelMoving, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(listMenu1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

    @Override
    protected void paintChildren(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint g = new GradientPaint(0, 0, Color.decode("#1c92d2"), 0, getHeight(), Color.decode("#f2fcfe"));
        g2.setPaint(g);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2.fillRect(getWidth() - 20, 0, getWidth(), getHeight());
        super.paintChildren(grphcs);
    }

    private int x, y;

    public void initMoving(JFrame fram) {
        panelMoving.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                x = e.getX();
                y = e.getY();
            }
        });
        panelMoving.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                fram.setLocation(e.getXOnScreen() - x, e.getYOnScreen() - y);
            }
        });
    }

    // Variables declaration
    private javax.swing.JLabel jLabel1;
    private GUI.dashboard.ListMenu listMenu1;
    private javax.swing.JPanel panelMoving;
}