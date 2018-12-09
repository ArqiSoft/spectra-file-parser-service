/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sds.spectrafileparser.domain.core;

/**
 *
 * @author Александр
 */
public class JSVSpectrum {
    
    public String Title;
    public String Jcamp;
    public String Mol;
    public String Dx;
    public String DataType;
    public String DataClass;
    public String Origin;
    public String Owner;
    public String Date;
    public String Time;
    public String FileName;

    public JSVSpectrum(String Title, String Jcamp, String Mol, String Dx, String DataType, String DataClass, String Origin, String Owner, String Date, String Time, String FileName) {
        this.Title = Title;
        this.Jcamp = Jcamp;
        this.Mol = Mol;
        this.Dx = Dx;
        this.DataType = DataType;
        this.DataClass = DataClass;
        this.Origin = Origin;
        this.Owner = Owner;
        this.Date = Date;
        this.Time = Time;
        this.FileName = FileName;
    }
    
    
}
