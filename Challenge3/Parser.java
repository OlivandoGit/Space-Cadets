import java.util.ArrayList;
import java.util.Arrays;
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

    //Get the syntax from the Syntax.txt file:
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

    //Get the keyword from the line of code:
    private String getKey(String line){
        String key;
        if (line.indexOf(" ") != -1){
            key = line.substring(0, line.indexOf(" "));
        }
        else {
            key = line.replace(":", "");
        }
        return key;
    }

    //Check each line of the sourcecode for syntax errors as well as check all if, while, defines are closed:
    public List<Integer> checkSyntax(){
        List<Integer> errors = new ArrayList<Integer>();

        List<Integer> openIf = new ArrayList<Integer>();
        List<Integer> openWhile = new ArrayList<Integer>();
        List<Integer> openDefine = new ArrayList<Integer>();

        for (int i = 0; i < this.Sourcecode.size(); i++) {
            String line = this.Sourcecode.get(i).trim();
            if (line.length() > 0){

                String key = getKey(line).replace(":", "");


                //Checking if all if, while, defines are closed:
                if (key.equals("if")){
                    openIf.add(i + 1);
                } else if (key.equals("while")){
                    openWhile.add(i + 1);
                } else if (key.equals("define")){
                    openDefine.add(i + 1);
                }

                if (key.equals("endif")){
                    if (openIf.size() > 0){
                        openIf.remove(openIf.size() - 1);
                    }
                    else{
                        errors.add(i + 1);
                    }
                } else if (key.equals("endwhile")){
                    if (openWhile.size() > 0){
                        openWhile.remove(openWhile.size() - 1);
                    }
                    else {
                        errors.add(i + 1);
                    }
                } else if (key.equals("enddefine")){
                    if (openDefine.size() > 0){
                        openDefine.remove(openDefine.size() - 1);
                    }
                    else{
                        errors.add(i + 1);
                    }
                }

                //Checking eachline against regular expression syntax:
                if (this.Syntax.containsKey(key)){
                    if (!line.matches(this.Syntax.get(key))){
                        errors.add(i + 1);
                        
                    }
                }
                else {
                    errors.add(i + 1);
                }
            }
        }

        //-1 used to change error type:
        errors.add(-1);

        //Adding errors for unclosed if, while, defines:
        if (openIf.size() > 0){
            for (Integer line : openIf) {
                errors.add(line);
            }
        } 
        else if (openWhile.size() > 0){
            for (Integer line : openWhile) {
                errors.add(line);
            }
        } 
        else if (openDefine.size() > 0){
            for (Integer line : openDefine) {
                errors.add(line);
            }
        }
        return errors;
    }

    //Checking all variables and subrotuines are initialised before use:
    public List<Integer> checkVariables(){
        List<Integer> inuse = new ArrayList<Integer>();
        List<Integer> notdefined = new ArrayList<Integer>();
        List<Integer> invalidname = new ArrayList<Integer>();

        List<String> variables = new ArrayList<String>();
        HashMap<String, Integer> subroutines = new HashMap<String, Integer>();

        for (int i = 0; i < this.Sourcecode.size(); i++) {
            String line = this.Sourcecode.get(i).trim();
            String key = getKey(line);

            //adding all variable names to the list and checking for variable in use errors:
            if (key.equals("create")){
                String var = line.substring(line.indexOf(" ") + 1, line.length());
                if (variables.indexOf(var) == -1){
                    variables.add(var);
                }
                else {
                    inuse.add(i + 1);
                }

                //Check if variable name is valid:
                if (var.matches(this.Syntax.get("integer"))){
                    invalidname.add(i + 1);
                }
            //Checking if statements for variables defined:
            } else if (key.equals("if") | key.equals("elif")){
                String[] parts = line.split(" ");
                
                if (parts[1].matches(this.Syntax.get("string"))){
                    if (!variables.contains(parts[1])){
                        notdefined.add(i + 1);
                    }
                } 
                if (parts[3].replace(":", "").matches(this.Syntax.get("string"))){
                    if (!variables.contains(parts[3].replace(":", ""))){
                        notdefined.add(i + 1);
                    }
                }
            //Checking whiles for variables defined:
            } else if (key.equals("while")){
                String[] parts = line.split(" ");

                if (parts.length > 0){
                    if (parts[1].matches(this.Syntax.get("string"))){
                        if (!variables.contains(parts[1])){
                            notdefined.add(i + 1);
                        }
                    }
                    if (parts[3].replace(":", "").matches(this.Syntax.get("string"))){
                        if (!variables.contains(parts[3].replace(":", ""))){
                            notdefined.add(i + 1);
                        }
                    }
                }

            //Adding the subroutine name as well as how many variables it has to the hashmap and checking for subrotuine in use errors:
            } else if (key.equals("define")){
                String subroutine = line.substring(line.indexOf(" ") + 1, line.indexOf("("));
                
                if (!subroutines.containsKey(subroutine)){
                    //Checking if the subroutine has variables or not
                    if (line.indexOf("(") - line.indexOf(")") != -1){
                        String vars = line.substring(line.indexOf("(") + 1, line.indexOf(")"));

                        //Checking if the subroutine has more than one variable:
                        if (vars.contains(",")){
                            String[] vars2 = vars.split(",");
                            subroutines.put(subroutine, vars2.length);
                        }
                        else {
                            subroutines.put(subroutine, 1);
                        }
                    }
                    else{
                        subroutines.put(subroutine, 0);
                    }
                } 
                else{
                    inuse.add(i + 1);
                }

                //Check for invalid subroutine name:
                if (subroutine.matches(this.Syntax.get("integer"))){
                    invalidname.add(i + 1);
                }

            //Checking complete line for errors:
            } else if (key.equals("complete")){
                String subroutine = line.substring(line.indexOf(" ") + 1, line.indexOf("("));
                
                //checking if the subroutine exists:
                if (subroutines.containsKey(subroutine)){
                    //Checking if all variables are present:

                    //Checking if there are any variables:
                    if (line.indexOf("(") - line.indexOf(")") != -1){
                        String vars = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
                        //Checking if there are multiple variables:
                        
                        if (vars.contains(",")){
                            String[] vars2 = vars.split(",");
                            if (vars2.length != subroutines.get(subroutine)){
                                notdefined.add(i + 1);
                            }
                        } else{
                            if (subroutines.get(subroutine) != 1){
                                notdefined.add(i + 1);
                            }
                        }
                    }
                    else {
                        if (subroutines.get(subroutine) != 0){
                            notdefined.add(i + 1);
                        }
                    }
                }
                else{
                    notdefined.add(i + 1);
                }
            }
        }

        //Compile error lists into singular list for return:
        List<Integer> errors = new ArrayList<Integer>();
        
        for (Integer line : inuse) {
            errors.add(line);
        }

        errors.add(-1);

        for (Integer line : notdefined) {
            errors.add(line);
        }

        errors.add(-2);

        for (Integer line : invalidname) {
            errors.add(line);
        }

        return errors;
    }
    
    //Parse the code for the interpreter:
    public List<String> ParseSourcecode(){
        List<String> parsed = new ArrayList<String>();

        for (String line : this.Sourcecode) {
            
            //Check to see if the line begins with whitespace:
            String trim = "";
            if (line.startsWith(" ")){
                trim = " ";
                line = line.trim();
            }

            //Get the keyword from the line:
            String key = getKey(line);

            //List of commands that dont need to change during parseing:
            String[] nochange = {"create", "endelif", "else", "endif", "endwhile", "break", "continue", "define", "enddefine", "complete"};
            
            //Dealing with lines that don't need to change:
            if (Arrays.asList(nochange).contains(key)){
                parsed.add(trim + line);
            }

            //Parsing the rest of the lines:
            else if (key.equals("update")){
                String[] parts = line.split(" ");
                if (parts.length == 4 && parts[3].matches(this.Syntax.get("integer"))){
                    parsed.add(trim + "update-to-int " + parts[1] + " " + parts[3]);
                }
                else if (parts.length == 4){
                    parsed.add(trim + "update-to-var " + parts[1] + " " + parts[3]);
                }
                else if (parts[3].matches(this.Syntax.get("integer")) && parts[5].matches(this.Syntax.get("integer"))){
                    parsed.add(trim + "update-maths-int-int " + parts[1] + " " + parts[3] + " " + parts[4] + " " + parts[5]);
                }
                else if (parts[3].matches(this.Syntax.get("string")) && parts[5].matches(this.Syntax.get("integer"))){
                    parsed.add(trim + "update-maths-var-int " + parts[1] + " " + parts[3] + " " + parts[4] + " " + parts[5]);
                }
                else if (parts[3].matches(this.Syntax.get("integer")) && parts[5].matches(this.Syntax.get("string"))){
                    parsed.add(trim + "update-maths-int-var " + parts[1] + " " + parts[3] + " " + parts[4] + " " + parts[5]);
                }
                else if (parts[3].matches(this.Syntax.get("string")) && parts[5].matches(this.Syntax.get("string"))){
                    parsed.add(trim + "update-maths-var-var " + parts[1] + " " + parts[3] + " " + parts[4] + " " + parts[5]);
                }                
            }
            else if (key.equals("if") || key.equals("elif")){
                String[] parts = line.replace(":", "").split(" ");

                if (parts[1].matches(this.Syntax.get("integer")) && parts[3].matches(this.Syntax.get("integer"))){
                    parsed.add(trim + "if-int-int " + parts[1] + " " + parts[2] + " " + parts[3]);
                }

                else if (parts[1].matches(this.Syntax.get("integer")) && parts[3].matches(this.Syntax.get("string"))){
                    parsed.add(trim + "if-int-var " + parts[1] + " " + parts[2] + " " + parts[3]);
                }

                else if (parts[1].matches(this.Syntax.get("string")) && parts[3].matches(this.Syntax.get("integer"))){
                    parsed.add(trim + "if-var-int " + parts[1] + " " + parts[2] + " " + parts[3]);
                }

                else if (parts[1].matches(this.Syntax.get("string")) && parts[3].matches(this.Syntax.get("string"))){
                    parsed.add(trim + "if-var-var " + parts[1] + " " + parts[2] + " " + parts[3]);
                }
            }
            else if (key.equals("while")){
                String[] parts = line.replace(":", "").split(" ");

                if (parts[1].matches(this.Syntax.get("integer")) && parts[3].matches(this.Syntax.get("integer"))){
                    parsed.add(trim + "while-int-int " + parts[1] + " " + parts[2] + " " + parts[3]);
                } 
                else if (parts[1].matches(this.Syntax.get("integer")) && parts[3].matches(this.Syntax.get("string"))){
                    parsed.add(trim + "while-int-var " + parts[1] + " " + parts[2] + " " + parts[3]);
                }
                else if (parts[1].matches(this.Syntax.get("string")) && parts[3].matches(this.Syntax.get("integer"))){
                    parsed.add(trim + "while-var-int " + parts[1] + " " + parts[2] + " " + parts[3]);
                }
                else if (parts[1].matches(this.Syntax.get("string")) && parts[3].matches(this.Syntax.get("string"))){
                    parsed.add(trim + "while-var-var " + parts[1] + " " + parts[2] + " " + parts[3]);
                }
            }
        }
        return parsed;
    }
}