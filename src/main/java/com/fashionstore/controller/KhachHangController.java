package com.fashionstore.controller;

import java.util.List;
import com.fashionstore.dao.KhachHangDAO;
import com.fashionstore.model.KhachHang;

public class KhachHangController {
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();

    public List<KhachHang> getAllCustomers() {
        return khachHangDAO.getAllCustomers();
    }

    public boolean saveCustomer(KhachHang kh) {
        return khachHangDAO.saveCustomer(kh);
    }

    public boolean updateCustomer(KhachHang kh) {
        return khachHangDAO.updateCustomer(kh);
    }

    public KhachHang getCustomerBySdt(String sdt) {
        return khachHangDAO.getCustomerBySdt(sdt);
    }

    public KhachHang getCustomerByMa(String maKH) {
        return khachHangDAO.getCustomerByMa(maKH);
    }
}
