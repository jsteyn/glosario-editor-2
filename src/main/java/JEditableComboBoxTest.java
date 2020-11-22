import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class JEditableComboBoxTest extends JFrame {
   /**
    *
    */
   private static final long serialVersionUID = 1L;

   public JEditableComboBoxTest() {
      setTitle("JEditableComboBox Test");
      setLayout(new BorderLayout());
      final JComboBox combobox = new JComboBox();
      final JList list = new JList(new DefaultListModel());
      add(BorderLayout.NORTH, combobox);
      add(BorderLayout.CENTER, list);
      combobox.setEditable(true);
      combobox.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent ie) {
            if (ie.getStateChange() == ItemEvent.SELECTED) {
               ((DefaultListModel) list.getModel()).addElement(combobox.getSelectedItem());
               combobox.insertItemAt(combobox.getSelectedItem(), 0);
            }
         }
      });
      setSize(new Dimension(375, 250));
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLocationRelativeTo(null);
      setVisible(true);
   }
   public static void main(String[] args) throws Exception {
      new JEditableComboBoxTest();
   }
}