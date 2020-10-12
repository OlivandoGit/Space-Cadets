import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class BareBonesInterpreter {
    public static void main(String[] args) {
        HashMap<Integer, String> lines = new HashMap<Integer, String>();

        lines = getfile(lines);

        HashMap<String, Integer> vars = new HashMap<String, Integer>();

        vars = execute(lines, vars);

        System.out.println(vars);

    }

    public static HashMap<Integer, String> getfile(HashMap<Integer, String> lines){
        try {
            int PC = 0;
            File sourcecode = new File("Sourcecode.txt");

            Scanner reader = new Scanner(sourcecode);
            
            while (reader.hasNextLine()){
                String data = reader.nextLine();
                lines.put(PC, data);

                PC ++;
            }

            reader.close();

        } catch (FileNotFoundException e){
            System.out.println(e);
        }

        return lines;
    }

    public static HashMap<String, Integer> execute(HashMap<Integer, String> lines, HashMap<String, Integer> vars){
        int PC = 0;

        while (PC < lines.size()){
            String line = lines.get(PC);
            String[] parts = line.split(" ");
            
            if (parts.length == 2){
                String var = parts[1].replace(";", "");
                
                if (parts[0].equals("clear")){
                    if (vars.containsKey(var)){
                        vars.remove(var);
                    }
                    vars.put(var, 0);
                } 
                else{
                    int current = vars.get(var);
                    vars.remove(var);

                    if (parts[0].equals("incr")){
                        vars.put(var, current + 1);
                    }
                    else if (parts[0].equals("decr")){
                        vars.put(var, current - 1);
                    }
                }

            } else if (parts.length == 5){
                if (parts[0].equals("while")){
                    HashMap<Integer, String> lines2 = new HashMap<Integer, String>();

                    int newline = 0;
                    int endwhile = PC; 

                    for (int i = PC + 1; i < lines.size(); i ++){
                        String data = lines.get(i);

                        if (data.equals("end;")){
                            endwhile = i;
                            break;
                        } 
                        else {
                            lines2.put(newline, data.trim());
                        }
                        newline ++;
                    }

                    while (vars.get(parts[1]) != Integer.parseInt(parts[3])){
                        vars = execute(lines2, vars);
                    }
                    PC = endwhile;
                }
            }
            PC ++;
        }
        return vars;
    } 
}
