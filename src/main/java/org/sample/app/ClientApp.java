package org.sample.app;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.sample.thrift.Player;
import org.sample.thrift.SongStruct;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.Scanner;


public class ClientApp {
    private static final Scanner sc = new Scanner(System.in);
    private static final Random rand = new Random(System.nanoTime());

    private static final int serverPort = 9900;
    private static final String serverHost = "localhost";

    public static void main(String[] args) throws TException {
        TTransport transport = new TSocket(serverHost, serverPort);
        TProtocol protocol = new TBinaryProtocol(transport);
        transport.open();

        Player.Client client = new Player.Client(protocol);
        work(client);
    }

    static void work(Player.Client client) throws TException {
        println("Input:");
        println("\t1: get a song");
        println("\t2: save a song");


        while (true) {
            print("input op: ");
            int op = sc.nextInt();
            switch (op) {
                case 1:
                    int id = inputSongID();
                    SongStruct song = client.get(id);
                    println("got song: " + song);
                    break;

                case 2:
                    SongStruct songStruct = inputSongStruct();
                    client.save(songStruct);
                    println("...saving song " + songStruct.id);
                    break;

                default:
                    return;
            }
        }
    }

    private static int inputSongID() {
        print("...input song id: ");
        int id = sc.nextInt();

        return id;
    }

    private static SongStruct inputSongStruct() {
        print("...input song id: ");
        int id = sc.nextInt();

        print("...input song name: ");
        String name = sc.next();

        print("...input rating: ");
        double rating = sc.nextDouble();

        byte[] bytes = new byte[1024];
        rand.nextBytes(bytes);
        ByteBuffer content = ByteBuffer.wrap(bytes);
        return new SongStruct(id, name, rating, content);
    }

    private static void print(String text) {
        System.out.print(text);
    }

    private static void println(String text) {
        System.out.println(text);
    }
}
