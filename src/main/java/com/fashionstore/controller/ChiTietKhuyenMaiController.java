package com.fashionstore.controller;

import java.util.List;
import com.fashionstore.dao.ChiTietKhuyenMaiDAO;
import com.fashionstore.model.ChiTietKhuyenMai;

public class ChiTietKhuyenMaiController {
    private final ChiTietKhuyenMaiDAO dao = new ChiTietKhuyenMaiDAO();

    public List<ChiTietKhuyenMai> getByMaKM(String maKM) {
        return dao.getByMaKM(maKM);
    }

    public boolean save(ChiTietKhuyenMai ct) {
        return dao.save(ct);
    }

    public boolean delete(String maKM, String maBienThe) {
        return dao.delete(maKM, maBienThe);
    }

    public boolean deleteByMaKM(String maKM) {
        return dao.deleteByMaKM(maKM);
    }
}
