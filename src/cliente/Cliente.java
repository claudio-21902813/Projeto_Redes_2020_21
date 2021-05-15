package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;
import java.util.Scanner;

public class Cliente extends Thread{
    /*
    #########$$####
    ## VARIAVEIS ##
    ###############
     */


    private static String ip;
    private byte[] buf = new byte[256];
    private DatagramSocket udp_socket;
    private InetAddress address;

    public static Scanner teclado;

        /*
        ##################
        ###### Metodo RUN - Executa consoante cada pedido ######
        ##################
        */

    public Cliente(String address) throws SocketException,
            UnknownHostException {
        udp_socket = new DatagramSocket();
        this.address = InetAddress.getByName(address);
    }

    public Cliente(){
    }

    public String sendEcho(String msg) throws IOException {
        buf = msg.getBytes();
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, 4445);
        udp_socket.send(packet);
        packet = new DatagramPacket(buf, buf.length);
        udp_socket.receive(packet);
        String received = new String(
                packet.getData(), 0, packet.getLength());
        return received;
    }

    public void run()
    {
        Socket socket = null;
        try {
            socket = new Socket(ip, 6500);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintStream ps = new PrintStream(socket.getOutputStream());
            String menu = br.readLine();
            while (!(menu.equals("")))
            {
                System.out.println(menu);
                menu = br.readLine();
            }
            //System.out.println(TEXTO_MENU);
            String opcao = "";
            do {
                System.out.println("digite um comando-> ");
                opcao = teclado.nextLine();
                switch (opcao)
                {
                    case "0":{ // easy
                        ps.println("0");
                        menu = br.readLine();
                        while (!(menu.equals("")))
                        {
                            System.out.println(menu);
                            menu = br.readLine();
                        }
                        break;
                    }
                    case "1":{ // easy
                        ps.println("1");
                        System.out.println("****** ONLINE ******");
                        System.out.println("  HOST       Status  ");
                        String msg = br.readLine();
                        while (!(msg.equals("")))
                        {
                            System.out.println(msg);
                            msg = br.readLine();
                        }
                        break;
                    }
                    case "2":{//enviar msg a um host online
                        System.out.println("Qual o ip a enviar a msg?");
                        String texto_send = teclado.nextLine();
                        System.out.println("Qual a mensagem?");
                        String msg_udp = teclado.nextLine();
                        ps.println("2");
                        try {
                            Cliente client = new Cliente(texto_send);
                            System.out.println(client.sendEcho(msg_udp));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case "4":
                    {
                        ps.println("4");
                        System.out.println("****** Lista Branca ******");
                        System.out.println("  HOST       Status  ");
                        String msg = br.readLine();
                        while (!(msg.equals("")))
                        {
                            System.out.println(msg);
                            msg = br.readLine();
                        }
                        break;
                    }
                    case "5":
                    {
                        ps.println("5");
                        System.out.println("****** Lista Negra ******");
                        System.out.println("  HOST       Status  ");
                        String msg = br.readLine();
                        while (!(msg.equals("")))
                        {
                            System.out.println(msg);
                            msg = br.readLine();
                        }
                        break;
                    }
                    case "99":{
                        System.out.println("conexao fechou!!");
                        socket.close();
                        break;
                    }
                }

            }while (!opcao.equals("99"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        /*
     ##################
     ###### MAIN ######
     ##################
     */

    public static void main(String[] args) {
        teclado = new Scanner(System.in);
        Cliente cliente = new Cliente();
        cliente.start();
        System.out.println("fechado");
        ip = args[0];
    }


}
