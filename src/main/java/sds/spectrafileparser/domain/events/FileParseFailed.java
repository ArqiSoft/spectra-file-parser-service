package sds.spectrafileparser.domain.events;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import sds.messaging.contracts.AbstractContract;

public class FileParseFailed extends AbstractContract {

    private UUID id;
    private String timeStamp;
    private UUID userId;
    private long TotalRecords;
    private long ParsedRecords;
    private long FailedRecords;
    private String Message;  
    
    public FileParseFailed() {
        namespace = "Sds.SpectraFileParser.Domain.Events";
        contractName = FileParseFailed.class.getSimpleName();
    }

    public void setTotalRecords(long TotalRecords) {
        this.TotalRecords = TotalRecords;
    }

    public void setParsedRecords(long ParsedRecords) {
        this.ParsedRecords = ParsedRecords;
    }

    public void setFailedRecords(long FailedRecords) {
        this.FailedRecords = FailedRecords;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }  

    /**
     * @return the id
     */
    @JsonProperty("Id")
    public UUID getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * @return the timeStamp
     */
    @JsonProperty("TimeStamp")
    public String getTimeStamp() {
        return timeStamp;
    }

    /**
     * @param timeStamp the timeStamp to set
     */
    
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * @return the userId
     */
    @JsonProperty("Message")
    public UUID getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    @JsonProperty("TotalRecords")
    public long getTotalRecords() {
        return TotalRecords;
    }

    @JsonProperty("ParsedRecords")
    public long getParsedRecords() {
        return ParsedRecords;
    }

    @JsonProperty("FailedRecords")
    public long getFailedRecords() {
        return FailedRecords;
    }

    @JsonProperty("Message")
    public String getMessage() {
        return Message;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format(
                "%s [id=%s, timeStamp=%s, userId=%s, namespace=%s, contractName=%s, correlationId=%s]",
                FileParseFailed.class.getSimpleName(), id, timeStamp, userId, namespace, contractName, getCorrelationId());
    }
    
    

}
