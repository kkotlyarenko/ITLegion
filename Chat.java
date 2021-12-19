import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Vector;

public class Chat {

    public static final int PORT = 3000;
    public static LinkedList<ServerSomthing> serverList = new LinkedList<>();

    public static Story story;


    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        story = new Story();
        System.out.println("Server Started");
        try {
            while (true) {
                Socket socket = server.accept();
                try {
                    serverList.add(new ServerSomthing(socket));

                } catch (IOException e) {
                    socket.close();
                }
            }
        } finally {
            server.close();
        }
    }
}


class ServerSomthing extends Thread {
    Chat Govno = new Chat();
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;


    public ServerSomthing(Socket socket) throws IOException {
        this.socket = socket;

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        Chat.story.printStory(out);
        start();
    }
    @Override
    public void run() {
        String word;
        try {
            Vector vec = new Vector();
            word = in.readLine();
            try {
                out.write(word + "\n");
                out.flush();
            } catch (IOException ignored) {}
            try {
                while (true) {
                    word = in.readLine();
                    if(word.equals("!reg")){
                        //out.write("Вы начали процедуру регистрации" + "\n" + "Пожалуйста придумайте и введите Ваш пароль: " + "\n");
                        String pass = in.readLine();
                        System.out.print("New user password: " + pass + "\n");
                        out.flush();
                    }
                    else if(word.equals("!online"))
                    {
                        out.write("Users online: " + Govno.serverList.size() + "\n");
                        out.flush();
                    }
                    else if(word.equals("!help"))
                    {
                        out.write("To register type !reg, To login type !login, To see number of online users type !online" + "\n");
                        out.flush();
                    }
                    else if(word.equals("stop")) {
                        this.downService();
                        break;
                    }
                    System.out.println("Message: " + word);
                    if(!word.equals("!reg") || !word.equals("!online"))
                        Chat.story.addStoryEl(word);
                    for (ServerSomthing vr : Chat.serverList) {
                        vr.send(word);                     }
                }
            } catch (NullPointerException ignored) {}


        } catch (IOException e) {
            this.downService();
        }
    }


    private void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}

    }


    private void downService() {
        try {
            if(!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                for (ServerSomthing vr : Chat.serverList) {
                    if(vr.equals(this)) vr.interrupt();
                    Chat.serverList.remove(this);
                }
            }
        } catch (IOException ignored) {}
    }
}



class Story {

    private LinkedList<String> story = new LinkedList<>();



    public void addStoryEl(String el) {

        if (story.size() >= 10) {
            story.removeFirst();
            story.add(el);
        } else {
            story.add(el);
        }
    }



    public void printStory(BufferedWriter writer) {
        if(story.size() > 0) {
            try {
                writer.write("History messages" + "\n");
                for (String vr : story) {
                    writer.write(vr + "\n");
                }
                writer.write("/...." + "\n");
                writer.flush();
            } catch (IOException ignored) {}

        }

    }
}