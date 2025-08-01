package org.sample.app.client;

import org.sample.thrift.DateStruct;
import org.sample.util.AppTask;
import org.sample.app.client.service.SongLibService;
import org.sample.thrift.SongStruct;
import org.sample.util.RandUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
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
        println("\tany: to exit");

        while (true) {
            print("input op: ");
            int op = sc.nextInt();
            switch (op) {
                case 1:
                    int id = inputSongID();
                    SongStruct song = libService.find(id);
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


        DateStruct dateStruct = new DateStruct(1, 8, 2025);


        SongStruct song = new SongStruct(id, name, rating, authorIDs, content, dateStruct, new ArrayList<>());

        SongStruct clone1 = song.deepCopy();
        clone1.id += 1000;
        clone1.name += "_clone1";
        SongStruct clone2 = song.deepCopy();
        clone2.id += 1000;
        clone2.name += "_clone2";

        song.innerSongs.add(clone1);
        song.innerSongs.add(clone2);

        return song;
    }
}
