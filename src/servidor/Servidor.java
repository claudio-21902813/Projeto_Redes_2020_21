package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;

public class Servidor {

    static HashMap<String,String> listaBranca = new HashMap<>();
    static HashMap<String,String> listaNegra = new HashMap<>();
    static HashMap<String,String> listaOnline = new HashMap<>();


    public static void main(String[] args) throws IOException {

        //default listaBranca
        listaBranca = new HashMap<>();
        listaBranca.put("192.168.1.68","OFFLINE");
        listaBranca.put("192.168.10.90","OFFLINE");
        listaBranca.put("192.168.1.97","OFFLINE");
        listaNegra.put("127.0.0.1","OFFLINE");

        //default listaNegra
        listaNegra = new HashMap<>();
        listaNegra.put("192.168.1.97","OFFLINE");
        listaNegra.put("192.168.10.99","OFFLINE");

        listaOnline.put("192.168.1.68","ONLINE");
        ServerSocket server = new ServerSocket(7142);
        Socket socket = null;
        while(true)
        {
            socket = server.accept();

            //nao permitir conexoes na lista negra
            for(String hostBlocked : listaNegra.keySet()){
                if(hostBlocked.equals(socket.getInetAddress().getHostAddress())){
                    PrintStream stream = new PrintStream(socket.getOutputStream());
                    stream.println("block");
                }
            }
            //permitir conexoes na lista branca
            for (String host : listaBranca.keySet()){
                if(host.equals(socket.getInetAddress().getHostAddress()))
                {
                    System.out.println("cliente " + socket.getInetAddress().getHostAddress() + " foi conectado!");
                    listaBranca.put(host,"ONLINE");
                    listaOnline.put(host,"ONLINE");
                    PrintStream stream = new PrintStream(socket.getOutputStream());
                    stream.println("");
                    Thread t0 = new Thread(new Server_Manager(socket));//criar thread
                    t0.start();
                }
            }
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

    public Server_Manager(Socket socket) throws IOException {
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        printStream = new PrintStream(socket.getOutputStream());
    }


    /* codigo a executar numa thread separada para procotolos TCP */
    public void run(){
        try {
            String pedido = "";
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
                        printStream.println("Qual o ip a enviar msg?");
                        String ip = reader.readLine();
                        printStream.println("qual a msg? ");
                        String msg = reader.readLine();
                        System.out.println("ip=" + ip + " msg " + msg);
                        byte[] bufferMSG = msg.getBytes();
                        udp_socket = new DatagramSocket();
                            try {
                                this.address = InetAddress.getByName(ip);
                                DatagramPacket packet = new DatagramPacket(bufferMSG, bufferMSG.length, address, 9031);
                                udp_socket.send(packet);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        udp_socket.close();
                        printStream.println("UDP a fechar...");
                        break;
                    }
                    case "3":{
                        printStream.println("qual a msg a enviar para todos os host's ? ");
                        String msg = reader.readLine();
                        byte[] bufferMSG = msg.getBytes();
                        udp_socket = new DatagramSocket();

                        for (String ip: Servidor.listaOnline.keySet())
                        {
                            try {
                                this.address = InetAddress.getByName(ip);
                                DatagramPacket packet = new DatagramPacket(bufferMSG, bufferMSG.length, address, 9031);
                                udp_socket.send(packet);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        udp_socket.close();
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
        } catch (SocketException e2){
            System.out.println("Cliente Fechou o Programa ... ");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}