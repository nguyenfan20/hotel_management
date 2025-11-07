package BUS;

import DAO.InvoiceDAO;
import DTO.InvoiceDTO;
import java.util.List;

public class InvoiceBUS {
    private InvoiceDAO invoiceDAO;

    public InvoiceBUS() {
        this.invoiceDAO = new InvoiceDAO();
    }

    public List<InvoiceDTO> getAllInvoices() {
        return invoiceDAO.getAllInvoices();
    }

    public InvoiceDTO getInvoiceById(int invoiceId) {
        if (invoiceId <= 0) {
            System.err.println("ID hóa đơn không hợp lệ");
            return null;
        }
        return invoiceDAO.getInvoiceById(invoiceId);
    }

    public List<InvoiceDTO> getUnpaidInvoices() {
        return invoiceDAO.getUnpaidInvoices();
    }

    public boolean addInvoice(InvoiceDTO invoice) {
        if (!validateInvoice(invoice)) {
            return false;
        }
        return invoiceDAO.addInvoice(invoice);
    }

    public boolean updateInvoice(InvoiceDTO invoice) {
        if (invoice.getInvoiceId() <= 0) {
            System.err.println("ID hóa đơn không hợp lệ");
            return false;
        }
        if (!validateInvoice(invoice)) {
            return false;
        }
        return invoiceDAO.updateInvoice(invoice);
    }

    public boolean deleteInvoice(int invoiceId) {
        if (invoiceId <= 0) {
            System.err.println("ID hóa đơn không hợp lệ");
            return false;
        }
        return invoiceDAO.deleteInvoice(invoiceId);
    }

    public List<InvoiceDTO> searchInvoices(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllInvoices();
        }
        return invoiceDAO.searchInvoices(keyword.trim());
    }

    public List<InvoiceDTO> filterInvoicesByStatus(String status) {
        if (status == null || status.trim().isEmpty() || status.equals("Tất cả")) {
            return getAllInvoices();
        }
        return invoiceDAO.filterInvoicesByStatus(status);
    }

    public List<InvoiceDTO> getInvoicesByBooking(int bookingId) {
        if (bookingId <= 0) {
            return getAllInvoices();
        }
        return invoiceDAO.getInvoicesByBooking(bookingId);
    }

    private boolean validateInvoice(InvoiceDTO invoice) {
        if (invoice == null) {
            System.err.println("Dữ liệu hóa đơn không được null");
            return false;
        }
        if (invoice.getBookingId() <= 0) {
            System.err.println("ID đặt phòng không hợp lệ");
            return false;
        }
        if (invoice.getInvoiceNo() == null || invoice.getInvoiceNo().trim().isEmpty()) {
            System.err.println("Số hóa đơn không được để trống");
            return false;
        }
        if (invoice.getGrandTotal() < 0) {
            System.err.println("Tổng tiền không được âm");
            return false;
        }
        if (invoice.getStatus() == null || invoice.getStatus().trim().isEmpty()) {
            System.err.println("Trạng thái không được để trống");
            return false;
        }
        return true;
    }
}
