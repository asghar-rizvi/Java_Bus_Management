/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package busticketmanagement;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import java.sql.CallableStatement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Asghar Qambar Rizvi
 */
public class AdminAddSch extends javax.swing.JFrame {

    /**
     * Creates new form AdminAddSch
     */
    static String username;
    public AdminAddSch(String username) {
        initComponents();
        setExtendedState(MAXIMIZED_BOTH);
        this.username=username;
        populateBusComboBox();
        populateRouteComboBox();
        displayScheduleInTable();
         CustomHeaderRenderer renderer = new CustomHeaderRenderer();
        for (int i = 0; i < 6; i++) {
          table_Display.getColumnModel().getColumn(i).setHeaderRenderer(renderer);
            }
    }
     
class JpanelGradient extends JPanel{
        protected void paintComponent(Graphics g){
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height= getHeight();
            
            Color color1 = new Color(52,143,80);
            Color color2 = new Color(86,180,211);
            GradientPaint gp= new GradientPaint(0,0,color1,180,height,color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, width, height);
        }
    }
         public void populateRouteComboBox( ) {
        try {
            // Connect to the database
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=mydatabase;encrypt=true;trustServerCertificate=true", "admin", "123456");
                 CallableStatement cstmt = conn.prepareCall("{call GetAllRouteIDs}")) {
                
                // Execute the stored procedure
                ResultSet rs = cstmt.executeQuery();

                // Populate cmb_routeId with route IDs
                //cmb_routeId.removeAllItems();
                while (rs.next()) {
                    cmb_routeId.addItem(rs.getString("RouteId"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
    }

    // Method to populate cmb_busID with available bus IDs
    public void populateBusComboBox() {
        try {
            // Connect to the database
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=mydatabase;encrypt=true;trustServerCertificate=true", "admin", "123456");
                 CallableStatement cstmt = conn.prepareCall("{call GetAvailableBusIDs}")) {
                
                // Execute the stored procedure
                ResultSet rs = cstmt.executeQuery();

                // Populate cmb_busID with available bus IDs
               //cmb_busID.removeAllItems();
                while (rs.next()) {
                    cmb_busID.addItem(rs.getString("BusNumber"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
    }
    
    public void displayScheduleInTable() {
        try {
    // Load the JDBC driver
    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

    // Connect to the database
    try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=mydatabase;encrypt=true;trustServerCertificate=true", "admin", "123456")) {
        String sql = "{call DisplaySchedule()}"; // Call the stored procedure

        try (CallableStatement stmt = conn.prepareCall(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                // Create table model
                DefaultTableModel model = new DefaultTableModel();
                model.addColumn("ScheduleId");
                model.addColumn("RouteId");
                model.addColumn("BusNumber"); // Assuming "Busid" in the view is renamed to "BusNumber"
                model.addColumn("DepartureTime");
                model.addColumn("ArrivalTime");
                model.addColumn("Fare");

                // Populate table model with data from result set
                while (rs.next()) {
                    Object[] row = new Object[6];
                    row[0] = rs.getInt("ScheduleId");
                    row[1] = rs.getInt("RouteId");
                    row[2] = rs.getString("Busid");  // Assuming the view renames "Busid" to "BusNumber"
                    row[3] = rs.getTimestamp("Departure_time");
                    row[4] = rs.getTimestamp("Arrival_time");
                    row[5] = rs.getDouble("Fare");
                    model.addRow(row);
                }

                // Set table model to table_Display
                table_Display.setModel(model);
            }
        }
    }
} catch (Exception e) {
    e.printStackTrace(); // Handle exceptions appropriately
}

}

    
     private boolean checkTimes(String departureDateTime, String arrivalDateTime) {
        try {
            // Define the date format
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            dateFormat.setLenient(false);
            
            // Parse the input dates and times
            Date parsedDepartureDate = dateFormat.parse(departureDateTime);
            java.sql.Timestamp departureTimestamp = new java.sql.Timestamp(parsedDepartureDate.getTime());
            
            Date parsedArrivalDate = dateFormat.parse(arrivalDateTime);
            java.sql.Timestamp arrivalTimestamp = new java.sql.Timestamp(parsedArrivalDate.getTime());
            
            // Get the current date and time
            Date currentDate = new Date();
            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(currentDate.getTime());
            
            // Compare the parsed dates with the current date
            if (departureTimestamp.before(currentTimestamp)) {
                // Show error message if the departure date is before the current date
                JOptionPane.showMessageDialog(null, "Error: The departure time cannot be in the past.");
                return false; // Exit the method
            }
            
            if (arrivalTimestamp.before(currentTimestamp)) {
                // Show error message if the arrival date is before the current date
                JOptionPane.showMessageDialog(null, "Error: The arrival time cannot be in the past.");
                return false; // Exit the method
            }
            
            if (arrivalTimestamp.before(departureTimestamp)) {
                // Show error message if the arrival time is before the departure time
                JOptionPane.showMessageDialog(null, "Error: The arrival time cannot be before the departure time.");
                return false; // Exit the method
            }
            
            // Continue with the processing if both dates are valid
            JOptionPane.showMessageDialog(null, "The departure and arrival times are valid.");
            return true;
            // Further processing...

        } catch (Exception e) {
            // Show error message if parsing fails
            JOptionPane.showMessageDialog(null, "Error: Invalid date format. Please enter the dates in 'yyyy-MM-dd HH:mm:ss.S' format.");
            return false;
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_delete = new javax.swing.JButton();
        btn_update = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_Display = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        jPanel6 = new JpanelGradient();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        lbl_exit4 = new javax.swing.JLabel();
        lbl_addBus = new javax.swing.JLabel();
        lbl_addDriver = new javax.swing.JLabel();
        lbl_addRoute = new javax.swing.JLabel();
        lbl_addEmployee = new javax.swing.JLabel();
        lbl_addSchedule = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lbl_addAdmin2 = new javax.swing.JLabel();
        lbl_hompage = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lbl_addAdmin3 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lbl_addAdmin4 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lbl_addAdmin5 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        lbl_addAdmin6 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jPanel2 = new JpanelGradient();
        jLabel17 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txt_arrivalTime = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        txt_departureTime = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        cmb_busID = new javax.swing.JComboBox<>();
        cmb_routeId = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        txt_fare = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        lbl_bg = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btn_delete.setBackground(new java.awt.Color(0, 153, 153));
        btn_delete.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        btn_delete.setForeground(new java.awt.Color(255, 255, 255));
        btn_delete.setText("D E L E T E");
        btn_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_deleteActionPerformed(evt);
            }
        });

        btn_update.setBackground(new java.awt.Color(0, 153, 153));
        btn_update.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        btn_update.setForeground(new java.awt.Color(255, 255, 255));
        btn_update.setText("U P D A T E");
        btn_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_updateActionPerformed(evt);
            }
        });

        table_Display.setBorder(new javax.swing.border.MatteBorder(null));
        table_Display.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        table_Display.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Schedule ID", "ROUTE ID", "BUS ID", "DEPARTURE TIME", "ARRIVAL TIME", "FARE"
            }
        ));
        table_Display.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        table_Display.setFillsViewportHeight(true);
        table_Display.setRowHeight(25);
        table_Display.setSelectionBackground(new java.awt.Color(36, 136, 203));
        table_Display.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table_Display.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table_Display.setShowGrid(true);
        table_Display.setShowVerticalLines(false);
        table_Display.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_Display);

        jLabel13.setFont(new java.awt.Font("Times New Roman", 1, 36)); // NOI18N
        jLabel13.setText("            A D D   A  S C H E D U L E");
        jLabel13.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(51, 51, 51)));

        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/busticketmanagement/icons/bus.png"))); // NOI18N
        jLabel34.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/busticketmanagement/icons/delivery.png"))); // NOI18N
        jLabel35.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/busticketmanagement/icons/division.png"))); // NOI18N
        jLabel36.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/busticketmanagement/icons/driver.png"))); // NOI18N
        jLabel37.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/busticketmanagement/icons/logout.png"))); // NOI18N
        jLabel38.setText("jLabel6");
        jLabel38.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/busticketmanagement/icons/schedule.png"))); // NOI18N
        jLabel39.setText("jLabel7");
        jLabel39.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lbl_exit4.setFont(new java.awt.Font("Serif", 0, 24)); // NOI18N
        lbl_exit4.setForeground(new java.awt.Color(255, 255, 255));
        lbl_exit4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_exit4.setText("       E X I T");
        lbl_exit4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_exit4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_exit4MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lbl_exit4MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lbl_exit4MouseExited(evt);
            }
        });

        lbl_addBus.setBackground(new Color(0,0,0,0 ));
        lbl_addBus.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        lbl_addBus.setForeground(new java.awt.Color(255, 255, 255));
        lbl_addBus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_addBus.setText("ADD A BUS");
        lbl_addBus.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_addBus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_addBusMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lbl_addBusMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lbl_addBusMouseExited(evt);
            }
        });

        lbl_addDriver.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        lbl_addDriver.setForeground(new java.awt.Color(255, 255, 255));
        lbl_addDriver.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_addDriver.setText("ADD A DRIVER");
        lbl_addDriver.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_addDriver.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_addDriverMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lbl_addDriverMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lbl_addDriverMouseExited(evt);
            }
        });

        lbl_addRoute.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        lbl_addRoute.setForeground(new java.awt.Color(255, 255, 255));
        lbl_addRoute.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_addRoute.setText("ADD A ROUTE");
        lbl_addRoute.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_addRoute.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_addRouteMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lbl_addRouteMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lbl_addRouteMouseExited(evt);
            }
        });

        lbl_addEmployee.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        lbl_addEmployee.setForeground(new java.awt.Color(255, 255, 255));
        lbl_addEmployee.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_addEmployee.setText("ADD EMPLOYEE");
        lbl_addEmployee.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_addEmployee.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_addEmployeeMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lbl_addEmployeeMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lbl_addEmployeeMouseExited(evt);
            }
        });

        lbl_addSchedule.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        lbl_addSchedule.setForeground(new java.awt.Color(255, 255, 255));
        lbl_addSchedule.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_addSchedule.setText("ADD SCHEDULE");
        lbl_addSchedule.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_addSchedule.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_addScheduleMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lbl_addScheduleMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lbl_addScheduleMouseExited(evt);
            }
        });

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/busticketmanagement/icons/administrator.png"))); // NOI18N
        jLabel8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lbl_addAdmin2.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        lbl_addAdmin2.setForeground(new java.awt.Color(255, 255, 255));
        lbl_addAdmin2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_addAdmin2.setText("ADD  ADMIN");
        lbl_addAdmin2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_addAdmin2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_addAdmin2MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lbl_addAdmin2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lbl_addAdmin2MouseExited(evt);
            }
        });

        lbl_hompage.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        lbl_hompage.setForeground(new java.awt.Color(255, 255, 255));
        lbl_hompage.setText(" H O M E   P A G E");
        lbl_hompage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_hompage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_hompageMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lbl_hompageMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lbl_hompageMouseExited(evt);
            }
        });

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/busticketmanagement/icons/homepage.png"))); // NOI18N
        jLabel9.setText("jLabel7");
        jLabel9.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lbl_addAdmin3.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        lbl_addAdmin3.setForeground(new java.awt.Color(255, 255, 255));
        lbl_addAdmin3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_addAdmin3.setText("     VIEW PASSENGERS");
        lbl_addAdmin3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_addAdmin3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_addAdmin3MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lbl_addAdmin3MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lbl_addAdmin3MouseExited(evt);
            }
        });

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/busticketmanagement/icons/passenger.png"))); // NOI18N
        jLabel10.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lbl_addAdmin4.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        lbl_addAdmin4.setForeground(new java.awt.Color(255, 255, 255));
        lbl_addAdmin4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_addAdmin4.setText("     RESERVATIONS");
        lbl_addAdmin4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_addAdmin4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_addAdmin4MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lbl_addAdmin4MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lbl_addAdmin4MouseExited(evt);
            }
        });

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/busticketmanagement/icons/reservation.png"))); // NOI18N
        jLabel11.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lbl_addAdmin5.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        lbl_addAdmin5.setForeground(new java.awt.Color(255, 255, 255));
        lbl_addAdmin5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_addAdmin5.setText("USER QUERIES");
        lbl_addAdmin5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_addAdmin5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_addAdmin5MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lbl_addAdmin5MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lbl_addAdmin5MouseExited(evt);
            }
        });

        jLabel40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/busticketmanagement/icons/query.png"))); // NOI18N
        jLabel40.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lbl_addAdmin6.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        lbl_addAdmin6.setForeground(new java.awt.Color(255, 255, 255));
        lbl_addAdmin6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_addAdmin6.setText("FEEDBACKS");
        lbl_addAdmin6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_addAdmin6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_addAdmin6MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lbl_addAdmin6MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lbl_addAdmin6MouseExited(evt);
            }
        });

        jLabel41.setIcon(new javax.swing.ImageIcon(getClass().getResource("/busticketmanagement/icons/feedback.png"))); // NOI18N
        jLabel41.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(lbl_hompage, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(lbl_addBus, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(lbl_addDriver, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_addRoute, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_addEmployee, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lbl_addSchedule, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_addAdmin2, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lbl_addAdmin3, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(lbl_addAdmin4, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lbl_addAdmin6, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_addAdmin5, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(lbl_exit4, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(lbl_hompage, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(47, 47, 47)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_addBus, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(28, 28, 28)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_addDriver, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lbl_addRoute, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(25, 25, 25)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_addEmployee, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36))
                .addGap(29, 29, 29)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_addSchedule, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39))
                .addGap(25, 25, 25)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_addAdmin2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(30, 30, 30)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(lbl_addAdmin3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(lbl_addAdmin4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_addAdmin6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41))
                .addGap(28, 28, 28)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel40)
                    .addComponent(lbl_addAdmin5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel38))
                    .addComponent(lbl_exit4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(80, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel17.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel17.setText("S E L E C T   A   R O U T  E   I D :");

        jLabel16.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel16.setText("A R R I V A L  D A T E   A N D   T I M E :");

        txt_arrivalTime.setBackground(new Color(0,0,0,0));
        txt_arrivalTime.setFont(new java.awt.Font("Trebuchet MS", 0, 24)); // NOI18N
        txt_arrivalTime.setForeground(new java.awt.Color(255, 255, 255));
        txt_arrivalTime.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(0, 0, 0)));
        txt_arrivalTime.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_arrivalTimeKeyTyped(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(0, 153, 153));
        jButton1.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("E N T E R   R E C O R D");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        txt_departureTime.setBackground(new Color(0,0,0,0));
        txt_departureTime.setFont(new java.awt.Font("Trebuchet MS", 0, 24)); // NOI18N
        txt_departureTime.setForeground(new java.awt.Color(255, 255, 255));
        txt_departureTime.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(0, 0, 0)));
        txt_departureTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_departureTimeActionPerformed(evt);
            }
        });
        txt_departureTime.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_departureTimeKeyTyped(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel18.setText("D E P A R T U R E  D A T E   A N D   T I M E :");

        cmb_busID.setBackground(new Color(0,0,0,0));
        cmb_busID.setFont(new java.awt.Font("Trebuchet MS", 0, 24)); // NOI18N
        cmb_busID.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "[- SELECT ONE-]" }));
        cmb_busID.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(0, 0, 0)));

        cmb_routeId.setBackground(new Color(0,0,0,0));
        cmb_routeId.setFont(new java.awt.Font("Trebuchet MS", 0, 24)); // NOI18N
        cmb_routeId.setForeground(new java.awt.Color(255, 255, 255));
        cmb_routeId.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "[- SELECT ONE-]" }));
        cmb_routeId.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(0, 0, 0)));
        cmb_routeId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_routeIdActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel19.setText("E N T E R   F A I R  :");

        txt_fare.setBackground(new Color(0,0,0,0));
        txt_fare.setFont(new java.awt.Font("Trebuchet MS", 0, 24)); // NOI18N
        txt_fare.setForeground(new java.awt.Color(255, 255, 255));
        txt_fare.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(0, 0, 0)));
        txt_fare.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_fareKeyTyped(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel15.setText("S E L E C T   A   B U S   I D : ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(212, 212, 212)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cmb_routeId, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(202, 202, 202)
                        .addComponent(cmb_busID, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(172, 172, 172)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txt_departureTime, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(192, 192, 192)
                        .addComponent(txt_arrivalTime, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_fare, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(192, 192, 192)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(jLabel15))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmb_routeId, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmb_busID, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addComponent(jLabel16))
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(txt_departureTime, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txt_arrivalTime, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(txt_fare, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel19)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        lbl_bg.setBackground(new java.awt.Color(255, 255, 255));
        lbl_bg.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(140, 140, 140)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(240, 240, 240)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 960, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(170, 170, 170)
                        .addComponent(btn_update, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(300, 300, 300)
                        .addComponent(btn_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addComponent(lbl_bg, javax.swing.GroupLayout.PREFERRED_SIZE, 1940, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_update, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addComponent(lbl_bg, javax.swing.GroupLayout.PREFERRED_SIZE, 1080, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String departureTime = txt_departureTime.getText();
        String arrivalTime = txt_arrivalTime.getText();
        String busId = cmb_busID.getSelectedItem().toString();
        int routeId = Integer.parseInt(cmb_routeId.getSelectedItem().toString()); 
        float fare = Float.parseFloat(txt_fare.getText()); // Convert fare to double
        
        if (departureTime.isEmpty() || arrivalTime.isEmpty() || busId.isEmpty() || txt_fare.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Fields cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            if (!checkTimes(departureTime, arrivalTime)) {
                return;
            }
            else{
            try {
                // Connect to the database
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=mydatabase;encrypt=true;trustServerCertificate=true", "admin", "123456");
                     CallableStatement cstmt = conn.prepareCall("{call InsertSchedule(?, ?, ?, ?, ?)}")) {

                    cstmt.setInt(1, routeId);
                    cstmt.setString(2, busId);
                    cstmt.setString(3, departureTime);
                    cstmt.setString(4, arrivalTime);
                    cstmt.setFloat(5, fare); 

                    // Execute the stored procedure
                    int rowsInserted = cstmt.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(null, "Schedule inserted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        txt_arrivalTime.setText("");
                        txt_departureTime.setText("");
                        txt_fare.setText("");
                        cmb_busID.setSelectedIndex(0);
                        cmb_routeId.setSelectedIndex(0);
                        displayScheduleInTable();
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to insert schedule", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txt_departureTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_departureTimeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_departureTimeActionPerformed

    private void btn_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_deleteActionPerformed
        int selectedRow = table_Display.getSelectedRow();
        if (selectedRow==-1) {
            JOptionPane.showMessageDialog(this, "SELECT A ROW FIRST ");
        }
        else{
             try {
            
            if (selectedRow != -1) {
                // Get the ScheduleId from the first column of the selected row
                int scheduleId = (int) table_Display.getValueAt(selectedRow, 0);

                // Connect to the database
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=mydatabase;encrypt=true;trustServerCertificate=true", "admin", "123456");
                     CallableStatement cstmt = conn.prepareCall("{call DeleteSchedule(?)}")) {

                    cstmt.setInt(1, scheduleId);

                    int rowsAffected = cstmt.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Schedule record deleted successfully!");
                        displayScheduleInTable();
                    } else {
                        // No rows affected, schedule record not found or deletion failed
                        JOptionPane.showMessageDialog(null, "Failed to delete schedule record or record not found");
                    }
                }
            } else {
                // No row selected
                JOptionPane.showMessageDialog(null, "Please select a schedule record to delete");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
    }//GEN-LAST:event_btn_deleteActionPerformed
    }
    private void btn_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_updateActionPerformed
        int selectedRow = table_Display.getSelectedRow();
        if (selectedRow==-1) {
            JOptionPane.showMessageDialog(this, "SELECT A ROW FIRST ");
        }
        else{
                String scheduleId = table_Display.getValueAt(selectedRow, 0).toString();
                String routeId = table_Display.getValueAt(selectedRow, 1).toString();
                String busId = table_Display.getValueAt(selectedRow, 2).toString();
                String departureTime = table_Display.getValueAt(selectedRow, 3).toString();
                String arrivalTime = table_Display.getValueAt(selectedRow, 4).toString();
                String fare = table_Display.getValueAt(selectedRow, 5).toString();
                UpdateSchedule updateSch = new UpdateSchedule(scheduleId,routeId,busId,departureTime,arrivalTime,fare,username);
                updateSch.setVisible(true);
                
        }
    }//GEN-LAST:event_btn_updateActionPerformed

    private void cmb_routeIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_routeIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_routeIdActionPerformed

    private void txt_departureTimeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_departureTimeKeyTyped
      char c = evt.getKeyChar();
        if (Character.isAlphabetic(c)) {
            evt.consume();
         
                JOptionPane.showMessageDialog(null, "Error:  must contain only digits.");
            
        }  
    }//GEN-LAST:event_txt_departureTimeKeyTyped

    private void txt_arrivalTimeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_arrivalTimeKeyTyped
char c = evt.getKeyChar();
        if (Character.isAlphabetic(c)) {
            evt.consume();
         
                JOptionPane.showMessageDialog(null, "Error:  must contain only digits.");
            
        }          // TODO add your handling code here:
    }//GEN-LAST:event_txt_arrivalTimeKeyTyped

    private void txt_fareKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_fareKeyTyped
        char c = evt.getKeyChar();
    
    // Allow control characters like backspace, delete, arrow keys
    if (!Character.isDigit(c) && !Character.isISOControl(c)) {
        evt.consume();
        JOptionPane.showMessageDialog(null, "Error: Fare must contain only digits.");
    } else if (txt_fare.getText().length() >= 6 && !Character.isISOControl(c)) {
        evt.consume();
        JOptionPane.showMessageDialog(null, "Error: Fare must be less than 6 digits.");
    }
    }//GEN-LAST:event_txt_fareKeyTyped

    private void lbl_exit4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_exit4MouseClicked
        HOMEPAGE home=new HOMEPAGE();
        home.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lbl_exit4MouseClicked

    private void lbl_exit4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_exit4MouseEntered
        lbl_exit4.setForeground(new Color(0,0,0));        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_exit4MouseEntered

    private void lbl_exit4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_exit4MouseExited
        lbl_exit4.setForeground(new Color(255,255,255));
        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_exit4MouseExited

    private void lbl_addBusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addBusMouseClicked
        AdminAddBus addBus=new AdminAddBus(username);
        addBus.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lbl_addBusMouseClicked

    private void lbl_addBusMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addBusMouseEntered
        lbl_addBus.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_lbl_addBusMouseEntered

    private void lbl_addBusMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addBusMouseExited
        lbl_addBus.setForeground(new Color(255,255,255));
    }//GEN-LAST:event_lbl_addBusMouseExited

    private void lbl_addDriverMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addDriverMouseClicked
        AdminAddDriver addDriver=new AdminAddDriver(username);
        addDriver.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lbl_addDriverMouseClicked

    private void lbl_addDriverMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addDriverMouseEntered
        lbl_addDriver.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_lbl_addDriverMouseEntered

    private void lbl_addDriverMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addDriverMouseExited
        lbl_addDriver.setForeground(new Color(255,255,255));        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_addDriverMouseExited

    private void lbl_addRouteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addRouteMouseClicked
        AdminAddRoute addRoute = new AdminAddRoute(username);
        addRoute.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lbl_addRouteMouseClicked

    private void lbl_addRouteMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addRouteMouseEntered
        lbl_addRoute.setForeground(new Color(0,0,0));        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_addRouteMouseEntered

    private void lbl_addRouteMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addRouteMouseExited
        lbl_addRoute.setForeground(new Color(255,255,255));        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_addRouteMouseExited

    private void lbl_addEmployeeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addEmployeeMouseClicked
        AdminAddEmploye addEmp=new AdminAddEmploye(username);
        addEmp.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lbl_addEmployeeMouseClicked

    private void lbl_addEmployeeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addEmployeeMouseEntered
        lbl_addEmployee.setForeground(new Color(0,0,0));        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_addEmployeeMouseEntered

    private void lbl_addEmployeeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addEmployeeMouseExited
        lbl_addEmployee.setForeground(new Color(255,255,255));
        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_addEmployeeMouseExited

    private void lbl_addScheduleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addScheduleMouseClicked
        AdminAddSch addSch = new AdminAddSch(username);
        addSch.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lbl_addScheduleMouseClicked

    private void lbl_addScheduleMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addScheduleMouseEntered
        lbl_addSchedule.setForeground(new Color(0,0,0));        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_addScheduleMouseEntered

    private void lbl_addScheduleMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addScheduleMouseExited
        lbl_addSchedule.setForeground(new Color(255,255,255));        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_addScheduleMouseExited

    private void lbl_addAdmin2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addAdmin2MouseClicked
        AdminAddNew addAdmin = new AdminAddNew(username);
        addAdmin.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lbl_addAdmin2MouseClicked

    private void lbl_addAdmin2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addAdmin2MouseEntered
        lbl_addAdmin2.setForeground(new Color(0,0,0));        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_addAdmin2MouseEntered

    private void lbl_addAdmin2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addAdmin2MouseExited
        lbl_addAdmin2.setForeground(new Color(255,255,255));        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_addAdmin2MouseExited

    private void lbl_hompageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_hompageMouseClicked
        AdminHome home= new AdminHome(username);
        home.setVisible(true);
        this.setVisible(true);
    }//GEN-LAST:event_lbl_hompageMouseClicked

    private void lbl_hompageMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_hompageMouseEntered
        lbl_hompage.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_lbl_hompageMouseEntered

    private void lbl_hompageMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_hompageMouseExited
        lbl_hompage.setForeground(new Color(255,255,255));
    }//GEN-LAST:event_lbl_hompageMouseExited

    private void lbl_addAdmin3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addAdmin3MouseClicked
        AdminViewPass pass=new AdminViewPass(username);
        pass.setVisible(true);
        this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_addAdmin3MouseClicked

    private void lbl_addAdmin3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addAdmin3MouseEntered
        lbl_addAdmin3.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_lbl_addAdmin3MouseEntered

    private void lbl_addAdmin3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addAdmin3MouseExited
        lbl_addAdmin3.setForeground(new Color(255,255,255));
    }//GEN-LAST:event_lbl_addAdmin3MouseExited

    private void lbl_addAdmin4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addAdmin4MouseClicked
        AdminViewReser res = new AdminViewReser(username);
        res.setVisible(true);
        this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_addAdmin4MouseClicked

    private void lbl_addAdmin4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addAdmin4MouseEntered
        lbl_addAdmin4.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_lbl_addAdmin4MouseEntered

    private void lbl_addAdmin4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addAdmin4MouseExited
        lbl_addAdmin4.setForeground(new Color(255,255,255));
    }//GEN-LAST:event_lbl_addAdmin4MouseExited

    private void lbl_addAdmin5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addAdmin5MouseClicked
        AdminViewUserQueries query=new AdminViewUserQueries(username);
        query.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lbl_addAdmin5MouseClicked

    private void lbl_addAdmin5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addAdmin5MouseEntered
        lbl_addAdmin5.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_lbl_addAdmin5MouseEntered

    private void lbl_addAdmin5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addAdmin5MouseExited
        lbl_addAdmin5.setForeground(new Color(255,255,255));
    }//GEN-LAST:event_lbl_addAdmin5MouseExited

    private void lbl_addAdmin6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addAdmin6MouseClicked
        AdminViewFeedback feed=new AdminViewFeedback(username);
        feed.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lbl_addAdmin6MouseClicked

    private void lbl_addAdmin6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addAdmin6MouseEntered
        lbl_addAdmin6.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_lbl_addAdmin6MouseEntered

    private void lbl_addAdmin6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addAdmin6MouseExited
        lbl_addAdmin6.setForeground(new Color(255,255,255));
    }//GEN-LAST:event_lbl_addAdmin6MouseExited

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AdminAddSch.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminAddSch.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminAddSch.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminAddSch.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminAddSch(username).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_delete;
    private javax.swing.JButton btn_update;
    private javax.swing.JComboBox<String> cmb_busID;
    private javax.swing.JComboBox<String> cmb_routeId;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl_addAdmin2;
    private javax.swing.JLabel lbl_addAdmin3;
    private javax.swing.JLabel lbl_addAdmin4;
    private javax.swing.JLabel lbl_addAdmin5;
    private javax.swing.JLabel lbl_addAdmin6;
    private javax.swing.JLabel lbl_addBus;
    private javax.swing.JLabel lbl_addDriver;
    private javax.swing.JLabel lbl_addEmployee;
    private javax.swing.JLabel lbl_addRoute;
    private javax.swing.JLabel lbl_addSchedule;
    private javax.swing.JLabel lbl_bg;
    private javax.swing.JLabel lbl_exit4;
    private javax.swing.JLabel lbl_hompage;
    private javax.swing.JTable table_Display;
    private javax.swing.JTextField txt_arrivalTime;
    private javax.swing.JTextField txt_departureTime;
    private javax.swing.JTextField txt_fare;
    // End of variables declaration//GEN-END:variables
}
