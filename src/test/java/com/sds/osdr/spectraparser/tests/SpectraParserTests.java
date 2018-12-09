package com.sds.osdr.spectraparser.tests;

import sds.spectrafileparser.domain.core.JcampReader;
import sds.spectrafileparser.domain.core.Record;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import org.junit.Test;
import org.springframework.util.Assert;

public class SpectraParserTests {

    @Test
    public void testMethodForJdx() throws FileNotFoundException {
        JcampReader parser = new JcampReader(new FileInputStream("2-Methyl-1-Propanol.jdx"));
        Iterator<Record> i = parser.iterator();
        int count = 0;
        while(i.hasNext()){
            Record record = i.next();
            Assert.isInstanceOf(Record.class, record);
            count++;
        }
        Assert.isTrue(count == 1);
    }
}
