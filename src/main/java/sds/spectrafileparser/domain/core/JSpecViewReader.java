/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sds.spectrafileparser.domain.core;

import jspecview.common.Spectrum;
import jspecview.source.JDXReader;
import jspecview.source.JDXSource;

/**
 *
 * @author Александр
 */
public class JSpecViewReader {
    
    public static JSVSpectrum Read(String jcamp) throws Exception
    {
	java.io.InputStream stream = new java.io.StringBufferInputStream(jcamp);

	JDXSource source = JDXReader.createJDXSourceFromStream(stream, false, false, 0);

        int numOfSpec = source.getNumberOfSpectra();

        if (source.getNumberOfSpectra() == 0)
            throw new JcampHasNoSpectrumException();

        if (source.getNumberOfSpectra() > 1)
            throw new JcampHasTooManySpectraException("Only the case when JCAMP file has one spectrum currently supported");

        Spectrum spec = source.getJDXSpectrum(0);

        return new JSVSpectrum(spec.getTitle(), jcamp, "", spec.getJcampdx(), spec.getDataType(), 
                spec.getDataClass(), spec.getOrigin(), spec.getOwner(), spec.getDate(), spec.getTime(), spec.getFilePath());
                
    }
    
}
