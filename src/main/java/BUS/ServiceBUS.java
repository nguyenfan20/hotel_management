package BUS;

import DAO.ServiceDAO;
import DTO.ServiceDTO;
import java.util.ArrayList;
import java.util.List;

public class ServiceBUS {
    private final ServiceDAO dao;
    private final List<ServiceDTO> services;

    public ServiceBUS() {
        dao = new ServiceDAO();
        services = new ArrayList<>(dao.getAll());
    }

    
    public List<ServiceDTO> getAll() {
        return new ArrayList<>(services);
    }

    
    public ServiceDTO getById(int id) {
        return dao.getById(id);
    }

    
    public boolean add(ServiceDTO s) {
        int newId = dao.insert(s);
        if (newId > 0) {
            s.setServiceId(newId);
            services.add(s);
            return true;
        }
        return false;
    }

    
    public boolean update(ServiceDTO s) {
        boolean ok = dao.update(s);
        if (ok) {
            for (int i = 0; i < services.size(); i++) {
                if (services.get(i).getServiceId() == s.getServiceId()) {
                    services.set(i, s);
                    break;
                }
            }
        }
        return ok;
    }

    
    public boolean delete(int id) {
        boolean ok = dao.delete(id);
        if (ok) {
            services.removeIf(s -> s.getServiceId() == id);
        }
        return ok;
    }

    
    public List<ServiceDTO> searchByName(String keyword) {
        return dao.searchByName(keyword);
    }

    
    public boolean setActive(int id, boolean active) {
        boolean ok = dao.setActive(id, active);
        if (ok) {
            for (ServiceDTO s : services) {
                if (s.getServiceId() == id) {
                    s.setActive(active);
                    break;
                }
            }
        }
        return ok;
    }
}
