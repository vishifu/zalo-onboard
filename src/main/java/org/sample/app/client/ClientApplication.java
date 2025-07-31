package org.sample.app.client;

import org.sample.app.client.service.AppTask;
import org.sample.app.client.service.SongLibService;
import org.sample.app.client.service.impl.AppUserIOTaskImpl;
import org.sample.app.client.service.impl.SongLibServiceImpl;


public class ClientApplication {

    private static final int serverPort = 9900;
    private static final String serverHost = "localhost";

    public static void main(String[] args) throws Exception {

        SongLibService songLibService = new SongLibServiceImpl(serverHost, serverPort);
        AppTask task = new AppUserIOTaskImpl(songLibService);

        task.run();

    }
}
