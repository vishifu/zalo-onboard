package org.sample.app.server;

import org.sample.app.server.service.SongDataService;
import org.sample.app.server.service.impl.SongDataServiceImpl;
import org.sample.mongo.MongoContext;
import org.sample.mongo.MongoConstants;
import org.sample.util.AppTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerApplication {

    private static final Logger log = LoggerFactory.getLogger(ServerApplication.class);
    private static final int port = 9900;

    public static void main(String[] args) throws Exception {
        String conn = System.getenv("MONGODB_CONNECTION_STRING");
        log.info("detect MONGO connection string: {}", conn);
        MongoContext context = new MongoContext();
        context.connect(conn, MongoConstants.DATABASE_NAME);

        SongDataService songDataService = new SongDataServiceImpl(context);
        AppTask task = () -> songDataService.process(port);
        task.run();
    }

}
