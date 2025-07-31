package org.sample.app.client.service.impl;

import org.sample.app.client.service.AppTask;
import org.sample.app.client.service.SongLibService;
import org.sample.thrift.SongStruct;
import org.sample.util.RandUtil;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import static org.sample.util.Support.print;
import static org.sample.util.Support.println;

public class AppUserIOTaskImpl implements AppTask {

    private static final Scanner sc = new Scanner(System.in);

    private final SongLibService libService;

    public AppUserIOTaskImpl(SongLibService libService) {
        this.libService = libService;
    }

    @Override
    public void run() throws Exception {
        println("Input:");
        println("\t1: get a song");
        println("\t2: save a song");


        while (true) {
            print("input op: ");
            int op = sc.nextInt();
            switch (op) {
                case 1:
                    int id = inputSongID();
                    SongStruct song = libService.find(id);
                    println("got song: " + song);
                    break;

                case 2:
                    SongStruct songStruct = inputSongStruct();
                    libService.saveSong(songStruct);
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
        // TODO
        print("...input song id: ");
        int id = sc.nextInt();

        print("...input song name: ");
        String name = sc.next();

        print("...input rating: ");
        double rating = sc.nextDouble();

        Set<Integer> authorIDs = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            authorIDs.add(RandUtil.randInt());
        }

        ByteBuffer content = ByteBuffer.wrap(RandUtil.randBytes(1024));

        return new SongStruct(id, name, rating, authorIDs, content);
    }
}
