import java.util.Scanner;

//This class represents the datafields of a UMLClass instance
//will consist of a name, visibility modifier, data type (custom or primitive)
//as well as methods to set or get these values
public class UMLDataField {
    //name of dataField
    private String name;

    //DataType can be either an enum representing a primitive type, or it can be set to
    //"OTHER", in which case the user will have to enter a string representing their custom type
    //"In the case of wrapper classes such as "DOUBLE, INT, etc.", visibility will be set to OTHER
    //and the custom name will hold the value "DOUBLE" etc.
    private DataType dataType;

    private String customNameType;

    private Visibility visibility;


    //will not take a customNameType parameter directly. will check to see if dataType == OTHER and
    //then prompt user for custom data type name
    public UMLDataField(String name, DataType dataType, Visibility visibility){
        Scanner input = new Scanner(System.in);

        this.name = name;
        this.dataType = dataType;
        this.visibility = visibility;

        if(this.dataType == DataType.OTHER){
            System.out.println("enter your custom data type name");

            //scan for customName since dataType == OTHER
            customNameType = input.next();

        }
    }

    public UMLDataField(){
        this.name = null;
        this.dataType = null;
        this.customNameType = null;
        this.visibility = null;
    }


    //setter to reset dataType. If new dataType is OTHER, then need to prompt user for a customTypeName and
    //set the customTypeName field. If previous dataType was OTHER, and is being changed to a primitive,
    //Then we need to set the customNameType to null
    public void setDataType(DataType dataType){
        Scanner input = new Scanner(System.in);
        if(this.dataType == DataType.OTHER && dataType != DataType.OTHER){
            this.customNameType = null;
        }

        this.dataType = dataType;
        if(this.dataType == DataType.OTHER){
            System.out.println("enter name of custom type");
            setCustomNameType(input.next());
        }


    }

    //setter to reset the visibility modifier
    public void setVisibility(Visibility visibility){
        this.visibility = visibility;
    }

    //setter to reset name of DataField Instance
    public void setName(String name){
        this.name = name;
    }

    //setter to reset the customNameType ONLY if dataType == OTHER, otherwises prints message
    //informing user that dataType has an invalid value and then returns.
    public void setCustomNameType(String customNameType){
        if(this.dataType == DataType.OTHER){
            this.customNameType = customNameType;
        }
        else{
            System.out.println("dataType must be set to OTHER in order to set a customNameType");
        }
    }

    //getter for the name of DataField instance
    public String getName(){
        return this.name;
    }

    //getter for the customNameType of DataField instance
    //should return null if dataType is not equal to OTHER
    public String getCustomNameType(){
        if(this.dataType == DataType.OTHER) {
            return this.customNameType;
        }
        else{
            return null;
        }
    }

    //getter method for visibility of DataField instance
    public Visibility getVisibility(){
        return this.visibility;
    }


    //getter method for dataType of DataField instance
    public DataType getDataType(){
        return this.dataType;
    }

    //toString() method for easy testing
    public String toString(){
        String data = "name: " + this.name + ", dataType: " + this.dataType +", " + "Visibility: " + this.visibility +
                      ", customeNameType: " + this.customNameType;
        return data;
    }


    public static void main(String[] args){

        UMLDataField dataField1 = new UMLDataField();
        String name = dataField1.getName();
        String customType = dataField1.getCustomNameType();
        Visibility vis = dataField1.getVisibility();
        DataType dataT = dataField1.getDataType();

        System.out.println(dataField1.toString());

        dataField1.setDataType(DataType.OTHER);

        System.out.println(dataField1.toString());

        dataField1.setName("dog");
        dataField1.setVisibility(Visibility.PRIVATE);
        System.out.println(dataField1.toString());


    }




}
