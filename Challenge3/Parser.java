import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Parser {

    private HashMap<String, String> Syntax = new HashMap<String, String>();
    private List<String> Sourcecode = new ArrayList<String>();

    public Parser(List<String> code){
        this.Sourcecode = code;
        this.getSyntax();
    }

    private void getSyntax(){
        try {
            File syntax = new File("Syntax.txt");

            Scanner reader = new Scanner(syntax);
            
            while (reader.hasNextLine()){
                String line = reader.nextLine();

                if (line.length() > 0){
                    String[] data = line.split(" : ");
                    
                    Syntax.put(data[0], data[1]);
                }
            }

            reader.close();
        } 
        catch (FileNotFoundException e){
            System.out.println(e);
        }
    }

    public List<Integer> checkSyntax(){
        List<Integer> errors = new ArrayList<Integer>();

        for (int i = 0; i < Sourcecode.size(); i++) {
            if (Sourcecode.get(i).length() > 0){

                String key;
                if (Sourcecode.get(i).trim().indexOf(" ") != -1){
                    key = Sourcecode.get(i).trim().substring(0, Sourcecode.get(i).trim().indexOf(" "));
                }
                else {
                    key = Sourcecode.get(i).trim().replace(";", "");
                }

                if (Syntax.containsKey(key)){
                    if (!Sourcecode.get(i).trim().matches(Syntax.get(key))){
                        errors.add(i + 1);
                    }
                }
                else {
                    errors.add(i + 1);
                }
            }
        }
        return errors;
    }
}