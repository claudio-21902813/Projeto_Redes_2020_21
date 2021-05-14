package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Cliente extends Thread{
    /*
    #########$$####
    ## VARIAVEIS ##
    ###############
     */

    public static Scanner teclado;
    private static final String TEXTO_MENU = "MENU CLIENTE" +
            "\n\n"+
            "0 - Menu Inicial\n"+
            "1 - Listar utilizadores online\n"+//tcp
            "2 - Enviar mensagem a um utilizador\n"+//tcp + udp
            "3 - Enviar mensagens a todos os utilizadores\n"+ //tcp + udp
            "4 - Lista branca de utilizadores\n"+ //tcp
            "5 - Lista negra de utilizadores\n"+ //tcp
            "99 - Sair";

    /*
     ##################
     ###### MAIN ######
     ##################
     */

    private static String ip;

    public static void main(String[] args) {
        teclado = new Scanner(System.in);
        Cliente cliente = new Cliente();
        cliente.start();
        ip = args[0];
        System.out.println(args[0]);
    }

        /*
        ##################
        ###### Metodo RUN - Executa consoante cada pedido ######
        ##################
        */
    public void run()
    {
        Socket socket = null;
        try {
            socket = new Socket(ip, 6500);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintStream ps = new PrintStream(socket.getOutputStream());
            /*ps.println("Ola Servidor !!"); // escreve mensagem na socket
            // imprime resposta do servidor
            System.out.println("Recebido : " + br.readLine());
            // termina socket*/
            System.out.println(TEXTO_MENU);
            String opcao = "";
           do {
               System.out.println("digite um comando-> ");
               opcao = teclado.nextLine();
               switch (opcao)
               {
                   case "1":{ // easy
                       System.out.println(TEXTO_MENU);
                       break;
                   }
                   case "2":{ // easy
                       ps.println("2");
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



}

