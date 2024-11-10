/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.financeapp.personal_finance_tool;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.awt.geom.Rectangle2D;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.LinkedHashMap;
import org.jfree.chart.plot.PlotOrientation;


public class ReportGenerator {
    private Map<String, Double> getCategoryExpenseData(int year, String category,int userId) {
      // Initialize map with all months of the year and 0 expenses
  Map<String, Double> categoryData = new LinkedHashMap<>();
    String[] months = new java.text.DateFormatSymbols().getMonths();
    
    // Initialize the map with all months and set the expenses to 0 initially
    for (int i = 0; i < 12; i++) {
        categoryData.put(months[i], 0.0);
    }

    // Query to fetch category data for the specific year, user, and category, grouping by month
    String query = "SELECT MONTH(transaction_date) AS month_number, SUM(amount) AS total_expense "
                 + "FROM transactions "
                 + "WHERE YEAR(transaction_date) = ? AND category = ? AND user_id = ? "
                 + "GROUP BY month_number ORDER BY month_number";

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/personal_finance", "root", "mysql");
         PreparedStatement pstmt = conn.prepareStatement(query)) {

        pstmt.setInt(1, year);
        pstmt.setString(2, category);
        pstmt.setInt(3,userId);
        
        
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            int monthNumber = rs.getInt("month_number");
            double totalExpense = rs.getDouble("total_expense");
            
            String monthName = months[monthNumber - 1];
            categoryData.put(monthName, totalExpense);
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error retrieving data: " + e.getMessage());
    }

    return categoryData;
}

    // Method to generate PDF with chart and transaction data
   public void generatePdfWithChart(int year, String category, int userId) throws DocumentException, IOException {
    Map<String, Double> categoryData = getCategoryExpenseData(year, category,userId);
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (Map.Entry<String, Double> entry : categoryData.entrySet()) {
        dataset.addValue(entry.getValue(), "Expenses", entry.getKey());
    }

    JFreeChart chart = createChart(dataset);

    BufferedImage chartImage = chart.createBufferedImage(800, 600);

    String filePath = "YearlyExpenseReport_" + year + "_" + category + ".pdf";

    Document document = new Document();
    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
    document.open();

    // Title Section: Professional and Clear Title
    Font titleFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 18, Font.BOLD);
    Paragraph title = new Paragraph("Detailed Yearly Expense Report", titleFont);
    title.setAlignment(Element.ALIGN_CENTER);
    document.add(title);
    document.add(Chunk.NEWLINE);

    // Subtitle for Clarity
    Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
    Paragraph subtitle = new Paragraph("Category: " + category + " | Year: " + year, subtitleFont);
    subtitle.setAlignment(Element.ALIGN_CENTER);
    document.add(subtitle);
    document.add(Chunk.NEWLINE);

    // Add Date & User Info
    Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC);
    Paragraph info = new Paragraph("Report Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()), infoFont);
    info.setAlignment(Element.ALIGN_RIGHT);
    document.add(info);
    document.add(Chunk.NEWLINE);

    Paragraph userDetails = new Paragraph("User ID: " + userId, infoFont);
    userDetails.setAlignment(Element.ALIGN_RIGHT);
    document.add(userDetails);
    document.add(Chunk.NEWLINE);

    // Add the Chart with Caption
    Image chartPdfImage = Image.getInstance(writer.getDirectContent(), chartImage, 1);
    chartPdfImage.setAlignment(Image.ALIGN_CENTER);
    chartPdfImage.scaleToFit(500, 400);
    document.add(chartPdfImage);

    // Add Caption for the Chart
    Font captionFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC);
    Paragraph caption = new Paragraph("Figure 1: Monthly Expense Breakdown for the Category: " + category, captionFont);
    caption.setAlignment(Element.ALIGN_CENTER);
    document.add(caption);
    document.add(Chunk.NEWLINE);

    // Add Transaction Details Section
    Font sectionFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD);
    Paragraph transactionDetailsTitle = new Paragraph("Transaction Details", sectionFont);
    transactionDetailsTitle.setSpacingBefore(20);
    document.add(transactionDetailsTitle);

    // Create Table with Data
    PdfPTable table = new PdfPTable(4);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{2f, 3f, 2f, 2f});

    Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    addTableHeader(table, headerFont);

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/personal_finance", "root", "mysql");
         PreparedStatement pstmt = conn.prepareStatement(
             "SELECT transaction_date, description, amount, category "
             + "FROM transactions "
             + "WHERE YEAR(transaction_date) = ? AND category = ? AND user_id = ? "
             + "ORDER BY transaction_date")) {

        pstmt.setInt(1, year);
        pstmt.setString(2, category);
        pstmt.setInt(3, userId);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            addTableRow(table, rs);
        }

        document.add(table);
    } catch (SQLException e) {
        e.printStackTrace();
    }

    // Add Summary Section
    Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
    Paragraph summaryTitle = new Paragraph("Summary and Insights", summaryFont);
    summaryTitle.setSpacingBefore(20);
    document.add(summaryTitle);

    // A placeholder summary (you can analyze the data to generate dynamic insights)
    Paragraph summaryText = new Paragraph("In this report, the total expenses for the category " + category + " in " + year + " are summarized. "
            + "The report highlights the spending trends, such as peak expense months and the preferred payment methods. "
            + "This information can help you make informed decisions about future budgeting and expense tracking.",
            FontFactory.getFont(FontFactory.HELVETICA, 11));
    document.add(summaryText);
    document.add(Chunk.NEWLINE);

    // Close Document and notify
    document.close();
    JOptionPane.showMessageDialog(null, "PDF Report Generated Successfully!");
    openPdf(filePath);
}

// Helper methods for table
private void addTableHeader(PdfPTable table, Font font) {
    String[] headers = {"Date", "Description", "Amount", "Category"};
    for (String header : headers) {
        PdfPCell headerCell = new PdfPCell(new Phrase(header, font));
        headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(headerCell);
    }
}

private void addTableRow(PdfPTable table, ResultSet rs) throws SQLException {
    table.addCell(rs.getString("transaction_date"));
    table.addCell(rs.getString("description"));
    table.addCell("₹" + rs.getString("amount"));
    table.addCell(rs.getString("category"));
}

    private void openPdf(String filePath) {
    try {
        // Open the generated PDF using the default system viewer
        File pdfFile = new File(filePath);
        if (pdfFile.exists()) {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(pdfFile);
            } else {
                JOptionPane.showMessageDialog(null, "Desktop is not supported on your system.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "PDF file does not exist.");
        }
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error opening PDF: " + e.getMessage());
    }
}
    private JFreeChart createChart(DefaultCategoryDataset dataset) {
    JFreeChart chart = ChartFactory.createBarChart(
        "Category Expenses", "Year", "Amount", dataset, PlotOrientation.VERTICAL,
        true, true, false);

    CategoryPlot plot = chart.getCategoryPlot();
    plot.setDomainGridlinesVisible(true);
    plot.setRangeGridlinesVisible(true);
    return chart;
}
    
}
