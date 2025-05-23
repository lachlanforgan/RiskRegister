import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Main frontend application for Risk Register system
 */
public class RiskRegisterApp extends JFrame {
    
    private JTable riskTable;
    private DefaultTableModel tableModel;
    private JButton addButton, deleteButton, viewButton;
    private RiskService riskService;
    
    public RiskRegisterApp() {
        // Initialize the service
        riskService = new RiskService();
        
        // Set up the JFrame
        setTitle("Risk Register");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create the table model
        String[] columns = {"Risk ID", "Project ID", "Title", "Description", "Likelihood", "Impact", "Owner", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        riskTable = new JTable(tableModel);
        
        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(riskTable);
        
        // Create the button panel
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add Risk");
        deleteButton = new JButton("Delete Risk");
        viewButton = new JButton("View Details");
        
        // Add action listeners to buttons
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddRiskDialog();
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedRisk();
            }
        });
        
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewRiskDetails();
            }
        });
        
        // Add buttons to panel
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton);
        
        // Add components to frame
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Load risk data
        loadRiskData();
    }
    
    /**
     * Loads risk data from the database into the table
     */
    private void loadRiskData() {
        // Clear the table
        tableModel.setRowCount(0);
        
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM risk");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("riskID"),
                    rs.getInt("projectID"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("likelihood"),
                    rs.getString("impact"),
                    rs.getString("owner"),
                    rs.getString("status")
                };
                tableModel.addRow(row);
            }
            
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading risk data: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Shows dialog for adding a new risk
     */
    private void showAddRiskDialog() {
        JDialog dialog = new JDialog(this, "Add New Risk", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Risk ID
        JTextField riskIDField = new JTextField();
        panel.add(new JLabel("Risk ID:"));
        panel.add(riskIDField);
        
        // Project ID
        JTextField projectIDField = new JTextField();
        panel.add(new JLabel("Project ID:"));
        panel.add(projectIDField);
        
        // Title
        JTextField titleField = new JTextField();
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        
        // Description
        JTextField descriptionField = new JTextField();
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        
        // Likelihood
        String[] likelihoodOptions = {"LOW", "MEDIUM", "HIGH"};
        JComboBox<String> likelihoodCombo = new JComboBox<>(likelihoodOptions);
        panel.add(new JLabel("Likelihood:"));
        panel.add(likelihoodCombo);
        
        // Impact
        String[] impactOptions = {"LOW", "MEDIUM", "HIGH", "CRITICAL"};
        JComboBox<String> impactCombo = new JComboBox<>(impactOptions);
        panel.add(new JLabel("Impact:"));
        panel.add(impactCombo);
        
        // Mitigation Plan
        JTextField mitigationField = new JTextField();
        panel.add(new JLabel("Mitigation Plan:"));
        panel.add(mitigationField);
        
        // Owner
        JTextField ownerField = new JTextField();
        panel.add(new JLabel("Owner:"));
        panel.add(ownerField);
        
        // Status
        String[] statusOptions = {"OPEN", "MITIGATED", "CLOSED"};
        JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int riskID = Integer.parseInt(riskIDField.getText());
                    int projectID = Integer.parseInt(projectIDField.getText());
                    String title = titleField.getText();
                    String description = descriptionField.getText();
                    String mitigation = mitigationField.getText();
                    String owner = ownerField.getText();
                    String status = (String) statusCombo.getSelectedItem();
                    
                    RiskEntity.Likelihood likelihood;
                    switch ((String) likelihoodCombo.getSelectedItem()) {
                        case "HIGH": likelihood = RiskEntity.Likelihood.HIGH; break;
                        case "MEDIUM": likelihood = RiskEntity.Likelihood.MEDIUM; break;
                        default: likelihood = RiskEntity.Likelihood.LOW; break;
                    }
                    
                    RiskEntity.Impact impact;
                    switch ((String) impactCombo.getSelectedItem()) {
                        case "CRITICAL": impact = RiskEntity.Impact.CRITICAL; break;
                        case "HIGH": impact = RiskEntity.Impact.HIGH; break;
                        case "MEDIUM": impact = RiskEntity.Impact.MEDIUM; break;
                        default: impact = RiskEntity.Impact.LOW; break;
                    }
                    
                    boolean success = riskService.createRisk(riskID, projectID, title, description, 
                                                            mitigation, owner, status, likelihood, impact);
                    
                    if (success) {
                        loadRiskData();
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                            "Failed to add risk. Risk ID may already exist.", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Risk ID and Project ID must be numbers", 
                        "Input Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    /**
     * Deletes the selected risk
     */
    private void deleteSelectedRisk() {
        int selectedRow = riskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a risk to delete", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int riskID = (int) tableModel.getValueAt(selectedRow, 0);
        int projectID = (int) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete Risk ID: " + riskID + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            RiskEntity risk = new RiskEntity();
            risk.setRiskID(riskID);
            risk.setProjectID(projectID);
            
            boolean success = riskService.deleteRisk(risk);
            if (success) {
                loadRiskData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting risk", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Shows detailed view of the selected risk
     */
    private void viewRiskDetails() {
        int selectedRow = riskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a risk to view", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int riskID = (int) tableModel.getValueAt(selectedRow, 0);
        int projectID = (int) tableModel.getValueAt(selectedRow, 1);
        
        RiskEntity risk = riskService.getRisk(riskID, projectID);
        
        if (risk != null) {
            JDialog dialog = new JDialog(this, "Risk Details", true);
            dialog.setSize(600, 400);
            dialog.setLocationRelativeTo(this);
            
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JPanel detailsPanel = new JPanel(new GridLayout(8, 2, 10, 10));
            
            detailsPanel.add(new JLabel("Risk ID:"));
            detailsPanel.add(new JLabel(String.valueOf(risk.getRiskID())));
            
            detailsPanel.add(new JLabel("Project ID:"));
            detailsPanel.add(new JLabel(String.valueOf(risk.getProjectID())));
            
            detailsPanel.add(new JLabel("Title:"));
            detailsPanel.add(new JLabel(risk.getTitle()));
            
            detailsPanel.add(new JLabel("Description:"));
            JTextArea descArea = new JTextArea(risk.getDescription());
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);
            descArea.setEditable(false);
            detailsPanel.add(new JScrollPane(descArea));
            
            detailsPanel.add(new JLabel("Likelihood:"));
            detailsPanel.add(new JLabel(risk.generateLikelihood()));
            
            detailsPanel.add(new JLabel("Impact:"));
            detailsPanel.add(new JLabel(risk.generateImpact()));
            
            detailsPanel.add(new JLabel("Mitigation Plan:"));
            JTextArea mitigationArea = new JTextArea(risk.getMitigationPlan());
            mitigationArea.setLineWrap(true);
            mitigationArea.setWrapStyleWord(true);
            mitigationArea.setEditable(false);
            detailsPanel.add(new JScrollPane(mitigationArea));
            
            detailsPanel.add(new JLabel("Owner:"));
            detailsPanel.add(new JLabel(risk.getOwner()));
            
            detailsPanel.add(new JLabel("Status:"));
            detailsPanel.add(new JLabel(risk.getStatus()));
            
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> dialog.dispose());
            
            panel.add(detailsPanel, BorderLayout.CENTER);
            panel.add(closeButton, BorderLayout.SOUTH);
            
            dialog.add(panel);
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Could not retrieve risk details", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Main method to start the application
     */
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RiskRegisterApp().setVisible(true);
            }
        });
    }
}