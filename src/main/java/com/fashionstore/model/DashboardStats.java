package com.fashionstore.model;

public class DashboardStats {
    private final long currentMonthRevenue;
    private final long lastMonthRevenue;
    private final int ordersToday;
    private final int sellingProducts;
    private final int activeEmployees;

    public DashboardStats(long currentMonthRevenue, long lastMonthRevenue,
            int ordersToday, int sellingProducts, int activeEmployees) {
        this.currentMonthRevenue = currentMonthRevenue;
        this.lastMonthRevenue = lastMonthRevenue;
        this.ordersToday = ordersToday;
        this.sellingProducts = sellingProducts;
        this.activeEmployees = activeEmployees;
    }

    public long getCurrentMonthRevenue() {
        return currentMonthRevenue;
    }

    public long getLastMonthRevenue() {
        return lastMonthRevenue;
    }

    public int getOrdersToday() {
        return ordersToday;
    }

    public int getSellingProducts() {
        return sellingProducts;
    }

    public int getActiveEmployees() {
        return activeEmployees;
    }

    public double getMoMChangePercent() {
        if (lastMonthRevenue == 0) {
            return currentMonthRevenue == 0 ? 0 : 100;
        }
        return (currentMonthRevenue - lastMonthRevenue) * 100.0 / lastMonthRevenue;
    }
}
