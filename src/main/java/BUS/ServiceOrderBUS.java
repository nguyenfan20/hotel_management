package BUS;

import DAO.ServiceOrderDAO;
import DTO.ServiceOrderDTO;
import java.util.ArrayList;
import java.util.List;

public class ServiceOrderBUS {

    private final ServiceOrderDAO dao;
    private final List<ServiceOrderDTO> serviceOrders;

    public ServiceOrderBUS() {
        dao = new ServiceOrderDAO();
        serviceOrders = new ArrayList<>();
        try {
            serviceOrders.addAll(dao.getAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public List<ServiceOrderDTO> getAll() {
        return new ArrayList<>(serviceOrders);
    }

    
    public ServiceOrderDTO getById(int id) {
        return dao.getById(id);
    }

    
    public boolean add(ServiceOrderDTO order) {
        int newId = dao.insert(order);
        if (newId > 0) {
            order.setServiceOrderId(newId); 
            serviceOrders.add(order);
            return true;
        }
        return false;
    }

    
    public boolean update(ServiceOrderDTO order) {
        boolean ok = dao.update(order);
        if (ok) {
            for (int i = 0; i < serviceOrders.size(); i++) {
                if (serviceOrders.get(i).getServiceOrderId() == order.getServiceOrderId()) {
                    serviceOrders.set(i, order);
                    break;
                }
            }
        }
        return ok;
    }

    
    public boolean delete(int id) {
        boolean ok = dao.delete(id);
        if (ok) {
            serviceOrders.removeIf(o -> o.getServiceOrderId() == id);
        }
        return ok;
    }

    
    public List<ServiceOrderDTO> searchByServiceName(String keyword) {
        return dao.searchByServiceName(keyword);
    }

    public List<ServiceOrderDTO> getServiceOrdersByBooking(int bookingId) {
        return dao.getByBookingId(bookingId);
    }

    public void reload() {
        serviceOrders.clear();
        try {
            serviceOrders.addAll(dao.getAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
