package sds.spectrafileparser.commandhandlers;

import sds.spectrafileparser.domain.commands.ParseFile;
import sds.spectrafileparser.domain.events.RecordParsed;
import sds.spectrafileparser.domain.events.RecordParseFailed;
import sds.spectrafileparser.domain.events.FileParseFailed;
import sds.spectrafileparser.domain.events.FileParsed;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sds.spectrafileparser.domain.core.Field;
import com.npspot.jtransitlight.JTransitLightException;
import com.npspot.jtransitlight.consumer.ReceiverBusControl;
import com.npspot.jtransitlight.publisher.IBusControl;
import sds.spectrafileparser.domain.core.Record;
import sds.spectrafileparser.domain.core.JcampReader;
import com.sds.storage.BlobInfo;
import com.sds.storage.BlobStorage;
import com.sds.storage.Guid;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;
import sds.messaging.callback.AbstractMessageProcessor;
import sds.messaging.callback.MessageProcessor;

@Component
public class ParseFileProcessor extends AbstractMessageProcessor<ParseFile> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParseFileProcessor.class);

    ReceiverBusControl receiver;
    IBusControl bus;
    BlobStorage storage;

    @Autowired
    public ParseFileProcessor(ReceiverBusControl receiver, IBusControl bus,
            BlobStorage storage) throws JTransitLightException, IOException {
        this.bus = bus;
        this.receiver = receiver;
        this.storage = storage;
    }

    @Override
    public void process(ParseFile message) {

        long failedRecords = 0;
        long parsedRecords = 0;

        try {
            BlobInfo blob = storage.getFileInfo(new Guid(message.getBlobId()), message.getBucket());

            if (blob == null) {
                throw new FileNotFoundException(String.format("Blob with Id %s not found in bucket %s", new Guid(message.getBlobId()), message.getBucket()));
            }

            Iterable<Record> records = null;

            switch (FilenameUtils.getExtension(blob.getFileName()).toLowerCase()) {
                case "dx":
                case "jdx":
                    records = new JcampReader(storage.getFileStream(new Guid(message.getBlobId()), message.getBucket()));
                    break;
                default:
                    FileParseFailed fileParseFailed = new FileParseFailed();
                    fileParseFailed.setId(message.getId());
                    fileParseFailed.setParsedRecords(parsedRecords);
                    fileParseFailed.setFailedRecords(failedRecords);
                    fileParseFailed.setTotalRecords(parsedRecords + failedRecords);
                    fileParseFailed.setMessage(String.format("Cannot parse reaction file %s. Format is not supported.", blob.getFileName()));
                    fileParseFailed.setCorrelationId(message.getCorrelationId());
                    fileParseFailed.setUserId(message.getUserId());
                    fileParseFailed.setTimeStamp(getTimestamp());
                    bus.publish(fileParseFailed);
                    
                    return;
            }
            
            String bucket = message.getBucket();
            long index = 0;
            LOGGER.debug("Creating iterator.");
            List<String> fields = new ArrayList<String>();
            Iterator<Record> iterator = records.iterator();
            LOGGER.debug("Created iterator.");
            while (iterator.hasNext()) {
                try {
                    Record record = iterator.next();

                    Guid blobId = Guid.newGuid();

                    storage.addFile(blobId, blobId.toString() + ".rxn", record.getData().getBytes(), "chemical/x-jcamp-dx", bucket, null);

                    fields.addAll((Collection<String>) record.getProperties().stream().map(p -> p.getName()).collect(Collectors.toList()));

                    RecordParsed recordParsed = new RecordParsed();
                    recordParsed.setId(UUID.randomUUID());
                    recordParsed.setFileId(message.getId());
                    recordParsed.setIndex(index);
                    List<Field> arr = record.getProperties().stream().map(p -> new Field(p.getName(), p.getValue())).collect(Collectors.toList());
                    recordParsed.setFields(arr);
                    recordParsed.setBucket(message.getBucket());
                    recordParsed.setBlobId(blobId);
                    recordParsed.setCorrelationId(message.getCorrelationId());
                    recordParsed.setUserId(message.getUserId());
                    recordParsed.setTimeStamp(getTimestamp());
                    LOGGER.debug("Parsed record with index " + record.getIndex());
                    bus.publish(recordParsed);

                    parsedRecords++;
                } catch (Exception ex) {
                    RecordParseFailed recordParseFailed = new RecordParseFailed();
                    recordParseFailed.setId(UUID.randomUUID());
                    recordParseFailed.setFileId(message.getId());
                    recordParseFailed.setIndex(index);
                    recordParseFailed.setCorrelationId(message.getCorrelationId());
                    recordParseFailed.setMessage(ex.getMessage());
                    recordParseFailed.setUserId(message.getUserId());
                    recordParseFailed.setTimeStamp(getTimestamp());
                    bus.publish(recordParseFailed);
                    failedRecords++;
                }
                index++;

                //  temporary limitation: we don't want to process more than 100 records inside any file
                if (index >= 100) {
                    break;
                }
            }

            FileParsed fileParsed = new FileParsed();
            fileParsed.setId(message.getId());
            fileParsed.setParsedRecords(parsedRecords);
            fileParsed.setFailedRecords(failedRecords);
            fileParsed.setTotalRecords(parsedRecords + failedRecords);
            fileParsed.setFields(fields.stream().distinct().collect(Collectors.toList()));
            fileParsed.setCorrelationId(message.getCorrelationId());
            fileParsed.setUserId(message.getUserId());
            fileParsed.setTimeStamp(getTimestamp());
            bus.publish(fileParsed);

        } catch (Exception ex) {
            FileParseFailed fileParseFailed = new FileParseFailed();
            fileParseFailed.setId(message.getId());
            fileParseFailed.setParsedRecords(parsedRecords);
            fileParseFailed.setFailedRecords(failedRecords);
            fileParseFailed.setTotalRecords(parsedRecords + failedRecords);
            fileParseFailed.setMessage(ex.getMessage());
            fileParseFailed.setCorrelationId(message.getCorrelationId());
            fileParseFailed.setUserId(message.getUserId());
            fileParseFailed.setTimeStamp(getTimestamp());
            bus.publish(fileParseFailed);
        }
    }

    private String getTimestamp() {
        //("yyyy-MM-dd'T'HH:mm:ss'Z'")
        return LocalDateTime.now().toString();
    }

}
