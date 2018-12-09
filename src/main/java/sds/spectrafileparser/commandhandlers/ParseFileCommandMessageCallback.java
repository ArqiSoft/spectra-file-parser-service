package sds.spectrafileparser.commandhandlers;

import sds.spectrafileparser.domain.commands.ParseFile;
import java.util.concurrent.BlockingQueue;
import sds.messaging.callback.AbstractMessageCallback;

public class ParseFileCommandMessageCallback extends AbstractMessageCallback<ParseFile> {

    public ParseFileCommandMessageCallback(Class<ParseFile> tClass, BlockingQueue<ParseFile> queue) {
        super(tClass, queue);
    }

}
