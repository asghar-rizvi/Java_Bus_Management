/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package busticketmanagement;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Asghar Qambar Rizvi
 */
public class PassPayment extends javax.swing.JFrame {

    /**
     * Creates new form PassPayment
     */
    static String id;
     double fare;
     String reservationId;
    public PassPayment(String id) {
        initComponents();
        this.id=id;
        checkIfPayed();
        pnl_paym.setVisible(false);
        setExtendedState(MAXIMIZED_BOTH);
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
    private String getDepartureTimeForPassPayment(int reservationId) {
            String departureTime = null;

            try {
                // Load SQL Server JDBC driver
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

                // Establish the connection
                Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=mydatabase;encrypt=true;trustServerCertificate=true", "admin", "123456");

                // Prepare the stored procedure call
                CallableStatement cstmt = conn.prepareCall("{call GetDepartureTime(?)}");
                cstmt.setInt(1, reservationId);

                // Execute the stored procedure and get the result
                ResultSet rs = cstmt.executeQuery();

                if (rs.next()) {
                    departureTime = rs.getString("Departure_time");
                }

                // Close the resources
                rs.close();
                cstmt.close();
                conn.close();

            } catch (Exception e) {
                e.printStackTrace();
                // Handle SQL exception
            }

            return departureTime;
}

    private void insertIntoPayment(String paymentMethod){
                try {
                int reservation = Integer.parseInt(reservationId);
                BigDecimal amount = BigDecimal.valueOf(fare);
                String transDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=mydatabase;encrypt=true;trustServerCertificate=true", "admin", "123456");
                     CallableStatement cstmt = conn.prepareCall("{call InsertIntoPayment(?, ?, ?, ?)}")) {

                    // Set parameters for the stored procedure
                    cstmt.setInt(1, reservation);
                    cstmt.setBigDecimal(2, amount);
                    cstmt.setString(3, paymentMethod);
                    cstmt.setString(4, transDate);

                    // Execute the stored procedure
                    int rowsInserted = cstmt.executeUpdate();
                    if (rowsInserted > 0) {
                        //JOptionPane.showMessageDialog(null, "Payment inserted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        //JOptionPane.showMessageDialog(null, "Failed to insert payment", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid Reservation ID", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while inserting payment", "Error", JOptionPane.ERROR_MESSAGE);
            }
    }
    private void displayInfo(){
        try {
         Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
         Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=mydatabase;encrypt=true;trustServerCertificate=true", "admin", "123456");
         CallableStatement cstmt = conn.prepareCall("{call DisplayInfoForPayment(?)}");
        cstmt.setString(1, id); // Set parameter value
        ResultSet rs = cstmt.executeQuery();
        

        // Construct formatted string
        StringBuilder displayText = new StringBuilder();
        boolean check=false;
        while (rs.next()) {
            check=true;
            reservationId = rs.getString("ReservationId");
            String source = rs.getString("Source");
            String destination = rs.getString("Destination");
            String seatNumber = rs.getString("Seat_number");
            fare = rs.getDouble("Fare");

            // Append formatted information to the string
            displayText.append("Reservation ID: ").append(reservationId).append("\n");
            displayText.append("Source: ").append(source).append("\n");
            displayText.append("Destination: ").append(destination).append("\n");
            displayText.append("Seat Number: ").append(seatNumber).append("\n");
            displayText.append("Fare: ").append(fare).append("\n\n");
        }
        
        // Close resources
        rs.close();
        cstmt.close();
        conn.close();

        // Set the formatted string to JTextArea
            if (check) {
                txt_display.setText(displayText.toString());
            }
            else
                txt_display.setText("NO RESERVATION HAS BEEN DONE BY YOU");
        
        
    } catch (Exception ex) {
        JOptionPane.showInternalMessageDialog(this, ex);
        // Handle SQL exception
    }
    }
    private void checkIfPayed() {
    String status = "";
    boolean isConfirmed = false;
    
    try {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=mydatabase;encrypt=true;trustServerCertificate=true", "admin", "123456");
        
        CallableStatement cstmt = conn.prepareCall("{call DisplayStatus(?)}");
        cstmt.setString(1, id);
        ResultSet rs = cstmt.executeQuery();

        // Process the result set
        while (rs.next()) {
            status = rs.getString("status");
            
            if ("Confirmed".equalsIgnoreCase(status)) {
                isConfirmed = true;
                break; // No need to check further
            }
        }

        // Close resources
        rs.close();
        cstmt.close();
        conn.close();

        // Determine the message to display
        if (isConfirmed) {
            txt_display.setText("NO PAYMENTS TO MAKE");
        } else {
            displayInfo(); // Assuming displayInfo() shows the necessary payment information
        }

    } catch (Exception ex) {
        ex.printStackTrace();
        txt_display.setText("An error occurred while retrieving reservation status");
    }
}


    private int getReservationId(String passId) {
        int reservationId = -1; // Default value if reservation ID is not found
        try {
            // Connect to the database
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=mydatabase;encrypt=true;trustServerCertificate=true", "admin", "123456");
                 CallableStatement cstmt = conn.prepareCall("{call GetReservationID(?)}")) {
                
                // Set parameter for the stored procedure
                cstmt.setString(1, passId);

                // Execute the stored procedure
                ResultSet rs = cstmt.executeQuery();

                // Retrieve reservation ID from the result set
                if (rs.next()) {
                    reservationId = rs.getInt("ReservationId");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return reservationId;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new JpanelGradient();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lbl_exit = new javax.swing.JLabel();
        lbl_BookBus = new javax.swing.JLabel();
        lbl_payment = new javax.swing.JLabel();
        lbl_ticket = new javax.swing.JLabel();
        lbl_profle = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lbl_addEmployee1 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_display = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        cmb_payment = new javax.swing.JComboBox<>();
        pnl_paym = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txt_cc = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txt_cv = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txt_mm = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        btn_process = new javax.swing.JButton();
        lbl_bg = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/busticketmanagement/icons/bus.png"))); // NOI18N
        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/busticketmanagement/icons/ticket.png"))); // NOI18N
        jLabel3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/busticketmanagement/icons/profile.png"))); // NOI18N
        jLabel4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/busticketmanagement/icons/credit-card.png"))); // NOI18N
        jLabel5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/busticketmanagement/icons/logout.png"))); // NOI18N
        jLabel6.setText("jLabel6");
        jLabel6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lbl_exit.setFont(new java.awt.Font("Serif", 0, 24)); // NOI18N
        lbl_exit.setForeground(new java.awt.Color(255, 255, 255));
        lbl_exit.setText("       E X I T");
        lbl_exit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_exit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_exitMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lbl_exitMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lbl_exitMouseExited(evt);
            }
        });

        lbl_BookBus.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        lbl_BookBus.setForeground(new java.awt.Color(255, 255, 255));
        lbl_BookBus.setText("BOOK A TICKET");
        lbl_BookBus.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_BookBus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_BookBusMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lbl_BookBusMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lbl_BookBusMouseExited(evt);
            }
        });

        lbl_payment.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        lbl_payment.setForeground(new java.awt.Color(255, 255, 255));
        lbl_payment.setText("P A Y M E N T");
        lbl_payment.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_payment.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_paymentMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lbl_paymentMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lbl_paymentMouseExited(evt);
            }
        });

        lbl_ticket.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        lbl_ticket.setForeground(new java.awt.Color(255, 255, 255));
        lbl_ticket.setText("R E S E R V A T I O N S");
        lbl_ticket.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_ticket.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_ticketMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lbl_ticketMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lbl_ticketMouseExited(evt);
            }
        });

        lbl_profle.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        lbl_profle.setForeground(new java.awt.Color(255, 255, 255));
        lbl_profle.setText("P R O F I L E");
        lbl_profle.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_profle.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_profleMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lbl_profleMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lbl_profleMouseExited(evt);
            }
        });

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/busticketmanagement/icons/feedback.png"))); // NOI18N
        jLabel7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lbl_addEmployee1.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        lbl_addEmployee1.setForeground(new java.awt.Color(255, 255, 255));
        lbl_addEmployee1.setText("F E E D  B A C K");
        lbl_addEmployee1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_addEmployee1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_addEmployee1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lbl_addEmployee1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lbl_addEmployee1MouseExited(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(lbl_exit, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel3))
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_addEmployee1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_ticket, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_profle, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_payment, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_BookBus, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jLabel4))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(lbl_profle, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(61, 61, 61)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(lbl_BookBus, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(lbl_payment, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(45, 45, 45)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_ticket, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_addEmployee1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 290, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel6))
                    .addComponent(lbl_exit, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(100, 100, 100))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 280, 830));

        jLabel13.setFont(new java.awt.Font("Times New Roman", 1, 36)); // NOI18N
        jLabel13.setText("                 MAKE PAYMENTS FOR YOUR RESERVATIONS ");
        jLabel13.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(51, 51, 51)));
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 50, 1110, 80));

        txt_display.setEditable(false);
        txt_display.setBackground(new java.awt.Color(0, 204, 204));
        txt_display.setColumns(20);
        txt_display.setFont(new java.awt.Font("Serif", 1, 24)); // NOI18N
        txt_display.setRows(5);
        txt_display.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jScrollPane1.setViewportView(txt_display);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 160, 880, 340));

        jLabel2.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel2.setText("SELECT PAYMENT METHOD:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 550, 250, 50));

        cmb_payment.setFont(new java.awt.Font("Segoe UI Symbol", 1, 18)); // NOI18N
        cmb_payment.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "[- S E L E C T -]", "CASH ON DELIVERY", "DEBIT CARD" }));
        getContentPane().add(cmb_payment, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 600, 200, 30));

        pnl_paym.setBackground(new java.awt.Color(0, 204, 204));
        pnl_paym.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel8.setFont(new java.awt.Font("Perpetua Titling MT", 0, 18)); // NOI18N
        jLabel8.setText("ENTER DEBIT CARD NUMBER:");

        txt_cc.setBackground(new Color(0,0,0,0));
        txt_cc.setFont(new java.awt.Font("Nirmala UI", 0, 18)); // NOI18N
        txt_cc.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(0, 0, 0)));
        txt_cc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_ccKeyTyped(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Perpetua Titling MT", 0, 18)); // NOI18N
        jLabel9.setText("ENTER CVC:");

        txt_cv.setBackground(new Color(0,0,0,0));
        txt_cv.setFont(new java.awt.Font("Nirmala UI", 0, 18)); // NOI18N
        txt_cv.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(0, 0, 0)));
        txt_cv.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_cvKeyTyped(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Perpetua Titling MT", 0, 18)); // NOI18N
        jLabel10.setText("ENTER MM/YY:");

        txt_mm.setBackground(new Color(0,0,0,0));
        txt_mm.setFont(new java.awt.Font("Nirmala UI", 0, 18)); // NOI18N
        txt_mm.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(0, 0, 0)));
        txt_mm.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_mmKeyTyped(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(204, 204, 204));
        jButton2.setFont(new java.awt.Font("Yu Gothic Light", 1, 14)); // NOI18N
        jButton2.setText("P A Y");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_paymLayout = new javax.swing.GroupLayout(pnl_paym);
        pnl_paym.setLayout(pnl_paymLayout);
        pnl_paymLayout.setHorizontalGroup(
            pnl_paymLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_paymLayout.createSequentialGroup()
                .addContainerGap(107, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(92, 92, 92))
            .addGroup(pnl_paymLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnl_paymLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_paymLayout.createSequentialGroup()
                        .addGroup(pnl_paymLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_cv, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addGap(140, 140, 140)
                        .addGroup(pnl_paymLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_mm, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(txt_cc, javax.swing.GroupLayout.PREFERRED_SIZE, 431, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnl_paymLayout.setVerticalGroup(
            pnl_paymLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_paymLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_cc, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnl_paymLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_paymLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_cv)
                    .addComponent(txt_mm, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        getContentPane().add(pnl_paym, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 530, 600, 250));

        btn_process.setBackground(new java.awt.Color(0, 204, 204));
        btn_process.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        btn_process.setForeground(new java.awt.Color(255, 255, 255));
        btn_process.setText("P R O C E S S");
        btn_process.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_process.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_processActionPerformed(evt);
            }
        });
        getContentPane().add(btn_process, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 660, 200, 30));

        lbl_bg.setBackground(new java.awt.Color(255, 255, 255));
        lbl_bg.setForeground(new java.awt.Color(255, 255, 255));
        lbl_bg.setOpaque(true);
        getContentPane().add(lbl_bg, new org.netbeans.lib.awtextra.AbsoluteConstraints(-240, 0, 1940, 830));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    private void func(){
        int reservation = Integer.parseInt(reservationId);
        
        try {
            // Update reservation status to 'Confirmed'
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=mydatabase;encrypt=true;trustServerCertificate=true", "admin", "123456")) {
                
                CallableStatement cstmt = conn.prepareCall("{call UpdateStatusAfterBilling(?)}");
                cstmt.setString(1, id); // Set parameter value
                cstmt.executeUpdate();
                cstmt.close();
                
                // Get the reservation ID
                int reservationID = getReservationId(id);

                // Get departure time from the database or some other source
                String departureTimeFromDB = getDepartureTimeForPassPayment(reservationID); // Replace with your method of getting departure time

                if (departureTimeFromDB.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Departure time not available", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Parse departure time
                LocalDateTime departureDateTime = LocalDateTime.parse(departureTimeFromDB, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));

                // Calculate expiry date (1 hour more than departure time)
                LocalDateTime expiryDateTime = departureDateTime.plusHours(1);

                // Format expiry date to desired string format
                String expiryDate = expiryDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                // Get current date and time
                LocalDateTime currentDateTime = LocalDateTime.now();

                // Format current date and time to desired string format
                String issueDate = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                // Insert ticket into database
                try (CallableStatement cstmtInsert = conn.prepareCall("{call InsertIntoTicket(?, ?, ?)}")) {
                    // Set parameters for the stored procedure
                    cstmtInsert.setInt(1, reservationID);
                    cstmtInsert.setString(2, issueDate);
                    cstmtInsert.setString(3, expiryDate);

                    // Execute the stored procedure
                    int rowsInserted = cstmtInsert.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(null, "Ticket inserted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to insert ticket", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            JOptionPane.showMessageDialog(this, "SUCCESSFULLY DOWNLOADED");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
  }
    
    private void btn_processActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_processActionPerformed
        if (txt_display.getText().isBlank()) {
            JOptionPane.showMessageDialog(this,"NO RESERVATIONS");
        }
        else if (cmb_payment.getSelectedIndex() ==0) {
            JOptionPane.showMessageDialog(this,"SELECT AT LEAST ONE PAYMENT METHOD");
            
        }
        else if (cmb_payment.getSelectedIndex() == 1) {
            
            JOptionPane.showMessageDialog(this, "TICKET HSA BEEN RESERVED AS COD");
            insertIntoPayment("Cash on Delivery");
            func();
            
            
        }
        else if (cmb_payment.getSelectedIndex() == 2) {
            insertIntoPayment("Credit Card");
            pnl_paym.setVisible(true);
        }
    }//GEN-LAST:event_btn_processActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if ((txt_cc.getText().isBlank() || txt_cv.getText().isBlank() || txt_mm.getText().isBlank()) && cmb_payment.getSelectedIndex() ==2) {
        JOptionPane.showMessageDialog(this, "EMPTY CREDITIDENTIALS");
    }
    else {
            func();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void txt_ccKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_ccKeyTyped
        char c = evt.getKeyChar();
    if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) || txt_cc.getText().length() >= 17) {
        evt.consume();
        JOptionPane.showMessageDialog(null, "Error: Credit card number must contain only digits and must be less than 17 characters.");
    }
    }//GEN-LAST:event_txt_ccKeyTyped

    private void txt_cvKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_cvKeyTyped
         char c = evt.getKeyChar();
    // Check if the character is a digit
    if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)  ||  txt_cv.getText().length() >=4) {
        evt.consume(); // Consume the event if the character is not a digit
        JOptionPane.showMessageDialog(null, "Error: Month must contain only 4 digits");
    }
    }//GEN-LAST:event_txt_cvKeyTyped

    private void txt_mmKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_mmKeyTyped
        char c = evt.getKeyChar();
    // Check if the character is a digit
    if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
        evt.consume(); // Consume the event if the character is not a digit
        JOptionPane.showMessageDialog(null, "Error: Month must contain only digits.");
    } else {
        // Get the current text in the text field
        String text = txt_mm.getText();
        
        // Check if the length is 2 and add '/' automatically
        if (text.length() == 2 && !text.contains("/")) {
            txt_mm.setText(text + "/");
        }
        
        // Limit the length to 5 characters
        if (txt_mm.getText().length() >= 5) {
            evt.consume(); // Consume the event if the length is greater than 5
            JOptionPane.showMessageDialog(null, "Error: Month/Year must be in MM/YY format.");
        }
    }
    }//GEN-LAST:event_txt_mmKeyTyped

    private void lbl_exitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_exitMouseClicked
        HOMEPAGE home=new HOMEPAGE();
        home.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lbl_exitMouseClicked

    private void lbl_exitMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_exitMouseEntered
        lbl_exit.setForeground(new Color(0,0,0));        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_exitMouseEntered

    private void lbl_exitMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_exitMouseExited
        lbl_exit.setForeground(new Color(255,255,255));
        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_exitMouseExited

    private void lbl_BookBusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_BookBusMouseClicked
        PassBookBus bookBus=new PassBookBus(id);
        bookBus.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lbl_BookBusMouseClicked

    private void lbl_BookBusMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_BookBusMouseEntered

        lbl_BookBus.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_lbl_BookBusMouseEntered

    private void lbl_BookBusMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_BookBusMouseExited
        lbl_BookBus.setForeground(new Color(255,255,255));
    }//GEN-LAST:event_lbl_BookBusMouseExited

    private void lbl_paymentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_paymentMouseClicked
        PassPayment payment= new PassPayment(id);
        payment.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lbl_paymentMouseClicked

    private void lbl_paymentMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_paymentMouseEntered
        lbl_payment.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_lbl_paymentMouseEntered

    private void lbl_paymentMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_paymentMouseExited
        lbl_payment.setForeground(new Color(255,255,255));        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_paymentMouseExited

    private void lbl_ticketMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_ticketMouseClicked
        PassTicket ticket=new PassTicket(id);
        ticket.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lbl_ticketMouseClicked

    private void lbl_ticketMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_ticketMouseEntered
        lbl_ticket.setForeground(new Color(0,0,0));        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_ticketMouseEntered

    private void lbl_ticketMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_ticketMouseExited
        lbl_ticket.setForeground(new Color(255,255,255));        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_ticketMouseExited

    private void lbl_profleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_profleMouseClicked
        PassProfile pro=new PassProfile(id);
        pro.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lbl_profleMouseClicked

    private void lbl_profleMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_profleMouseEntered
        lbl_profle.setForeground(new Color(0,0,0));        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_profleMouseEntered

    private void lbl_profleMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_profleMouseExited
        lbl_profle.setForeground(new Color(255,255,255));
        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_profleMouseExited

    private void lbl_addEmployee1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addEmployee1MouseClicked
        PassFeedBack feed=new PassFeedBack(id);
        feed.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lbl_addEmployee1MouseClicked

    private void lbl_addEmployee1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addEmployee1MouseEntered
        lbl_addEmployee1.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_lbl_addEmployee1MouseEntered

    private void lbl_addEmployee1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_addEmployee1MouseExited
        lbl_addEmployee1.setForeground(new Color(255,255,255));
    }//GEN-LAST:event_lbl_addEmployee1MouseExited

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
            java.util.logging.Logger.getLogger(PassPayment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PassPayment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PassPayment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PassPayment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PassPayment(id).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_process;
    private javax.swing.JComboBox<String> cmb_payment;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl_BookBus;
    private javax.swing.JLabel lbl_addEmployee1;
    private javax.swing.JLabel lbl_bg;
    private javax.swing.JLabel lbl_exit;
    private javax.swing.JLabel lbl_payment;
    private javax.swing.JLabel lbl_profle;
    private javax.swing.JLabel lbl_ticket;
    private javax.swing.JPanel pnl_paym;
    private javax.swing.JTextField txt_cc;
    private javax.swing.JTextField txt_cv;
    private javax.swing.JTextArea txt_display;
    private javax.swing.JTextField txt_mm;
    // End of variables declaration//GEN-END:variables
}
