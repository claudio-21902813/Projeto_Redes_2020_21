package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;
import java.util.HashMap;

public class Servidor {

    static HashMap<String,String> listaBranca = new HashMap<>();
    static HashMap<String,String> listaNegra = new HashMap<>();
    static HashMap<String,String> listaOnline = new HashMap<>();


    public static void main(String[] args) throws IOException {


        //default listaBranca
        listaBranca = new HashMap<>();
        listaBranca.put("127.0.0.1","OFFLINE");
        listaBranca.put("127.0.0.2","OFFLINE");
        listaBranca.put("127.0.0.3","OFFLINE");

        //default listaBranca
        listaNegra = new HashMap<>();
        listaNegra.put("192.168.1.222","OFFLINE");
        listaNegra.put("192.168.1.223","OFFLINE");
        listaNegra.put("192.168.1.224","OFFLINE");

        ServerSocket server = new ServerSocket(6500);
        Socket socket = null;
        while(true)
        {
            socket = server.accept();
            System.out.println("cliente connectado !");
            //verificar nas tabelas
            for (String host : listaBranca.keySet()){
                if(host.equals(socket.getInetAddress().getHostAddress()))
                {
                    listaBranca.put(host,"ONLINE");
                    listaOnline.put(host,"ONLINE");
                }
            }


            Thread t0 = new Thread(new Server_Manager(socket));//criar thread
            t0.start();
        }
    }
}

class Server_Manager implements Runnable{

    private Socket socket;
    private BufferedReader reader;
    private PrintStream printStream;
    private static final String TEXTO_MENU = "MENU CLIENTE" +
            "\n"+
            "0 - Menu Inicial\n"+
            "1 - Listar utilizadores online\n"+//tcp
            "2 - Enviar mensagem a um utilizador\n"+//tcp + udp
            "3 - Enviar mensagens a todos os utilizadores\n"+ //tcp + udp
            "4 - Lista branca de utilizadores\n"+ //tcp
            "5 - Lista negra de utilizadores\n"+ //tcp
            "99 - Sair\n";

    //UDP Variaveis
    private DatagramSocket udp_socket;
    private InetAddress address;
    private byte[] buf = new byte[256];

    public Server_Manager(Socket socket) throws IOException,SocketException,UnknownHostException {
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        printStream = new PrintStream(socket.getOutputStream());
    }


    /* codigo a executar numa thread separada para procotolos TCP */
    public void run(){
        try {
            String pedido = "";
            printStream.println(TEXTO_MENU);
            do {
                pedido = reader.readLine();
                if(pedido==null){
                    break;
                }
                switch (pedido)
                {
                    case "0":
                    {
                        printStream.println(TEXTO_MENU);
                        break;
                    }
                    case "1":{
                        StringBuilder resposta = new StringBuilder();
                        for(String host:Servidor.listaOnline.keySet())
                        {
                            resposta.append(host).append("   ").append(Servidor.listaOnline.get(host)).append("\n");
                        }
                        printStream.println(resposta);
                        break;
                    }
                    case "2":{
                        boolean running = true;
                        udp_socket = new DatagramSocket(4445);
                        while (running) {
                            try {
                                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                                udp_socket.receive(packet);
                                InetAddress address = packet.getAddress();
                                int port = packet.getPort();
                                packet = new DatagramPacket(buf, buf.length, address, port);
                                String received
                                        = new String(packet.getData(), 0, packet.getLength());
                                if (received.equals("end")) {
                                    running = false;
                                    continue;
                                }
                                udp_socket.send(packet);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }
                    case "4":{
                        StringBuilder resposta = new StringBuilder();
                        for(String host:Servidor.listaBranca.keySet())
                        {
                            resposta.append(host).append("   ").append(Servidor.listaBranca.get(host)).append("\n");
                        }
                        printStream.println(resposta);
                        break;
                    }
                    case "5":{
                        StringBuilder resposta = new StringBuilder();
                        for(String host:Servidor.listaNegra.keySet())
                        {
                            resposta.append(host).append("   ").append(Servidor.listaNegra.get(host)).append("\n");
                        }
                        printStream.println(resposta);
                        break;
                    }
                }
            }while (!pedido.equals("99"));
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}