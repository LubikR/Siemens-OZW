package cz.rennerovi.kotel.model;

/**
 * Created by User on 6.12.2015.
 */
public class DataPointDescriptionModel {
    private String Type;
    private String Value;
    private String Unit;
    private String Name;
    private double Min;
    private double Max;
    private double Resolution;
    private int FieldWitdh;
    private int DecimalDigits;
    private boolean HasValid;
    private boolean IsValid;

    public String getName() { return Name; }
    public String getValue() {
        return Value;
    }
    public String getUnit() { return Unit; }
}
