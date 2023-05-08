package org.example;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.*;

@Getter
@Setter
public class Parser {
    private Map<Character, List<Airport>> alphabet;
    private final String fileName = "/airports.csv";

    public Parser(){
        alphabet = new HashMap<>();
    }

    public void readCSV() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(this.getClass().getResourceAsStream(fileName))));
        String line;
        int i = 0;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            String[] fields = line.split(",");
            if (alphabet.containsKey(fields[1].charAt(1))){
                alphabet.get(fields[1].charAt(1))
                        .add(new Airport(i++, fields[1]));
            } else {
                List<Airport> list = new ArrayList<>();
                list.add(new Airport(i++, fields[1]));
                alphabet.put(fields[1].charAt(1), list);
            }
        }
        br.close();

        for(Map.Entry<Character, List<Airport>> airport : alphabet.entrySet()){
            airport.getValue().sort(Comparator.comparing(Airport::getName));
        }
    }

    public List<Integer> search(String line){
        if (line.charAt(0) < 'A' || line.charAt(0) > 'Z'){
            System.out.println("Error in search");
        }
        char c = line.charAt(0);
        List<Airport> airports = alphabet.get(c);

        if (airports == null)
            return null;

        String line1 = "\"" + line;
        int index = binarySearch(airports, line1);
        if (index == -1){
            System.out.println("Error in binarySearch");
            return null;
        }

        List<Integer> ids = new ArrayList<>();
        for (int i = index; i >= 0 ; i--) {
            if (airports.get(i).getName().startsWith(line1)){
                ids.add(airports.get(i).getNum());
            }
        }
        for (int i = index + 1; i < airports.size(); i++) {
            if (airports.get(i).getName().startsWith(line1)){
                ids.add(airports.get(i).getNum());
            }
        }
        Collections.sort(ids);

        return ids;
    }

    private static int binarySearch(List<Airport> a, String x) {
        int low = 0;
        int high = a.size() - 1;
        int mid;

        while (low <= high) {
            mid = (low + high) / 2;

            if (a.get(mid).getName().startsWith(x)){
                return mid;
            } else if (a.get(mid).getName().compareTo(x) < 0) {
                low = mid + 1;
            } else if (a.get(mid).getName().compareTo(x) > 0) {
                high = mid - 1;
            }
        }

        return -1;
    }

    public List<String[]> readCSVTwo(List<Check> checks, StringBuilder str, List<Integer> ids) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(this.getClass().getResourceAsStream(fileName))));
        String line;
        List<String[]> airports = new ArrayList<>();

        int i = 0;
        int id = -1;
        if (!ids.isEmpty()) {
            id = skipper(ids, br, i++, id);
            if (id != 0) {
                id++;
            }
        }else {
            return null;
        }
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            String[] fields = line.split(",");
            List<Character> trueFalse = new ArrayList<>();
            for(Check e : checks){
                if (e.getColumnNum() == 9 || e.getColumnNum() == 1){
                    if (e.checkIntColumn(fields)) {
                        trueFalse.add('T');
                    }else{
                        trueFalse.add('F');
                    }
                }else if(e.getColumnNum() == 7 || e.getColumnNum() == 10 || e.getColumnNum() == 8){
                    if (e.checkDoubleColumn(fields)){
                        trueFalse.add('T');
                    }else{
                        trueFalse.add('F');
                    }
                } else{
                    if (e.checkStringColumn(fields)){
                        trueFalse.add('T');
                    }else{
                        trueFalse.add('F');
                    }
                }
            }
            if ('T' == startToEnd(addToLine(str, trueFalse))){
                airports.add(fields);
            }
            if (ids.size()>i){
                id = skipper(ids, br, i++, id);
                id++;
            } else
                break;
        }
        br.close();

        airports.sort((s1, s2) -> s1[1].compareTo(s2[1]));
        return airports;
    }

    private int skipper(List<Integer> ids, BufferedReader br, int i, int id) throws IOException {
        while (id < (ids.get(i) - 1)){
            ++id;
            br.readLine();
        }
        return id;
    }

    public StringBuilder addToLine(StringBuilder line, List<Character> trueFalse) {
        StringBuilder str = new StringBuilder();
        int i = 0;
        int j = 0;
        char sign;
        while (line.length() > i) {
            if (i == 0) {
                if (line.charAt(i) != '(') {
                    str.append(trueFalse.get(j++));
                    str.append(line.charAt(i));
                } else {
                    str.append(line.charAt(i));
                }
                if (i + 1 == line.length() && (line.charAt(i) == '|' || line.charAt(i) == '&')) {
                    str.append(trueFalse.get(j++));
                }
                ++i;
                continue;
            }


            sign = line.charAt(i);
            char before = line.charAt(i - 1);
            if ((before == '(' && (sign == '|' || sign == '&'))
                    || ((before == '|' || before == '&') && sign == ')')
                    || ((before == '|' || before == '&') && (sign == '|' || sign == '&'))
                    || (before=='(' && sign == ')')){
                str.append(trueFalse.get(j++));
                str.append(line.charAt(i));
            } else if ((before == ')' && (sign == '|' || sign == '&'))
                    || ((before == '|' || before == '&') && sign == '(')
                    || (before == '(' && sign == '(')
                    || (before == ')' && sign == ')')){
                str.append(line.charAt(i));
            }
            if (i+1 == line.length() && (line.charAt(i) == '|' || line.charAt(i) == '&')) {
                str.append(trueFalse.get(j++));
            }
            ++i;
        }
        return str;
    }

    public char startToEnd(StringBuilder line){
        int i = 0;
        int start = -1;
        Deque<Integer> deque = new LinkedList<>();
        while (line.length() > i){
            if (line.charAt(i) == '('){
                deque.add(i + 1);
            } else if(line.charAt(i) == ')'){
                start = deque.removeLast();
                line.replace(start - 1, i + 1, String.valueOf(checker(line.substring(start, i))));
                i = start - 1;
            }
            ++i;
        }

        return checker(line.toString());
    }

    private char checker(String line) {
        int i = 0;
        int arIndex = 0;
        List<List<Character>> tflist = new ArrayList<>();
        tflist.add(new ArrayList<>());
        while (line.length() > i){
            if (line.charAt(i) == '|'){
                tflist.add(new ArrayList<>());
                ++arIndex;
            } else if (line.charAt(i) != '&'){
                tflist.get(arIndex).add(line.charAt(i));
            }
            ++i;
        }

        List<Character> resList = new ArrayList<>();
        for(List<Character> list : tflist){
            boolean flag = true;
            for (char c : list){
                if (c == 'F'){
                    flag = false;
                    resList.add('F');
                    break;
                }
            }
            if (flag)
                resList.add('T');
        }
        if (resList.size() == 1){
            return resList.get(0);
        }
        for (char c : resList){
            if (c == 'T'){
                return 'T';
            }
        }
        return 'F';
    }

    public StringBuilder parseToCondition(String in, int i, List<Check> checks){
        StringBuilder line = new StringBuilder();
        int staples = 0;
        while (in.length() > i){
            if (in.charAt(i) == '&'){
                line.append(in.charAt(i++));
            }else if (in.length() > (i + 1) && in.charAt(i) == '|' && in.charAt(i + 1) == '|'){
                line.append(in.charAt(i));
                i+=2;
            }else if ((in.length() > (i + 1) && in.charAt(i) == '|' && in.charAt(i + 1) != '|')){
                System.out.println("Error in '|'. Position: " + i);
                return null;
            }
            if (in.charAt(i) == '('){
                line.append(in.charAt(i++));
                ++staples;
            }
            if ((in.length() <= (i + 7)) || !"column[".equals(in.substring(i, i + 7))){
                System.out.println("Error in column[");
                return null;
            }

            i += 7;
            int colNum = 0;
            char sign;


            for (; in.charAt(i) != ']'; i++) {
                if (in.charAt(i) >= 48 && in.charAt(i) <= 57){
                    colNum = colNum * 10 + in.charAt(i) - 48;
                } else{
                    System.out.println("Error in column number. Position: " + i);
                    return null;
                }
            }

            if(colNum == 0 || in.charAt(i++) != ']' || colNum > 14){
                System.out.println("Error in column number or ']'. Position: " + i);
                return null;
            }

            //определяем знак
            if (in.length() <= i){
                System.out.println("Error in length");
                return null;
            }
            if((colNum > 1 && colNum < 7) || colNum > 10) {
                if (in.charAt(i) == '='){
                    sign = '=';
                    ++i;
                } else if(in.charAt(i) == '<' && in.charAt(i + 1) == '>'){
                    sign = ' ';
                    i += 2;
                }else {
                    System.out.println("Error in string sign. Position: " + i);
                    return null;
                }
            }
            else {
                if (in.charAt(i) == '=' || in.charAt(i) == '>') {
                    sign = in.charAt(i++);
                } else if (in.charAt(i) == '<') {
                    sign = '<';
                    ++i;
                    if (in.charAt(i) == '>') {
                        sign = ' ';
                        ++i;
                    }
                } else {
                    System.out.println("Error in number sign. Position: " + i);
                    return null;
                }
            }

            //checks
            if (in.length() <= i){
                System.out.println("Error in length");
                return null;
            }

            //string check
            if((colNum > 1 && colNum < 7) || colNum > 10){
                StringBuilder word = new StringBuilder();
                if (in.charAt(i) != 34){
                    System.out.println("Error in '\"'. Position: " + i);
                    return null;
                }
                word.append(in.charAt(i++));
                for (; i < in.length() && in.charAt(i) != '&' && in.charAt(i) != '|' && in.charAt(i) != ')'; i++) {
                    if ((in.charAt(i) >= 65 && in.charAt(i) <= 90) || (in.charAt(i) >= 97 && in.charAt(i) <= 122) || in.charAt(i) == 34){
                        word.append(in.charAt(i));
                    }else {
                        System.out.println("Error in string check. Position: " + i);
                        return null;
                    }
                }
                if (word.length() < 2 || word.charAt(word.length() - 1) != '\"'){
                    System.out.println("Error in '\"'. Position: " + (i -1));
                    return null;
                }
                checks.add(new Check(colNum - 1, word, sign));
            }

            //int check
            else if (colNum == 9 || colNum == 1){
                int secondNum = 0;
                for (; i < in.length() && in.charAt(i) != '&' && in.charAt(i) != '|' && in.charAt(i) != ')'; i++) {
                    if (in.charAt(i) >= 48 && in.charAt(i) <= 57){
                        secondNum = secondNum * 10 + in.charAt(i) - 48;
                    }else {
                        System.out.println("Error in int check. Position: " + i);
                        return null;
                    }
                }
                checks.add(new Check(colNum - 1, secondNum, sign));
            }

            //double check
            else if (colNum == 7 || colNum == 8 || colNum == 10){
                StringBuilder number = new StringBuilder();
                double secondNum;

                if ("\\N".equals(in.substring(i, i + 2))){
                    if (sign != '='){
                        System.out.println("Error in \\N. Position: " + i);
                        return null;
                    }
                    checks.add(new Check(colNum - 1, in.substring(i, i + 2), sign));
                    i += 2;
                } else {
                    for (; i < in.length() && (in.charAt(i) != '&' && in.charAt(i) != '|' && in.charAt(i) != ')'); i++) {
                        if ((in.charAt(i) >= 48 && in.charAt(i) <= 57) || in.charAt(i) == '.' || in.charAt(i) == '-') {
                            number.append(in.charAt(i));
                        } else {
                            System.out.println("Error in double check. Position: " + i);
                            return null;
                        }
                    }
                    secondNum = Double.parseDouble(String.valueOf(number));
                    checks.add(new Check(colNum - 1, secondNum, sign));
                }
            }
            if (in.length() > i && in.charAt(i) == ')'){
                --staples;
                line.append(in.charAt(i++));
            }
        }
        if (staples != 0){
            System.out.println("Error in '(' or ')'. Position: " + i);
            return null;
        }
        return line;
    }
}
