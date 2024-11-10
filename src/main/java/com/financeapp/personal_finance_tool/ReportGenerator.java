/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.financeapp.personal_finance_tool;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;

import java.util.Map;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;

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
import java.awt.BasicStroke;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.DefaultPieDataset;


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
public void generatePdfWithBarChartAndInsights(int year, String category, int userId) throws DocumentException, IOException {
    // Retrieve data for category expenses, monthly expenses, and the transactions
    Map<String, Double> categoryData = getCategoryExpenseData(year, category, userId);
    Map<String, Double> monthlyExpenses = getMonthlyExpenseData(year, category, userId);
    
    // Bar chart dataset for category expenses
    DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
    for (Map.Entry<String, Double> entry : categoryData.entrySet()) {
        barDataset.addValue(entry.getValue(), "Expenses", entry.getKey());
    }

    // Create Bar chart
    JFreeChart barChart = createChart(barDataset);
    
    // Pie chart for the monthly breakdown of expenses
    JFreeChart pieChart = createPieChart(monthlyExpenses);

    // Convert charts to images
    BufferedImage barChartImage = barChart.createBufferedImage(800, 600);
    BufferedImage pieChartImage = pieChart.createBufferedImage(800, 600);

    // Define the file path for the PDF
    String filePath = "SmartFinance_YearlyExpenseReport_" + year + "_" + category + ".pdf";
    
    // Create PDF document and writer
    Document document = new Document();
    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));

    // Add footer with copyright statement
    writer.setPageEvent(new PdfPageEventHelper() {
        public void onEndPage(PdfWriter writer, Document document) {
            PdfPTable footer = new PdfPTable(1);
            footer.setTotalWidth(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin());
            try {
                footer.setWidths(new float[]{100});
            } catch (DocumentException ex) {
                Logger.getLogger(ReportGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
            PdfPCell cell = new PdfPCell(new Phrase("© " + new java.util.Date().getYear() + " SmartFinance. All Rights Reserved.", FontFactory.getFont(FontFactory.HELVETICA, 8, Font.ITALIC)));
            //cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footer.addCell(cell);
            footer.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin() - 10, writer.getDirectContent());
        }
    });

    document.open();

    // Add title and report details
    Font titleFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 16, Font.BOLD);
    Paragraph title = new Paragraph("SmartFinance - Yearly Expense Report", titleFont);
    title.setAlignment(Element.ALIGN_CENTER);
    document.add(title);
    document.add(Chunk.NEWLINE);

    Font detailsFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
    Paragraph details = new Paragraph("Category: " + category + " | Year: " + year, detailsFont);
    details.setAlignment(Element.ALIGN_CENTER);
    document.add(details);
    document.add(Chunk.NEWLINE);

    // Add Bar chart image
    Image barChartPdfImage = Image.getInstance(writer.getDirectContent(), barChartImage, 1);
    barChartPdfImage.setAlignment(Image.ALIGN_CENTER);
    barChartPdfImage.scaleToFit(500, 400);
    document.add(barChartPdfImage);
    
    Font captionFont = FontFactory.getFont(FontFactory.HELVETICA, 10, java.awt.Font.ITALIC);
    Paragraph chartCaption = new Paragraph("Figure 1: Monthly Expense Breakdown for " + category, captionFont);
    chartCaption.setAlignment(Element.ALIGN_CENTER);
    document.add(chartCaption);
    document.add(Chunk.NEWLINE);
    
   Font dataFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    String highestMonth = categoryData.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
    String lowestMonth = categoryData.entrySet().stream().min(Map.Entry.comparingByValue()).get().getKey();
    Paragraph highestMonthText = new Paragraph("Month with Highest Expense: " + highestMonth + " (₹" + categoryData.get(highestMonth) + ")", dataFont);
    Paragraph lowestMonthText = new Paragraph("Month with Lowest Expense: " + lowestMonth + " (₹" + categoryData.get(lowestMonth) + ")", dataFont);
    document.add(highestMonthText);
    document.add(lowestMonthText);
    document.add(Chunk.NEWLINE);

   

    // Add transaction summary and table
    Font tableTitleFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD);
    Paragraph tableTitle = new Paragraph("Transaction Summary", tableTitleFont);
    tableTitle.setSpacingBefore(10);
    document.add(tableTitle);
    
    PdfPTable table = new PdfPTable(4);  // Columns: Date, Description, Amount, Category
    table.setWidthPercentage(100);
    table.setWidths(new float[]{2f, 3f, 2f, 2f});
    Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    addTableHeader(table, headerFont);
    
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/personal_finance", "root", "mysql");
         PreparedStatement pstmt = conn.prepareStatement(
             "SELECT transaction_date, description, amount, category "
             + "FROM transactions "
             + "WHERE YEAR(transaction_date) = ? AND category = ? AND user_id = ? "
             + "ORDER BY transaction_date ASC")) {
        
        pstmt.setInt(1, year);      // Set the value for year
        pstmt.setString(2, category); // Set the value for category
        pstmt.setInt(3, userId);    // Set the value for user ID
        
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            addTableRow(table, rs);  // Add rows to the table
        }
        document.add(table);

    } catch (SQLException e) {
        e.printStackTrace();
    }

    // Add Pie chart image
    Image pieChartPdfImage = Image.getInstance(writer.getDirectContent(), pieChartImage, 1);
    pieChartPdfImage.setAlignment(Image.ALIGN_CENTER);
    pieChartPdfImage.scaleToFit(500, 400);
    document.add(pieChartPdfImage);

    // Add pie chart label
    Font chartLabelFont = FontFactory.getFont(FontFactory.HELVETICA, 10, java.awt.Font.ITALIC);
    Paragraph pieChartLabel = new Paragraph("Figure 2: Category Expense Breakdown by Month", chartLabelFont);
    pieChartLabel.setAlignment(Element.ALIGN_CENTER);
    document.add(pieChartLabel);
    
      // Summary and Insights
    Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
    Paragraph summaryTitle = new Paragraph("Summary and Insights", sectionFont);
    summaryTitle.setSpacingBefore(20);
    document.add(summaryTitle);

    // Example Insights
    // Add summary section
    

    Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
    String summaryText = "In conclusion, the data shows that the overall spending pattern for '" + category + "' has been consistent over the year. "
                         + "You may want to consider reviewing your monthly expenses and look for opportunities to save in high-expense months.";
    Paragraph summary = new Paragraph(summaryText, summaryFont);
    document.add(summary);

    

    // Add the thank you message
    Paragraph thankYou = new Paragraph("Thank you for using SmartFinance! We hope our insights help you in managing your finances.", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.ITALIC));
    thankYou.setAlignment(Element.ALIGN_CENTER);
    document.add(Chunk.NEWLINE);
    document.add(thankYou);

    // Close the document and notify the user
    document.close();
    JOptionPane.showMessageDialog(null, "PDF Report Generated Successfully!");
    openPdf(filePath);
}


private void addTableHeader(PdfPTable table, Font font) {
    String[] headers = {"Date", "Description", "Amount", "Category"};
    for (String header : headers) {
        PdfPCell headerCell = new PdfPCell(new Phrase(header, font));
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(headerCell);
    }
}

private void addTableRow(PdfPTable table, ResultSet rs) throws SQLException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    table.addCell(sdf.format(rs.getDate("transaction_date")));
    table.addCell(rs.getString("description"));
    table.addCell("₹" + rs.getDouble("amount"));
    table.addCell(rs.getString("category"));
}




private JFreeChart createChart(DefaultCategoryDataset dataset) {
    JFreeChart chart = ChartFactory.createBarChart(
        "Category Expenses", "Month", "Amount", dataset, PlotOrientation.VERTICAL,
        true, true, false);

    CategoryPlot plot = chart.getCategoryPlot();
    plot.setDomainGridlinesVisible(true);
    plot.setRangeGridlinesVisible(true);
    
   
    return chart;
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
    private Map<String, Double> getMonthlyExpenseData(int year, String category, int userId) {
    Map<String, Double> monthlyExpenses = new HashMap<>();
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/personal_finance", "root", "mysql");
         PreparedStatement pstmt = conn.prepareStatement(
             "SELECT MONTH(transaction_date) AS month, SUM(amount) AS total_expenses "
             + "FROM transactions "
             + "WHERE YEAR(transaction_date) = ? AND category = ? AND user_id = ? "
             + "GROUP BY MONTH(transaction_date) "
             + "ORDER BY month ASC")) {
        
        pstmt.setInt(1, year);      // Set the value for year
        pstmt.setString(2, category); // Set the value for category
        pstmt.setInt(3, userId);    // Set the value for user ID
        
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            String month = new SimpleDateFormat("MMMM").format(new GregorianCalendar(year, rs.getInt("month") - 1, 1).getTime());
            double totalExpenses = rs.getDouble("total_expenses");
            monthlyExpenses.put(month, totalExpenses);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return monthlyExpenses;
    }


private JFreeChart createPieChart(Map<String, Double> monthlyExpenses) {
    DefaultPieDataset dataset = new DefaultPieDataset();
    
    // Add the data to the dataset
    for (Map.Entry<String, Double> entry : monthlyExpenses.entrySet()) {
        dataset.setValue(entry.getKey(), entry.getValue());
    }

    // Create the pie chart
    JFreeChart pieChart = ChartFactory.createPieChart(
            "Expense Breakdown by Month",  // Chart title
            dataset,                      // Data
            true,                         // Include legend
            true,                         // Tooltips
            false);                       // URLs

    // Get the plot to customize its appearance
    PiePlot plot = (PiePlot) pieChart.getPlot();

    // Customize the appearance of the pie chart slices
    plot.setSectionPaint("January", Color.RED);     // Set custom color for each section
    plot.setSectionPaint("February", Color.BLUE);
    plot.setSectionPaint("March", Color.GREEN);
    plot.setSectionPaint("April", Color.YELLOW);
    plot.setSectionPaint("May", Color.ORANGE);
    plot.setSectionPaint("June", Color.CYAN);
    plot.setSectionPaint("July", Color.MAGENTA);
    plot.setSectionPaint("August", Color.PINK);
    plot.setSectionPaint("September", Color.LIGHT_GRAY);
    plot.setSectionPaint("October", Color.DARK_GRAY);
    plot.setSectionPaint("November", Color.YELLOW);
    plot.setSectionPaint("December", Color.ORANGE);

    // Set the label format (percentage and value)
    plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));
    plot.setLabelBackgroundPaint(new Color(255, 255, 255, 200));  // semi-transparent background for labels
    plot.setLabelOutlinePaint(Color.WHITE);  // White outline for labels

    // Optional: You can also modify the outline color of sections (not using strokes directly)
    //plot.setSectionOutlinePaint(Color.BLACK);

    // Optional: Set a custom background color
    plot.setBackgroundPaint(Color.WHITE);

    return pieChart;
}



    
    
}
