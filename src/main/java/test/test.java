/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import GUI.booking.BookingGUI;
import javax.swing.*;

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
        BookingGUI bookingGUI = new BookingGUI();
        frame.add(bookingGUI);

        // Tự động điều chỉnh kích thước theo panel
        frame.pack();

        // Hiển thị JFrame
        frame.setVisible(true);
    }
}


