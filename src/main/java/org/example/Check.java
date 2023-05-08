package org.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Check {
    private Integer columnNum;
    private Object checkedValue;
    private Character sign;

    public Integer getColumnNum() {
        return columnNum + 1;
    }

    public boolean checkIntColumn(String[] fields){
        if (sign == '<'){
                return Integer.parseInt(fields[columnNum]) < (Integer)checkedValue;
        } else if(sign == '>'){
                return Integer.parseInt(fields[columnNum]) > (Integer)checkedValue;
        } else if (sign == '='){
                return Integer.parseInt(fields[columnNum]) == (Integer)checkedValue;
        } else{
                return Integer.parseInt(fields[columnNum]) != (Integer)checkedValue;
        }
    }

    public boolean checkDoubleColumn(String[] fields){
        if ("\\N".equals(fields[columnNum]) && (sign == '>' || sign == '<')){
            return false;
        }
        if (sign == '<'){
            return Double.parseDouble(fields[columnNum]) < (Double)checkedValue;
        } else if(sign == '>'){
            return Double.parseDouble(fields[columnNum]) > (Double)checkedValue;
        } else if (sign == '='){
            if ("\\N".equals(checkedValue)){
                return fields[columnNum].equals(checkedValue);
            }
            return Double.parseDouble(fields[columnNum]) == (Double)checkedValue;
        } else{
            if ("\\N".equals(checkedValue)){
                return !fields[columnNum].equals(checkedValue);
            }
            return Double.parseDouble(fields[columnNum]) != (Double)checkedValue;
        }
    }

    public boolean checkStringColumn(String[] fields){
        if (sign == '=') {
            return fields[columnNum].equals(checkedValue.toString());
        }
        else {
            return !fields[columnNum].equals(checkedValue);
        }
    }
}
