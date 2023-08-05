package uk.ac.ucl.shell.util.property;

import java.util.ArrayList;

public class StrArr {
    private ArrayList<String> strArr;

    public StrArr(){
        strArr = new ArrayList<>();
    }

    public ArrayList<String> getStrArr() {
        return strArr;
    }

    public void addStr(String strIn){
        strArr.add(strIn);
    }

}