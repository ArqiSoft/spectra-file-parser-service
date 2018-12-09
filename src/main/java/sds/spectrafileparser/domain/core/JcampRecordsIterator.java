
package sds.spectrafileparser.domain.core;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import jspecview.common.Spectrum;
import jspecview.source.JDXReader;
import jspecview.source.JDXSource;

public class JcampRecordsIterator implements Iterator<Record> {
    
    private int ordinal = -1;
    private int index = 0;
    private Record current = null;
    
    public String Title = "Title";
    public String DataType = "DataType";
    public String DataClass = "DataClass";
    public String Mol = "Mol";
    public String Dx = "Dx";
    public String Origin = "Origin";
    public String Owner = "Owner";
    public String Date = "Date";
    public String Time = "Time";
    private JDXSource source;
    private ArrayList<String> jcamps = new ArrayList<>(); 
    private Iterator<String> jcampsIterator; 

    public JcampRecordsIterator(InputStream s) throws Exception
    {
        String jcampsString = "";
        try (Scanner input = new Scanner(s)) {
            String lines = "";
            while (input.hasNextLine()) {
                lines += input.nextLine() + '\n';
            }
            jcampsString = lines;
        }
        jcamps.add(jcampsString);
        jcampsIterator = jcamps.iterator();

//        jcampsIterator = Arrays.stream(jcampsString.split("##END=")).iterator();
//        source = JDXReader.createJDXSourceFromStream(new ByteArrayInputStream(jcampsString.getBytes(StandardCharsets.UTF_8)), false, false, 0);
//        index = source.getNumberOfSpectra();
    }

    @Override
    public boolean hasNext() {
        index++;
        return jcampsIterator.hasNext();
    }

    @Override
    public Record next()
    {
        if (current == null) 
        {
            try {
                String data =  jcampsIterator.next();
                JSVSpectrum spectrum = JSpecViewReader.Read(data);
                ArrayList<PropertyValue> properties = new ArrayList<>();
                properties.add(new PropertyValue(Title, spectrum.Title));
                properties.add(new PropertyValue(DataType, spectrum.DataType));
                properties.add(new PropertyValue(DataClass, spectrum.DataClass));
                properties.add(new PropertyValue(Dx, spectrum.Dx));
                properties.add(new PropertyValue(Date, spectrum.Date));
                properties.add(new PropertyValue(Origin, spectrum.Origin));
                properties.add(new PropertyValue(Owner,spectrum.Owner));
                properties.add(new PropertyValue(Time, spectrum.Time));

                return new Record(data, ordinal, RecordType.Spectrum, properties);                
            } catch (Exception ex) {
                Logger.getLogger(JcampRecordsIterator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
}
