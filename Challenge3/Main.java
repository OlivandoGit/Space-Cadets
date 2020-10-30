import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    private static String Filename = "Sourcecode.txt";
    private static List<String> Sourcecode = new ArrayList<String>();


    public static void main(String[] args) {
        getSourcecode();
        Parser parse = new Parser(Sourcecode);
        List<Integer> errors = parse.checkSyntax();

        String error = "Syntax Error in line: ";
        for (Integer line : errors) {
            if (line == -1){
                error = "Expression not closed error for line: ";
            }
            else{
                System.out.println(error +Integer.toString(line));
            }
        }

        System.out.println("");

        errors = parse.checkVariables();

        error = "Name already in use error in line: ";
        for (Integer line : errors) {
            if (line == -1){
                error = "Not defined error in line: ";
            }
            else if (line == -2){
                error = "Invalid name error in line :";
            }
            else{
                System.out.println(error +Integer.toString(line));
            }
        }

        List<String> parsedCode = parse.ParseSourcecode();

        Interpreter inter = new Interpreter(parsedCode);

        HashMap<String, Integer> variables = inter.getVariables();

        System.out.println(variables);
    }

    //Get the sourcecode from the sourcecode.txt file
    public static void getSourcecode() {
        try {
            File code = new File(Filename);

            Scanner reader = new Scanner(code);
            
            while (reader.hasNextLine()){
                String line = reader.nextLine();
                Sourcecode.add(line);
            }  

            reader.close();
        } 
        catch (FileNotFoundException e){
            System.out.println(e);
        }
    }
}
