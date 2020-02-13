
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/**
 * Chat-Client-Server Project
 * <p>
 * This class starts and maintains the client side of the Simple Chat Server.
 * The client receives two arguments, the port - which would enable connection
 * to the server and by default is assigned to port 1500 - and a username - which
 * would enable users be distinguished. by default, a client would simply be
 * assigned an anonymous username
 *
 * @author Neso Udora, pudora@purdue.edu
 * @author Zach Skiles, skilesz@purdue.edu
 * @version 2018-11-14
 */

final class ChatClient {

    //instance variables

    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;

    private final String server;
    private final String username;
    private final int port;
    private static String anon;

    private JFrame frame;
    private JTextField jTextField;
    private JTextArea jTextArea;
    private JPanel panel;
    private JLabel label;
    private JScrollPane jScrollPane;
    private JLabel logo;
    private Image my_logo;
    private JScrollPane jScrollPane2;
    //private int color;////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////8 July 2019

    /*
     * ChatClient constructor takes three arguments, which can all be ""
     */

    private ChatClient(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;

        // Frame and Panel creation here:
        this.frame = new JFrame("JolyTalk!");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.panel = (JPanel) frame.getContentPane();
        this.panel.setLayout(null);

        // JLabel stuff happens here:
        this.label = new JLabel("User: " + username);
        this.panel.add(label);
        this.label.setSize(1000, 100);
        this.label.setFont(new Font("Arial", Font.BOLD, 20));
        this.label.setLocation(350, -25);
        this.label.setForeground(Color.orange);

        // JTextField things happen here:
        this.jTextField = new JTextField(50);
        this.panel.add(jTextField);
        this.jTextField.setLocation(10, 50);
        this.jTextField.setSize(400, 30);
        //this.jTextField.setForeground(Color.red);///////////////////////////////////////////////////////////////////// un-comment if you want textfield text to have colors

        // JTextArea things happen here:
        this.jTextArea = new JTextArea(50, 50);
        this.jTextArea.setEditable(false);
        this.jTextArea.setFont(new Font("Comic Sans", Font.PLAIN, 15));
        //this.panel.add(jTextArea);
        //this.jTextArea.setLocation(450, 40);
        //this.jTextArea.setSize(500, 50);
        jTextArea.setLineWrap(true);
        jTextArea.setWrapStyleWord(true);
        this.jTextArea.setSelectedTextColor(Color.red);
        this.jScrollPane = new JScrollPane(jTextArea);
        this.jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.jScrollPane.setPreferredSize(new Dimension(500, 50));
        this.panel.add(jScrollPane);
        //this.jScrollPane.setLocation(450, 40);
        this.jScrollPane.setBounds(450, 50, 500, 450);

        // Logo stuff happens here:
        try {
            my_logo = ImageIO.read(new File("C:\\Users\\Neso\\Pictures\\JolysonLogo.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        my_logo = my_logo.getScaledInstance(500, 500, Image.SCALE_SMOOTH);
        this.logo = new JLabel(new ImageIcon(my_logo));
        this.panel.add(logo);
        this.logo.setLocation(-10, 100);
        this.logo.setSize(500, 500);

        // Finishes off the whole frame.
        this.panel.setBackground(Color.DARK_GRAY);
        this.frame.setSize(1000, 700);
        this.frame.setVisible(true);

    }


    /*
     * This starts the Chat Client and checks if a server with the entered details is running.
     * It closes once a server is not found and terminates the program.
     */

    private boolean start() {
        // Create a socket
        try {
            socket = new Socket(server, port);
        } catch (IOException e) {
            System.out.println("No Server available. Try again later.");
            JOptionPane jOptionPane = new JOptionPane();
            jOptionPane.showMessageDialog(null, "No Server available! Please try again later");
            this.frame.setVisible(false);
            System.exit(0);
            return false;
        }

        // Create your input and output streams
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // This thread will listen from the server for incoming messages
        Runnable r = new ListenFromServer();
        Thread t = new Thread(r);
        t.start();

        // After starting, send the clients username to the server.
        try {
            sOutput.writeObject(username);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }


    /*
     * This method is used to send a ChatMessage Object to the server
     */
    private void sendMessage(ChatMessage msg) throws IOException {
        sOutput.writeObject(msg);
    }


    /*
     * To start the Client use one of the following command
     * > java ChatClient
     * > java ChatClient username
     * > java ChatClient username portNumber
     * > java ChatClient username portNumber serverAddress
     *
     * If the portNumber is not specified 1500 should be used
     * If the serverAddress is not specified "localHost" should be used
     * If the username is not specified "Anonymous" should be used
     */
    public static void main(String[] args) {

        //Scanner in = new Scanner(System.in);// This scanner was used when zach and I
        // worked on the command prompt version. Can be removed but left just for development tracking purposes
        JTextField username_in = new JTextField(5);
        JTextField server_in = new JTextField(5);
        JTextField port_in = new JTextField(5);
        JPanel inquiry_p = new JPanel();
        inquiry_p.add(new JLabel("Username:"));
        inquiry_p.add(username_in);
        inquiry_p.add(Box.createHorizontalStrut(15)); // a space between the username field and server field
        inquiry_p.add(new JLabel("Server:"));
        inquiry_p.add(server_in);
        inquiry_p.add(Box.createHorizontalStrut(15)); // a space between the server field and the port field
        inquiry_p.add(new JLabel("Port:"));
        inquiry_p.add(port_in);

        ChatClient client;
        String input;// again something from the command line version

        int result = JOptionPane.showConfirmDialog(null, inquiry_p,
                "Please enter values you'd like to use. All fields can be left blank if wanted.", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if ((username_in.getText().equalsIgnoreCase("")) &&
                    (port_in.getText().equalsIgnoreCase("")) &&
                    (server_in.getText().equalsIgnoreCase(""))) {//
                client = new ChatClient("localHost", 1500, "Anonymous");//
                client.start();//
            } else if (!(username_in.getText().equalsIgnoreCase("")) &&
                    (port_in.getText().equalsIgnoreCase("")) &&
                    (server_in.getText().equalsIgnoreCase(""))) {//
                client = new ChatClient("localHost", 1500, username_in.getText());//
                client.start();//
            } else if ((username_in.getText().equalsIgnoreCase("")) &&
                    !(port_in.getText().equalsIgnoreCase("")) &&
                    (server_in.getText().equalsIgnoreCase(""))) {//
                client = new ChatClient("localHost", Integer.parseInt(port_in.getText()), "Anonymous");//
                client.start();//
            } else if ((username_in.getText().equalsIgnoreCase("")) &&
                    (port_in.getText().equalsIgnoreCase("")) &&
                    !(server_in.getText().equalsIgnoreCase(""))) {//
                client = new ChatClient(server_in.getText(), Integer.parseInt(port_in.getText()), "Anonymous");//
                client.start();//
            } else if ((username_in.getText().equalsIgnoreCase("")) &&
                    !(port_in.getText().equalsIgnoreCase("")) &&
                    !(server_in.getText().equalsIgnoreCase(""))) {//
                client = new ChatClient(server_in.getText(), Integer.parseInt(port_in.getText()), "Anonymous");//
                client.start();//
            } else if (!(username_in.getText().equalsIgnoreCase("")) &&
                    !(port_in.getText().equalsIgnoreCase("")) &&
                    (server_in.getText().equalsIgnoreCase(""))) {//
                client = new ChatClient("localHost", Integer.parseInt(port_in.getText()), username_in.getText());//
                client.start();//
            } else if (!(username_in.getText().equalsIgnoreCase("")) &&
                    (port_in.getText().equalsIgnoreCase("")) &&
                    !(server_in.getText().equalsIgnoreCase(""))) {//
                client = new ChatClient(server_in.getText(), 1500, username_in.getText());//
                client.start();//
            } else {//
                client = new ChatClient(server_in.getText(), Integer.parseInt(port_in.getText()), username_in.getText());//
                client.start();//
            }//
        } else {
            client = null;
            System.exit(0);
        }

        /*
         * The code below is gthe command line equivalent of the code that I re-wrote above
         */

        /*
        // Get proper arguments and override defaults
        if (args.length == 0) {//
            client = new ChatClient("localHost", 1500, "Anonymous");//
            client.start();//
        } else if (args.length == 1) {//
            client = new ChatClient("localHost", 1500, args[0]);//
            client.start();//
        } else if (args.length == 2) {//
            client = new ChatClient("localHost", Integer.parseInt(args[1]), args[0]);//
            client.start();//
        } else {//
            client = new ChatClient(args[2], Integer.parseInt(args[1]), args[0]);//
            client.start();//
        }//
        */

        //while (true) {// this while loop is necessary for the command line version

        client.jTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                anon = client.jTextField.getText();
                client.jTextField.setText("");
                String input = anon;
                if (client.socket == null || client.socket.isClosed()) {
                    return;
                }

                try {
                    if (input.equalsIgnoreCase("/logout")) {
                        client.sendMessage(new ChatMessage(1, input));
                        client.frame.setVisible(false);
                        System.exit(0);//return;
                    } else if (input.length() >= 5 && input.substring(0, 5).equalsIgnoreCase("/msg ") &&
                            input.split(" ").length >= 3) {
                        String[] directMessage = input.split(" ");
                        client.sendMessage(new ChatMessage(2, directMessage[1],
                                input.substring(6 + directMessage[1].length())));
                    } else if (input.equalsIgnoreCase("/list")) {
                        client.sendMessage(new ChatMessage(3, input));
                    } else if (input.equalsIgnoreCase("") || input.equalsIgnoreCase(" ")) {
                        client.sendMessage(new ChatMessage(4, input));
                    } else {
                        client.sendMessage(new ChatMessage(0, input));
                    }
                } catch (IOException eg) {
                    System.exit(0);//return;
                }
            }
        });

        /*
         * Once again, below is some more code from the command line version
         */

        //  input = in.nextLine();//

        //if (client.socket == null || client.socket.isClosed()) {//
        //  break;//
        //}//

        //try {//
        //  if (input.equalsIgnoreCase("/logout")) {//
        //    client.sendMessage(new ChatMessage(1, input));//

        //  break;//
        //} else if (input.length() >= 5 && input.substring(0, 5).equalsIgnoreCase("/msg ") &&//
        //      input.split(" ").length >= 3) {//
        // String[] directMessage = input.split(" ");//
        // client.sendMessage(new ChatMessage(2, directMessage[1],//
        //       input.substring(6 + directMessage[1].length())));//
        //} else if (input.equalsIgnoreCase("/list")) {//
        //  client.sendMessage(new ChatMessage(3, input));//
        //} else {//
        //  client.sendMessage(new ChatMessage(0, input));//
        //}//
        //} catch (IOException e) {//
        //  break;//
        //}//
        //}//

    }//


    /*
     * This is a private class inside of the ChatClient
     * It will be responsible for listening for messages from the ChatServer.
     * ie: When other clients send messages, the server will relay it to the client.
     */

    private final class ListenFromServer implements Runnable {
        public void run() {

            while (true) {
                try {
                    String msg = (String) sInput.readObject();

                    if (msg.equalsIgnoreCase("")) {
                        JOptionPane jOptionPane = new JOptionPane();
                        jOptionPane.showMessageDialog(null, "ERROR! CAN'T HAVE EMPTY INPUT.");
                    }

                    if (msg.equalsIgnoreCase("Disconnect")) {
                        sOutput.close();
                        JOptionPane jOptionPane = new JOptionPane();
                        jOptionPane.showMessageDialog(null, "ERROR! USER ALREADY EXISTS. \n " +
                                "Please try a different username.");
                        frame.setVisible(false);
                        System.exit(0);
                        break;
                    }

                    System.out.print(msg);
                    jTextArea.append(msg);
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Server disconnected or connection reset. Try again later.");
                    JOptionPane jOptionPane = new JOptionPane();
                    jOptionPane.showMessageDialog(null, "Server disconnected or connection reset." +
                            "Please try again later.");
                    System.exit(0);
                    break;
                }
            } //end while loop

        }
    }
}
