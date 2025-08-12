package poly.cafe.dao.impl;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Admin
 */
import java.util.Date;
import java.util.List;
import poly.cafe.dao.RevenueDAO;
import poly.cafe.entity.Revenue;
import poly.cafe.entity.Revenue.BySummary;
import poly.cafe.util.XQuery;

public class RevenueDAOImpl implements RevenueDAO {

    @Override
    public List<Revenue.ByCategory> getByCategory(Date begin, Date end) {
        String revenueByCategorySql
                = "SELECT category.Name AS Category, "
                + "   sum(detail.UnitPrice*detail.Quantity*(1-detail.Discount)) AS Revenue,"
                + "   sum(detail.Quantity) AS Quantity,"
                + "   min(detail.UnitPrice) AS MinPrice,"
                + "   max(detail.UnitPrice) AS MaxPrice,"
                + "   avg(detail.UnitPrice) AS AvgPrice "
                + "FROM BillDetails_New detail "
                + "   JOIN Foods_New food ON food.Id=detail.FoodId"
                + "   JOIN Categories_New category ON category.Id=food.CategoryId"
                + "   JOIN Bills_New bill ON bill.Id=detail.BillId "
                + "WHERE bill.Status=1 "
                + "   AND bill.Checkout IS NOT NULL "
                + "   AND bill.Checkout BETWEEN ? AND ? "
                + "GROUP BY category.Name "
                + "ORDER BY Revenue DESC";
        return XQuery.getBeanList(Revenue.ByCategory.class, revenueByCategorySql, begin, end);
    }

    @Override
    public List<Revenue.ByUser> getByUser(Date begin, Date end) {
        String revenueByUserSql
                = "SELECT bill.Username AS [User], "
                + "   sum(detail.UnitPrice*detail.Quantity*(1-detail.Discount)) AS Revenue,"
                + "   count(DISTINCT detail.BillId) AS Quantity,"
                + "   min(bill.Checkin) AS FirstTime,"
                + "   max(bill.Checkin) AS LastTime "
                + "FROM BillDetails_New detail "
                + "   JOIN Bills_New bill ON bill.Id=detail.BillId "
                + "WHERE bill.Status=1 "
                + "   AND bill.Checkout IS NOT NULL "
                + "   AND bill.Checkout BETWEEN ? AND ? "
                + "GROUP BY bill.Username "
                + "ORDER BY Revenue DESC";
        return XQuery.getBeanList(Revenue.ByUser.class, revenueByUserSql, begin, end);
    }
        @Override
    public  List<BySummary> getBySummary(Date begin, Date end) {
        String revenueBySummarySql = """
        SELECT
            SUM(detail.UnitPrice * detail.Quantity * (1 - detail.Discount)) AS Revenue,
            SUM(detail.Quantity) AS Quantity,
            COUNT(DISTINCT detail.BillId) AS BillCount,
            MIN(detail.UnitPrice) AS MinPrice,
            MAX(detail.UnitPrice) AS MaxPrice,
            AVG(detail.UnitPrice) AS AvgPrice
        FROM BillDetails detail
        JOIN Bills bill ON bill.Id = detail.BillId
        WHERE bill.Status = 1
          AND bill.Checkout IS NOT NULL
          AND bill.Checkout BETWEEN ? AND ?
        """;
        return XQuery.getBeanList(BySummary.class, revenueBySummarySql, begin, end);
    }

}