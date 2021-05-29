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
    private static final String TEXTO_MENU = "MENU CLIENTE" +
            "\n"+
            "0 - Menu Inicial\n"+
            "1 - Listar utilizadores online\n"+//tcp
            "2 - Enviar mensagem a um utilizador\n"+//tcp + udp
            "3 - Enviar mensagens a todos os utilizadores\n"+ //tcp + udp
            "4 - Lista branca de utilizadores\n"+ //tcp
            "5 - Lista negra de utilizadores\n"+ //tcp
            "99 - Sair\n";
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

    public void sendEcho(String msg) throws IOException {
        buf = msg.getBytes();
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, 9031);
        udp_socket.send(packet);
    }

    public void run()
    {
        Socket socket = null;
        System.out.println(TEXTO_MENU);
        try {
            socket = new Socket(ip, 7142);
            //socket.setSoTimeout(10*1000);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintStream ps = new PrintStream(socket.getOutputStream());


                if (br.readLine().equals("block")){
                    System.err.println("O seu ip esta na lista Negra\n Contacte o administrador");
                    System.exit(1);
                }

            String opcao = "";
            do {
                System.out.println("digite um comando-> ");
                opcao = teclado.nextLine();
                switch (opcao)
                {
                    case "0":{ // easy
                        ps.println("0");
                        String menu = br.readLine();
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
                        ps.println("1");
                        System.out.println("****** ONLINE ******");
                        System.out.println("  HOST       Status  ");
                        String msg = br.readLine();
                        while (!(msg.equals("")))
                        {
                            System.out.println(msg);
                            msg = br.readLine();
                        }
                        try {
                            ps.println("2");
                            System.out.println(br.readLine());
                            String ip_send = teclado.nextLine();
                            ps.println(ip_send);
                            System.out.println(br.readLine());
                            String msg_udp = teclado.nextLine();
                            ps.println(msg_udp);
                            Cliente client = new Cliente(ip);
                            client.sendEcho(msg_udp);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case "3":{
                        ps.println("3");
                        System.out.println(br.readLine());
                        String msg = teclado.nextLine();
                        ps.println(msg);
                        Cliente client = new Cliente(ip);
                        client.sendEcho(msg);
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
        //verifica os argumentos

        if(args.length != 1){
            System.err.println("Usagem: java cliente.java <ip do servidor>");
            System.exit(1);
        }
        teclado = new Scanner(System.in);
        Cliente cliente = new Cliente();
        cliente.start();
        ip = args[0];
    }


}
