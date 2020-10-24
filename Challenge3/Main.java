import java.util.List;
import java.util.ArrayList;
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

        for (Integer line : errors) {
            throw new IllegalArgumentException("Syntax Error in line " +Integer.toString(line));
        }

        System.out.println(errors);
    }

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
