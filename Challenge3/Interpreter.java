import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.Math;
   
public class Interpreter {
    private List<String> Sourcecode = new ArrayList<String>();

    private HashMap<String, Integer> Variables = new HashMap<String, Integer>();
    private HashMap<String, Integer> Subroutines = new HashMap<String, Integer>();

    public Interpreter(List<String> code){
        this.Sourcecode = code;
        this.complete(this.Sourcecode);
    }

    //Parse the symbol and complete the maths:
    private Integer completeMaths(int num1, int num2, String symbol){
        int total = 0;
        if (symbol.equals("+")){
            total = num1 + num2;
        }
        else if (symbol.equals("-")){
            total = num1 - num2;
        }
        else if (symbol.equals("/")){
            total = Math.floorDiv(num1, num2);
        }
        else if (symbol.equals("*")){
            total = num1 * num2;
        }
        else if (symbol.equals("%")){
            total = num1 % num2;
        }
        else if (symbol.equals("**")){
            total = num1 ^ num2;
        }
        return total;
    }
    
    //Parse the symbol and complete the if statement
    private Boolean completeIf(int var1, int var2, String symbol){
        Boolean total = false;

        if (symbol.equals("<")){
            total = var1 < var2;
        }
        else if (symbol.equals(">")){
            total = var1 > var2;
        }
        else if (symbol.equals("<=")){
            total = var1 <= var2;
        }
        else if (symbol.equals(">=")){
            total = var1 >= var2;
        }
        else if (symbol.equals("==")){
            total = var1 == var2;
        }
        else if (symbol.equals("!=")){
            total = var1 != var2;
        }

        return total;
    }
        
    //Get keyword from the line of code:
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

    //Complete the interpreting of code:
    public void complete(List<String> code){
        for (int PC = 0; PC < code.size(); PC++){
            String line = code.get(PC);

            String key = getKey(line);

            if (line.indexOf(" ") != -1){
                String[] parts = line.split(" ");
                
                //Create Variables :
                if (key.equals("create")){
                    this.Variables.put(parts[1], 0);
                }

                //Update Variables:
                else if (key.equals("update-to-int")){
                    this.Variables.put(parts[1], Integer.parseInt(parts[2]));
                }
                else if (key.equals("update-to-var")){
                    this.Variables.put(parts[1], this.Variables.get(parts[2]));
                }
                else if (key.equals("update-maths-int-int")){
                    this.Variables.put(parts[1], completeMaths(Integer.parseInt(parts[2]), Integer.parseInt(parts[4]), parts[3]));
                }
                else if (key.equals("update-maths-var-int")){
                    this.Variables.put(parts[1], completeMaths(this.Variables.get(parts[2]), Integer.parseInt(parts[4]), parts[3]));
                }
                else if (key.equals("update-maths-int-var")){
                    this.Variables.put(parts[1], completeMaths(Integer.parseInt(parts[2]), this.Variables.get(parts[4]), parts[3]));
                }
                else if (key.equals("update-maths-var-var")){
                    this.Variables.put(parts[1], completeMaths(this.Variables.get(parts[2]), this.Variables.get(parts[4]), parts[3]));
                }

                //Complete if statements:
                else if (key.startsWith("if")){
                    int endif = 0;
                    
                    for (int i = PC; i < code.size(); i++) {
                        if (code.get(i).equals("endif")){
                            endif = i;
                            break;
                        } 
                    }

                    Boolean completed = false;

                    List<String> lines = new ArrayList<String>();
                    for (int i = PC; i < endif; i++){
                        if (completed){
                            break;
                        }

                        key = getKey(code.get(i));
                        if (key.equals("if-int-int")){
                            if(completeIf(Integer.parseInt(parts[1]), Integer.parseInt(parts[3]), parts[2])){
                                for (int j = i + 1; j < endif; j++){
                                    if (code.get(j).startsWith(" ")){
                                        lines.add(code.get(j).trim());
                                    } 
                                    else {
                                        break;
                                    }
                                }
                                completed = true;
                            }
                        }
                        else if (key.equals("if-int-var")){
                            if(completeIf(Integer.parseInt(parts[1]), this.Variables.get(parts[3]), parts[2])){
                                for (int j = i + 1; j < endif; j++){
                                    if (code.get(j).startsWith(" ")){
                                        lines.add(code.get(j).trim());
                                        
                                    } 
                                    else {
                                        break;
                                    }
                                }
                                completed = true;
                            }
                        }
                        else if (key.equals("if-var-int")){
                            if(completeIf(this.Variables.get(parts[1]), Integer.parseInt(parts[3]), parts[2])){
                                for (int j = i + 1; j < endif; j++){
                                    if (code.get(j).startsWith(" ")){
                                        lines.add(code.get(j).trim());
                                    } 
                                    else {
                                        break;
                                    }
                                }
                                completed = true;
                            }
                        }
                        else if (key.equals("if-var-var")){
                            if(completeIf(this.Variables.get(parts[1]), this.Variables.get(parts[3]), parts[2])){
                                for (int j = i + 1; j < endif; j++){
                                    if (code.get(j).startsWith(" ")){
                                        lines.add(code.get(j).trim());
                                    } 
                                    else {
                                        break;
                                    }
                                }
                                completed = true;
                            }
                        }
                        else if (key.equals("else:")){
                            for (int j = i + 1; j < endif; j++){
                                if (code.get(j).startsWith(" ")){
                                    lines.add(code.get(j).trim());
                                } 
                                else {
                                    break;
                                }
                            }
                            completed = true;
                        }
                    }
                    if (lines.size() > 0){
                        complete(lines);
                    }
                    PC = endif;
                }
                
                //Complete while statements:
                else if (key.startsWith("while")){
                    int endwhile = 0;
                    List<String> lines = new ArrayList<String>();
                    for (int i = PC + 1; i < code.size(); i++){
                        if (code.get(i).equals("endwhile")){
                            endwhile = i;
                            break;
                        }
                        else {
                            lines.add(code.get(i).substring(1));
                        }
                    }

                    if (parts[2].equals("<")){
                        if (key.equals("while-int-int")){
                            while (Integer.parseInt(parts[1]) < Integer.parseInt(parts[3])){
                                complete(lines);
                            }
                        }
                        else if (key.equals("while-int-var")){
                            while (Integer.parseInt(parts[1]) < this.Variables.get(parts[3])){
                                complete(lines);
                            }
                        }
                        else if (key.equals("while-var-int")){
                            while (this.Variables.get(parts[1]) < Integer.parseInt(parts[3])){
                                complete(lines);
                            }
                        }
                        else if (key.equals("while-var-var")){
                            while (this.Variables.get(parts[1]) < this.Variables.get(parts[3])){
                                complete(lines);
                            }
                        }
                    }
                    else if (parts[2].equals(">")){
                        if (key.equals("while-int-int")){
                            while (Integer.parseInt(parts[1]) > Integer.parseInt(parts[3])){
                                complete(lines);
                            }
                        }
                        else if (key.equals("while-int-var")){
                            while (Integer.parseInt(parts[1]) > this.Variables.get(parts[3])){
                                complete(lines);
                            }
                        }
                        else if (key.equals("while-var-int")){
                            while (this.Variables.get(parts[1]) > Integer.parseInt(parts[3])){
                                complete(lines);
                            }
                        }
                        else if (key.equals("while-var-var")){
                            while (this.Variables.get(parts[1]) > this.Variables.get(parts[3])){
                                complete(lines);
                            }
                        }
                    }
                    else if (parts[2].equals("<=")){
                        if (key.equals("while-int-int")){
                            while (Integer.parseInt(parts[1]) <= Integer.parseInt(parts[3])){
                                complete(lines);
                            }
                        }
                        else if (key.equals("while-int-var")){
                            while (Integer.parseInt(parts[1]) <= this.Variables.get(parts[3])){
                                complete(lines);
                            }
                        }
                        else if (key.equals("while-var-int")){
                            while (this.Variables.get(parts[1]) <= Integer.parseInt(parts[3])){
                                complete(lines);
                            }
                        }
                        else if (key.equals("while-var-var")){
                            while (this.Variables.get(parts[1]) <= this.Variables.get(parts[3])){
                                complete(lines);
                            }
                        }
                    }
                    else if (parts[2].equals(">=")){
                        if (key.equals("while-int-int")){
                            while (Integer.parseInt(parts[1]) >= Integer.parseInt(parts[3])){
                                complete(lines);
                            }
                        }
                        else if (key.equals("while-int-var")){
                            while (Integer.parseInt(parts[1]) >= this.Variables.get(parts[3])){
                                complete(lines);
                            }
                        }
                        else if (key.equals("while-var-int")){
                            while (this.Variables.get(parts[1]) >= Integer.parseInt(parts[3])){
                                complete(lines);
                            }
                        }
                        else if (key.equals("while-var-var")){
                            while (this.Variables.get(parts[1]) >= this.Variables.get(parts[3])){
                                complete(lines);
                            }
                        }
                    } 
                    else if (parts[2].equals("==")){
                        if (key.equals("while-int-int")){
                            while (Integer.parseInt(parts[1]) == Integer.parseInt(parts[3])){
                                complete(lines);
                            }
                        }
                        else if (key.equals("while-int-var")){
                            while (Integer.parseInt(parts[1]) == this.Variables.get(parts[3])){
                                complete(lines);
                            }
                        }
                        else if (key.equals("while-var-int")){
                            while (this.Variables.get(parts[1]) == Integer.parseInt(parts[3])){
                                complete(lines);
                            }
                        }
                        else if (key.equals("while-var-var")){
                            while (this.Variables.get(parts[1]) == this.Variables.get(parts[3])){
                                complete(lines);
                            }
                        }
                    }
                    else if (parts[2].equals("!=")){
                        if (key.equals("while-int-int")){
                            while (Integer.parseInt(parts[1]) != Integer.parseInt(parts[3])){
                                complete(lines);
                            }
                        }
                        else if (key.equals("while-int-var")){
                            while (Integer.parseInt(parts[1]) != this.Variables.get(parts[3])){
                                complete(lines);
                            }
                        }
                        else if (key.equals("while-var-int")){
                            while (this.Variables.get(parts[1]) != Integer.parseInt(parts[3])){
                                complete(lines);
                            }
                        }
                        else if (key.equals("while-var-var")){
                            while (this.Variables.get(parts[1]) != this.Variables.get(parts[3])){
                                complete(lines);
                            }
                        }
                    }

                    PC = endwhile;
                }
                
                //Complete definitions:
                else if (key.equals("define")){
                    this.Subroutines.put(line.substring(line.indexOf(" ") + 1, line.length() - 1), PC);
                    
                    for (int i = PC; i < code.size(); i++){
                        if (code.get(i).equals("enddefine")){
                            PC = i;
                            break;
                        }
                    }
                }
                
                //Compelte subroutines:
                else if (key.equals("complete")){
                    String subKey = line.substring(line.indexOf(" ") + 1);
                    
                    int temp = this.Subroutines.get(subKey);

                    List<String> lines = new ArrayList<String>();

                    for (int i = temp; i < code.size(); i++){
                        if (code.get(i).equals("enddefine")){
                            break;
                        }
                        else{
                            lines.add(code.get(i).substring(1));
                        }
                    }

                    complete(lines);

                }
            }  
        }
    }

    public HashMap<String, Integer> getVariables(){
        return this.Variables;
    }
}