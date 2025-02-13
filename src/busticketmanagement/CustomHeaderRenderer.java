/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package busticketmanagement;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Asghar Qambar Rizvi
 */
public class CustomHeaderRenderer extends JLabel implements TableCellRenderer {

  public CustomHeaderRenderer() {
    super();
    setOpaque(true);
    setBackground(new Color(36, 136, 203)); // Your desired color
    setFont(new Font("Segoe UI", Font.BOLD, 22)); // Optional font settings
    setForeground(new Color(255,255,255));
    setBackground(new Color(0,204,204));
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                 boolean hasFocus, int row, int column) {
    setText(value.toString());
    return this;
  }
}

