package BUS;

import DAO.DiscountDAO;
import DTO.DiscountDTO;
import java.util.List;

public class DiscountBUS {
    private DiscountDAO discountDAO;

    public DiscountBUS() {
        this.discountDAO = new DiscountDAO();
    }

    public List<DiscountDTO> getAllDiscounts() {
        return discountDAO.getAllDiscounts();
    }

    public DiscountDTO getDiscountById(int discountId) {
        if (discountId <= 0) {
            System.err.println("ID chiết khấu không hợp lệ");
            return null;
        }
        return discountDAO.getDiscountById(discountId);
    }

    public boolean addDiscount(DiscountDTO discount) {
        if (!validateDiscount(discount)) {
            return false;
        }
        return discountDAO.addDiscount(discount);
    }

    public boolean updateDiscount(DiscountDTO discount) {
        if (discount.getDiscountId() <= 0) {
            System.err.println("ID chiết khấu không hợp lệ");
            return false;
        }
        if (!validateDiscount(discount)) {
            return false;
        }
        return discountDAO.updateDiscount(discount);
    }

    public boolean deleteDiscount(int discountId) {
        if (discountId <= 0) {
            System.err.println("ID chiết khấu không hợp lệ");
            return false;
        }
        return discountDAO.deleteDiscount(discountId);
    }

    public List<DiscountDTO> searchDiscounts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllDiscounts();
        }
        return discountDAO.searchDiscounts(keyword.trim());
    }

    public List<DiscountDTO> getActiveDiscounts() {
        return discountDAO.getActiveDiscounts();
    }

    public DiscountDTO getDiscountByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return discountDAO.getDiscountByCode(code.trim());
    }

    private boolean validateDiscount(DiscountDTO discount) {
        if (discount == null) {
            System.err.println("Dữ liệu chiết khấu không được null");
            return false;
        }
        if (discount.getCode() == null || discount.getCode().trim().isEmpty()) {
            System.err.println("Mã chiết khấu không được để trống");
            return false;
        }
        if (discount.getDiscountType() == null || discount.getDiscountType().trim().isEmpty()) {
            System.err.println("Loại chiết khấu không được để trống");
            return false;
        }
        if (discount.getDiscountValue() < 0) {
            System.err.println("Giá trị chiết khấu không được âm");
            return false;
        }
        if (discount.getMinSpend() < 0) {
            System.err.println("Chi tiêu tối thiểu không được âm");
            return false;
        }
        return true;
    }
}
