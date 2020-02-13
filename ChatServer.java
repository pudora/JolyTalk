
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.LocalDate;

/**
 * Chat-Client-Server Project
 *
 * This class starts and maintains the server side of the Simple Chat Server.
 * The server side is responsible for ensures that messages are received by all
 * recipients connected and manages the recipients and their data.
 *
 * @author Neso Udora, pudora@purdue.edu
 * @author Zach Skiles, skilesz@purdue.edu
 *
 * @version 2018-11-14
 */

final class ChatServer {

    //instance variables

    private static int uniqueId = 0;
    private final List<ClientThread> clients = new ArrayList<>();
    private final int port;
    private String badWords;
    public static ChatFilter chatFilter;
    public int cojunt = 0;
    private Date time = new Date();
    SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    private String date = time_formatter.format(time);
    private String log = "C:\\Users\\Neso\\Desktop\\Computer_Science\\CS_18000\\Project_4\\src\\log_files\\log_file_for_"
            + date + ".txt";
    private File logFile = new File(log);

    //constructor

    private ChatServer(int port) {
        this.port = port;
    }



    /*
     * This is what starts the ChatServer.
     * Right now it just creates the socketServer and adds a new ClientThread to a list to be handled
     */
    private void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            logFile.createNewFile();

            while (true) {
                boolean validUser = true;
                Socket socket = serverSocket.accept();
                Runnable r = new ClientThread(socket, uniqueId++);
                Thread t = new Thread(r);

                for (int i = 0; i < clients.size(); i++) {
                    if (clients.get(i).username.equals(((ClientThread) r).username) &&
                            (((ClientThread) r).username.length() < 9)) {
                        ((ClientThread) r).writeMessage("ERROR: User already exists.\n");
                        ((ClientThread) r).writeMessage("Disconnect");
                        ((ClientThread) r).close();
                        validUser = false;
                        break;
                    } else if (clients.get(i).username.equals(((ClientThread) r).username) &&
                            (((ClientThread) r).username.length() > 8) &&
                            ((ClientThread) r).username.substring(0,9).equals("Anonymous")) {
                        /*int a*/cojunt++;//= Integer.parseInt(((ClientThread) r).username.substring(9)) + 1;
                        ((ClientThread) r).username = "Anonymous" + cojunt;//a;
                        //break;
                    } else if (clients.get(i).username.equals(((ClientThread) r).username) &&
                            (((ClientThread) r).username.length() > 8) &&
                            !((ClientThread) r).username.substring(0,9).equals("Anonymous")) {
                        ((ClientThread) r).writeMessage("ERROR: User already exists.\n");
                        ((ClientThread) r).writeMessage("Disconnect");
                        ((ClientThread) r).close();
                        validUser = false;
                        break;
                    }
                }

                if (validUser) {
                    clients.add((ClientThread) r);
                    t.start();
                    System.out.println(((ClientThread) r).username + " just connected.");
                    ///////////////////////////////////////////////////
                    try {
                        FileWriter fileWriter = new FileWriter(log, true);
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                        bufferedWriter.write(((ClientThread) r).username + " just connected.\n");
                        bufferedWriter.close();
                    } catch(IOException e) {
                        System.out.println(
                                "Error writing to file '"
                                        + log + "'");
                    }
                    //////////////////////////////////////////////////
                }
            } //end while loop

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /*
     *  > java ChatServer
     *  > java ChatServer portNumber
     *  If the port number is not specified 1500 is used
     */
    public static void main(String[] args) {

        if (args.length == 0) {
            ChatServer server = new ChatServer(1500);
            server.start();
        } else if (args.length == 1) {
            ChatServer server = new ChatServer(Integer.parseInt(args[0]));
            server.start();
        } else {
            ChatServer server = new ChatServer(Integer.parseInt(args[0]));
            chatFilter = new ChatFilter(args[1]);
            server.start();
        } //end else statement


    }



    /*
     * This is a private class inside of the ChatServer
     * A new thread will be created to run this every time a new client connects.
     */
    private final class ClientThread implements Runnable {

        //instance variables

        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String username;
        ChatMessage cm;



        //constructor

        private ClientThread(Socket socket, int id) {
            this.id = id;
            this.socket = socket;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                username = (String) sInput.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }



        /*
         * This is what the client thread actually runs.
         */
        @Override
        public void run() {
            // Read the username sent to you by client
//            try {
//                cm = (ChatMessage) sInput.readObject();
//            } catch (IOException | ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//            System.out.println(username + ": Ping");
//
//
//            // Send message back to the client
//            try {
//                sOutput.writeObject("Pong");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            while (true) {
                try {
                    cm = (ChatMessage) sInput.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println(username + " disconnected or connection was reset.");
                    ////////////////////////////////////////////////
                    try {
                        FileWriter fileWriter = new FileWriter(log, true);
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                        bufferedWriter.write(username + " disconnected or connection was reset.\n");
                        bufferedWriter.close();
                    } catch(IOException ex) {
                        System.out.println(
                                "Error writing to file '"
                                        + log + "'");
                    }
                    ////////////////////////////////////////////////
                    cojunt = 0;
                    remove(this.id);
                    break;
                }

                if (cm.getType() == 1) {
                    this.writeMessage("Disconnect");
                    this.close();
                    remove(this.id);
                    System.out.println(username + " disconnected with a LOGOUT message.");
                    //////////////////////////////////////////////////
                    try {
                        FileWriter fileWriter = new FileWriter(log, true);
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                        bufferedWriter.write(username + " disconnected with a LOGOUT message.\n");
                        bufferedWriter.close();
                    } catch(IOException ex) {
                        System.out.println(
                                "Error writing to file '"
                                        + log + "'");
                    }
                    ///////////////////////////////////////////////
                    cojunt = 0;
                    break;
                } else if (cm.getType() == 2) {
                    if (username.equals(cm.getRecipient())) {
                        this.writeMessage("Cannot direct message yourself.\n");
                    } else {
                        directMessage(username + " -> " + cm.getRecipient() + ": " + cm.getMessage(),
                                cm.getRecipient(), this);
                        ///////////////////////////////////////////////
                        try {
                            FileWriter fileWriter = new FileWriter(log, true);
                            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                            bufferedWriter.write(username + " -> " + cm.getRecipient() + ": " + cm.getMessage() + "\n");
                            bufferedWriter.close();
                        } catch(IOException ex) {
                            System.out.println(
                                    "Error writing to file '"
                                            + log + "'");
                        }
                        //////////////////////////////////////////////
                    }

                } else if (cm.getType() == 3) {
                    this.writeMessage("\nCurrent Users Online: \n");
                    for (int i = 0; i < clients.size(); i++) {
                        if (!(clients.get(i).username.equals(username))) {// && !clients.get(i).username.equals("Anonymous")) {
                            //continue;
                        //} else {
                            this.writeMessage(clients.get(i).username + "\n");
                        }
                    }
                    this.writeMessage(username + " ------------------- (YOU)\n");
                    this.writeMessage(" \n");
                } else if (cm.getType() == 4) {
                    this.writeMessage("");
                }
                else {
                    if (username.length() >= 9) {
                        if (username.substring(0, 9).equals("Anonymous")) {
                            broadcast(username.substring(0, 9) + ": " + cm.getMessage());
                            System.out.println("Actual user serial number: " + username);
                            ////////////////////////////////////////////////////////////////////////////////////
                            try {
                                FileWriter fileWriter = new FileWriter(log, true);
                                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                                bufferedWriter.write("Actual user serial number: " + username + "\n");
                                bufferedWriter.close();
                            } catch (IOException ex) {
                                System.out.println(
                                        "Error writing to file '"
                                                + log + "'");
                            }
                            ////////////////////////////////////////////////////////////////////////////////////////////
                        } else {
                            broadcast(username + ": " + cm.getMessage());
                            /////////////////////////////////////////////////////
                            try {
                                FileWriter fileWriter = new FileWriter(log, true);
                                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                                bufferedWriter.write("Actual user serial number: " + username + "\n");
                                bufferedWriter.close();
                            } catch (IOException ex) {
                                System.out.println(
                                        "Error writing to file '"
                                                + log + "'");
                            }
                            ///////////////////////////////////////////////////////////////////
                        }
                    } else {
                        broadcast(username + ": " + cm.getMessage());
                        /////////////////////////////////////////////////////
                        try {
                            FileWriter fileWriter = new FileWriter(log, true);
                            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                            bufferedWriter.write("Actual user serial number: " + username + "\n");
                            bufferedWriter.close();
                        } catch (IOException ex) {
                            System.out.println(
                                    "Error writing to file '"
                                            + log + "'");
                        }
                    }
                }
            }


        }



        private boolean writeMessage(String msg) {
            if (!socket.isConnected()) {
                return false;
            } else {

                try {
                    this.sOutput.writeObject(msg);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

            }
        }



        synchronized private void remove(int id) {
            for (int i = 0; i < clients.size(); i++) {
                if (clients.get(i).id == id) {
                    clients.remove(i);
                    break;
                }
            } //end for loop

        }



        synchronized private void close() {
            try {
                sOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



    synchronized private void broadcast(String message) {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String time = format.format(now);

        if (chatFilter != null) {
            message = chatFilter.filter(message);
        }

        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).writeMessage(time + " " + message + "\n");
        }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
        try {
            FileWriter fileWriter = new FileWriter(log, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(time + " " + message + "\n");
            bufferedWriter.close();
        } catch(IOException ex) {
            System.out.println(
                    "Error writing to file '"
                            + log + "'");
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        System.out.println(time + " " + message);
    }



    synchronized private void directMessage(String message, String username, ClientThread sender) {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String time = format.format(now);

        if (chatFilter != null) {
            message = chatFilter.filter(message);
        }

        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).username.equals(username)) {
                clients.get(i).writeMessage(time + " " + message + "\n");
                System.out.println(time + " " + message);
                sender.writeMessage(time + " " + message + "\n");
                return;
            }
        }

        sender.writeMessage("User could not be found.\n");
    }

}
