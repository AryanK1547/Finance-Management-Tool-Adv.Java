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
        // (Insert the code from the previous message here)
        Map<String, Double> categoryData = getCategoryExpenseData(year, category,userId);
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (Map.Entry<String, Double> entry : categoryData.entrySet()) {
        dataset.addValue(entry.getValue(), "Expenses", entry.getKey());
    }

    JFreeChart chart = createChart(dataset);

    // Step 2: Convert chart to an image
    BufferedImage chartImage = chart.createBufferedImage(800, 600);

    // Step 3: Define PDF file path
    String filePath = "YearlyExpenseReport_" + year + "_" + category + ".pdf";

    // Step 4: Create PDF document and writer
    Document document = new Document();
    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
    document.open();

    // Step 5: Add report title
    Font titleFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 16, Font.BOLD);
    Paragraph title = new Paragraph("Yearly Expense Report for " + category + " (" + year + ")", titleFont);
    title.setAlignment(Element.ALIGN_CENTER);
    document.add(title);
    document.add(Chunk.NEWLINE);

    // Step 6: Add date and user information
    Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC);
   // Correctly construct the Paragraphs
    Paragraph info = new Paragraph("Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
    info.setFont(infoFont);  // Set the font if needed
    info.setAlignment(Element.ALIGN_RIGHT);
    document.add(info);

    Paragraph userDetails = new Paragraph("User ID: " + userId);
    userDetails.setFont(infoFont);  // Set the font if needed
    userDetails.setAlignment(Element.ALIGN_RIGHT);
    document.add(userDetails);
    document.add(Chunk.NEWLINE);  // Adds space after user details

    // Step 7: Add chart image to PDF    
    Image chartPdfImage = Image.getInstance(writer.getDirectContent(), chartImage, 1);
    chartPdfImage.setAlignment(Image.ALIGN_CENTER);
    chartPdfImage.scaleToFit(500, 400);
    document.add(chartPdfImage);
    document.add(Chunk.NEWLINE);

    // Step 8: Transaction details section title
    Font sectionFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD);
    Paragraph transactionDetailsTitle = new Paragraph("Transaction Details for Year " + year, sectionFont);
    transactionDetailsTitle.setSpacingBefore(10);
    document.add(transactionDetailsTitle);

    // Step 9: Create and format the table
    PdfPTable table = new PdfPTable(5); // 5 columns for date, description, amount, method, and category
    table.setWidthPercentage(100);
    table.setWidths(new float[]{2f, 3f, 2f, 2f, 2f});

    Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    addTableHeader(table, headerFont);

   try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/personal_finance", "root", "mysql");
     PreparedStatement pstmt = conn.prepareStatement(
         "SELECT MONTH(transaction_date) AS month_number, SUM(amount) AS total_expense "
         + "FROM transactions "
         + "WHERE YEAR(transaction_date) = ? AND category = ? AND user_id = ? "
         + "GROUP BY month_number ORDER BY month_number")) {

    pstmt.setInt(1, year);           // Set the value for the first parameter (year)
    pstmt.setString(2, category);    // Set the value for the second parameter (category)
    pstmt.setInt(3, userId);         // Set the value for the third parameter (user_id)

    ResultSet rs = pstmt.executeQuery();
   String[] months = new java.text.DateFormatSymbols().getMonths();
while (rs.next()) {
    int monthNumber = rs.getInt("month_number");
    double totalExpense = rs.getDouble("total_expense");

    if (monthNumber >= 1 && monthNumber <= 12) {
        String monthName = months[monthNumber - 1]; // Subtract 1 because months[] is zero-indexed
        categoryData.put(monthName, totalExpense);
    }
}

} catch (SQLException e) {
    JOptionPane.showMessageDialog(null, "Error retrieving data: " + e.getMessage());
}

    // Step 10: Close document and notify
    document.close();
    JOptionPane.showMessageDialog(null, "PDF Report Generated Successfully!");
    openPdf(filePath);
    }

    // Helper method to add table headers to the PDF table
    private void addTableHeader(PdfPTable table, Font font) {
        String[] headers = {"Date", "Description", "Amount", "Payment Method", "Category"};
        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header, font));
            headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(headerCell);
        }
    }

    // Helper method to add rows to the table from the ResultSet
    private void addTableRow(PdfPTable table, ResultSet rs) throws SQLException {
        table.addCell(rs.getString("transaction_date"));
        table.addCell(rs.getString("description"));
        table.addCell("₹" + rs.getString("amount"));
        table.addCell(rs.getString("payment_method"));
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
