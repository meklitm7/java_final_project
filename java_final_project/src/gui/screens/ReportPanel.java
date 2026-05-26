// src/gui/screens/ReportPanel.java
package gui.screens;

import db.ReportGenerator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.io.File;
import java.sql.Connection;

public class ReportPanel extends VBox {
    private Connection conn;

    public ReportPanel(Connection conn) {
        this.conn = conn;
        setPadding(new Insets(20));
        setSpacing(15);
        setAlignment(Pos.CENTER);
        setupUI();
    }

    private void setupUI() {
        Label title = new Label("Generate Reports");
        title.setFont(Font.font("Arial", 20));
        title.setTextFill(Color.web("#2c3e50"));

        Button monthlyBtn = new Button("Monthly Donations Report");
        Button approvedBtn = new Button("Approved Donations Report");
        Button companyBtn = new Button("Company Activity Report");
        Button volunteerBtn = new Button("Volunteer Report");

        styleButton(monthlyBtn, "#3498db");
        styleButton(approvedBtn, "#2ecc71");
        styleButton(companyBtn, "#f39c12");
        styleButton(volunteerBtn, "#9b59b6");

        Label msg = new Label();
        msg.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        // Create reports directory if it doesn't exist
        File reportsDir = new File("reports");
        if (!reportsDir.exists()) {
            reportsDir.mkdir();
        }

        monthlyBtn.setOnAction(e -> {
            try {
                ReportGenerator reportGen = new ReportGenerator(conn);
                reportGen.generateMonthlyDonationsReport("reports/monthly_donations.csv");
                msg.setText("✅ Monthly donations report generated at: " + new File("reports/monthly_donations.csv").getAbsolutePath());
            } catch (Exception ex) {
                msg.setText("❌ Error generating report: " + ex.getMessage());
            }
        });

        approvedBtn.setOnAction(e -> {
            try {
                ReportGenerator reportGen = new ReportGenerator(conn);
                reportGen.generateApprovedDonationsReport("reports/approved_donations.csv");
                msg.setText("✅ Approved donations report generated at: " + new File("reports/approved_donations.csv").getAbsolutePath());
            } catch (Exception ex) {
                msg.setText("❌ Error generating report: " + ex.getMessage());
            }
        });

        companyBtn.setOnAction(e -> {
            try {
                ReportGenerator reportGen = new ReportGenerator(conn);
                reportGen.generateCompanyActivityReport("reports/company_activity.csv");
                msg.setText("✅ Company activity report generated at: " + new File("reports/company_activity.csv").getAbsolutePath());
            } catch (Exception ex) {
                msg.setText("❌ Error generating report: " + ex.getMessage());
            }
        });

        volunteerBtn.setOnAction(e -> {
            try {
                // Create a simple volunteer report
                ReportGenerator reportGen = new ReportGenerator(conn);
                reportGen.generateVolunteerReport("reports/volunteer_report.csv");
                msg.setText("✅ Volunteer report generated at: " + new File("reports/volunteer_report.csv").getAbsolutePath());
            } catch (Exception ex) {
                msg.setText("❌ Error generating report: " + ex.getMessage());
            }
        });

        getChildren().addAll(
            title,
            monthlyBtn,
            approvedBtn,
            companyBtn,
            volunteerBtn,
            msg
        );
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-pref-width: 250px; -fx-pref-height: 40px; -fx-font-size: 14px;");
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + "; -fx-background-radius: 5;"));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace("; -fx-background-radius: 5;", "")));
    }
}