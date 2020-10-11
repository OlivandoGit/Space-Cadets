import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;


public class EmailToName {
    public static String baseURL ="https://www.ecs.soton.ac.uk/people/";
    
    public static void main(String[] args) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter email id: ");

            URL url = new URL(baseURL + input.readLine());

            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            
            String element;
            String name = new String();

            while ((element = reader.readLine()) != null){
                if (element.contains("property=\"name\"")){
                   int startindex = element.indexOf("property=\"name\"");
                   int endindex = element.indexOf("<", startindex);

                   name = element.substring(startindex + 16, endindex);
                   break;
                }
            }

            if (!name.isEmpty()){
                System.out.println(name);
            } 
            else {
                System.out.println("User not found");
            }
            
        } catch (Exception e) {
            System.out.println(e);
        }
        
    }
}