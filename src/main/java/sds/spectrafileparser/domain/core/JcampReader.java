package sds.spectrafileparser.domain.core;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JcampReader implements Iterable<Record> {

    private InputStream stream;

    public JcampReader(InputStream stream) {
        this.stream = stream;
    }

    public Iterable<String> Extensions() {
        return Arrays.asList( ".JDX", ".DX" );
    }

    @Override
    public Iterator<Record> iterator() {
        try {
            return new JcampRecordsIterator(stream);
        } catch (Exception ex) {
            Logger.getLogger(JcampReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
