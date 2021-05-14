package cliente;

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


    public static void main(String[] args) {
        teclado = new Scanner(System.in);
        inicia();
    }

    private static void inicia()
    {
        Cliente clienteThread = new Cliente();
        System.out.println(TEXTO_MENU);
        String texto = "";

        while (!(texto = teclado.nextLine()).equals("99"))
        {
            System.out.println("way");
        }
        System.out.println("a sair...");
    }


    /*
    ##################
    ###### Metodo RUN - Executa consoante cada pedido ######
    ##################
    */
    public void run()
    {

    }


}

