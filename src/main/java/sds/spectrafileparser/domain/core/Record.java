
package sds.spectrafileparser.domain.core;

import java.util.List;

public class Record {
    private RecordType Type;
    private long Index;
    private String Data;
    private List<PropertyValue> Properties;
    private String Error;
    
    public Record(String data, long index, RecordType type, List<PropertyValue> properties)
    {
        Data = data;
        Index = index;
        Type = type;
        Properties = properties;
    }
    
    //alternative constructor for failed record
    public Record(long index, RecordType type, String error)
    {
        Index = index;
        Type = type;
        Error = error;
    }

    public RecordType getType() {
        return Type;
    }

    public long getIndex() {
        return Index;
    }

    public String getData() {
        return Data;
    }

    public List<PropertyValue> getProperties() {
        return Properties;
    }

    public String getError() {
        return Error;
    }
}
