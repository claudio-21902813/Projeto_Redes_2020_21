package cliente;

import java.io.IOException;
import java.net.*;


public class MsgUDP {
    private DatagramSocket socket;
    private InetAddress address;
    private byte[] buf;


    public MsgUDP(String address) throws SocketException,
            UnknownHostException {
        socket = new DatagramSocket(9031);
        this.address = InetAddress.getByName(address);
    }


    public void sendEcho() throws IOException {
        while(true) {
            buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(
                    packet.getData(), 0, packet.getLength());
            System.out.println(received);
        }
    }
    public void close() {
        socket.close();
    }

    public static void main(String[] args) {
        try {
            MsgUDP client = new MsgUDP(args[0]);
            client.sendEcho();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
