package GUI.dashboard;

import event.EventMenuSelected;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicScrollBarUI;
import model.Model_Menu;

public class ListMenu extends JScrollPane {

    private final JList<Model_Menu> list;
    private final DefaultListModel<Model_Menu> model;
    private int selectedIndex = -1;
    private int overIndex = -1;
    private EventMenuSelected event;

    public void addEventMenuSelected(EventMenuSelected event) {
        this.event = event;
    }

    public ListMenu() {
        model = new DefaultListModel<>();
        list = new JList<>(model);
        setViewportView(list);

        // === CẤU HÌNH SCROLLPANE HIỆN ĐẠI ===
        setBorder(null);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // === THANH CUỘN ĐẸP MẮT ===
        JScrollBar verticalScrollBar = getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.setBlockIncrement(50);
        verticalScrollBar.setUI(new ModernScrollBarUI());

        // === JLIST TRONG SUỐT ===
        list.setOpaque(false);
        list.setCellRenderer(createCellRenderer());

        // === SCROLLPANE TRONG SUỐT ===
        setOpaque(false);
        getViewport().setOpaque(false);

        setupMouseEvents();
    }

    // === VẼ NỀN GRADIENT (giống Menu.java) ===
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gradient = new GradientPaint(0, 0, Color.decode("#1c92d2"), 0, getHeight(), Color.decode("#f2fcfe"));
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight()); // Nền phẳng, không bo góc

        g2.dispose();
        super.paintComponent(g);
    }

    // === MOUSE EVENTS ===
    private void setupMouseEvents() {
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int index = list.locationToIndex(e.getPoint());
                    if (index != -1 && model.getElementAt(index).getType() == Model_Menu.MenuType.MENU) {
                        selectedIndex = index;
                        if (event != null) event.selected(index);
                        list.repaint();
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                overIndex = -1;
                list.repaint();
            }
        });

        list.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = list.locationToIndex(e.getPoint());
                if (index != overIndex) {
                    overIndex = (index != -1 && model.getElementAt(index).getType() == Model_Menu.MenuType.MENU) ? index : -1;
                    list.repaint();
                }
            }
        });
    }

    // === RENDERER TYPE-SAFE ===
    private ListCellRenderer<? super Model_Menu> createCellRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> jlist, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Model_Menu data = (Model_Menu) value;
                MenuItem item = new MenuItem(data);
                item.setSelected(selectedIndex == index);
                item.setOver(overIndex == index);
                item.setOpaque(false);
                return item;
            }
        };
    }

    public void addItem(Model_Menu data) {
        model.addElement(data);
    }

    // === CUSTOM SCROLLBAR UI – SIÊU ĐẸP, SIÊU GỌN ===
    private static class ModernScrollBarUI extends BasicScrollBarUI {
        private final int THUMB_SIZE = 8;
        private final Color THUMB_COLOR = new Color(150, 150, 150, 120);
        private final Color TRACK_COLOR = new Color(0, 0, 0, 0);

        @Override
        protected void paintTrack(Graphics g, javax.swing.JComponent c, java.awt.Rectangle trackBounds) {
            g.setColor(TRACK_COLOR);
            g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }

        @Override
        protected void paintThumb(Graphics g, javax.swing.JComponent c, java.awt.Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = thumbBounds.x + (thumbBounds.width - THUMB_SIZE) / 2;
            int y = thumbBounds.y;
            int width = THUMB_SIZE;
            int height = thumbBounds.height;

            g2.setColor(THUMB_COLOR);
            g2.fillRoundRect(x, y, width, height, THUMB_SIZE, THUMB_SIZE);

            g2.dispose();
        }

        @Override
        protected javax.swing.JButton createDecreaseButton(int orientation) {
            return createEmptyButton();
        }

        @Override
        protected javax.swing.JButton createIncreaseButton(int orientation) {
            return createEmptyButton();
        }

        private javax.swing.JButton createEmptyButton() {
            javax.swing.JButton btn = new javax.swing.JButton();
            btn.setPreferredSize(new java.awt.Dimension(0, 0));
            btn.setMinimumSize(new java.awt.Dimension(0, 0));
            btn.setMaximumSize(new java.awt.Dimension(0, 0));
            return btn;
        }
    }
}