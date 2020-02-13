import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

public class ChatWindow extends JPanel implements ActionListener {
    //JOptionPane jOptionPane = new JOptionPane();
    JTextField jTextField;
    JTextArea jTextArea;
    JLabel jLabel;
    JFrame jFrame;

    public ChatWindow() {
    }


    public static void main(String[] args) {
        //ChatWindow a = new ChatWindow();
        while (true) {
            String a = "Heloo";
            Scanner scanner = new Scanner(System.in);
            System.out.println(scanner.nextLine());
        }
        //javax.swing.SwingUtilities.invokeLater(new Runnable() {
           // public void run() {
          //      a.myShow();
           // }
        //});
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String text = jTextField.getText();
        jTextArea.append(text + "\n");
        jTextField.setText("");
    }

    private void myShow() {

        JFrame frame = new JFrame("TextDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = (JPanel) frame.getContentPane();
        panel.setLayout(null);

        JLabel label = new JLabel("USER");
        panel.add(label);
        label.setSize(100,100);
        label.setLocation(10,-25);

        jTextField = new JTextField(50);
        panel.add(jTextField);
        jTextField.setLocation(10, 40);
        jTextField.setSize(400, 30);

        jTextArea = new JTextArea(50, 50);
        panel.add(jTextArea);
        jTextArea.setLocation(450, 40);
        jTextArea.setSize(500, 50);
        JScrollPane jScrollPane = new JScrollPane(jTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //panel.add(jScrollPane);

        frame.setSize(1000, 700);
        frame.setVisible(true);
    }
}


