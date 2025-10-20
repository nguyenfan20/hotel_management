package BUS;

import DAO.PaymentDAO;
import DTO.PaymentDTO;
import java.util.List;

public class PaymentBUS {
    private PaymentDAO paymentDAO;

    public PaymentBUS() {
        this.paymentDAO = new PaymentDAO();
    }

    public List<PaymentDTO> getAllPayments() {
        return paymentDAO.getAllPayments();
    }

    public PaymentDTO getPaymentById(int paymentId) {
        if (paymentId <= 0) {
            System.err.println("ID thanh toán không hợp lệ");
            return null;
        }
        return paymentDAO.getPaymentById(paymentId);
    }

    public boolean addPayment(PaymentDTO payment) {
        if (!validatePayment(payment)) {
            return false;
        }
        return paymentDAO.addPayment(payment);
    }

    public boolean updatePayment(PaymentDTO payment) {
        if (payment.getPaymentId() <= 0) {
            System.err.println("ID thanh toán không hợp lệ");
            return false;
        }
        if (!validatePayment(payment)) {
            return false;
        }
        return paymentDAO.updatePayment(payment);
    }

    public boolean deletePayment(int paymentId) {
        if (paymentId <= 0) {
            System.err.println("ID thanh toán không hợp lệ");
            return false;
        }
        return paymentDAO.deletePayment(paymentId);
    }

    public List<PaymentDTO> searchPayments(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllPayments();
        }
        return paymentDAO.searchPayments(keyword.trim());
    }

    public List<PaymentDTO> filterPaymentsByStatus(String status) {
        if (status == null || status.trim().isEmpty() || status.equals("Tất cả")) {
            return getAllPayments();
        }
        return paymentDAO.filterPaymentsByStatus(status);
    }

    public List<PaymentDTO> getPaymentsByBooking(int bookingId) {
        if (bookingId <= 0) {
            return getAllPayments();
        }
        return paymentDAO.getPaymentsByBooking(bookingId);
    }

    public double calculateTotalPaid(int bookingId) {
        List<PaymentDTO> payments = getPaymentsByBooking(bookingId);
        double total = 0;
        for (PaymentDTO payment : payments) {
            if ("Completed".equals(payment.getStatus())) {
                total += payment.getAmount();
            }
        }
        return total;
    }

    private boolean validatePayment(PaymentDTO payment) {
        if (payment == null) {
            System.err.println("Dữ liệu thanh toán không được null");
            return false;
        }
        if (payment.getBookingId() <= 0) {
            System.err.println("ID đặt phòng không hợp lệ");
            return false;
        }
        if (payment.getAmount() <= 0) {
            System.err.println("Số tiền phải lớn hơn 0");
            return false;
        }
        if (payment.getMethod() == null || payment.getMethod().trim().isEmpty()) {
            System.err.println("Phương thức thanh toán không được để trống");
            return false;
        }
        if (payment.getStatus() == null || payment.getStatus().trim().isEmpty()) {
            System.err.println("Trạng thái thanh toán không được để trống");
            return false;
        }
        return true;
    }
}
