import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class BareBonesInterpreter{
    public static void main(String[] args){
        List<String> lines = new ArrayList<String>();

        lines = getfile(lines);

        HashMap<String, Integer> vars = new HashMap<String, Integer>();

        vars = execute(lines, vars);

        System.out.println(vars);
    }

    public static List<String> getfile(List<String> lines){
        try {
            File sourcecode = new File("sourcecode.txt");
 
            Scanner reader = new Scanner(sourcecode);
            
            while (reader.hasNextLine()){
                String data = reader.nextLine();
                lines.add(data);
            }

            reader.close();
        } 
        catch (FileNotFoundException e){
            System.out.println(e);
        }

        return lines;
    }

    public static HashMap<String, Integer> execute(List<String> lines, HashMap<String, Integer> vars){
        int PC = 0;

        while (PC < lines.size()){
            String line = lines.get(PC);
            String[] parts = line.split(" ");

            if (parts[0].equals("clear")){
                String var = parts[1].replace(";", "");
                
                if (vars.containsKey(var)){
                    vars.remove(var);
                }
                vars.put(var, 0);
            }

            else if (parts[0].equals("incr")){
                String var = parts[1].replace(";", "");
                vars.put(var, vars.get(var) + 1);
            }

            else if (parts[0].equals("decr")){
                String var = parts[1].replace(";", "");
                vars.put(var, vars.get(var) - 1);
            }

            else if (parts[0].equals("while")){
                List<String> lines2 = new ArrayList<String>();

                int endwhile = PC; 

                for (int i = PC + 1; i < lines.size(); i ++){
                    String data = lines.get(i);

                    if (data.equals("end;")){
                        endwhile = i;
                        break;
                    } 
                    else{
                        lines2.add(data.trim());
                    }
                }

                while (vars.get(parts[1]) != Integer.parseInt(parts[3])){
                    vars = execute(lines2, vars);
                }

                PC = endwhile;
            }
            PC ++;
        }
        return vars;
    } 
}