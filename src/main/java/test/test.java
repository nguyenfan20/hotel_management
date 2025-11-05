/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import GUI.booking.BookingManagement;

/**
 *
 * @author daoho
 */

import javax.swing.JFrame;

public class test {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Booking");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // hiển thị ở giữa màn hình

        // Tạo panel BookingGUI và thêm vào JFrame
        BookingManagement bookingManagement = new BookingManagement();
        frame.add(bookingManagement);

        // Tự động điều chỉnh kích thước theo panel
        frame.pack();

        // Hiển thị JFrame
        frame.setVisible(true);
    }
}


