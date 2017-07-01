import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.InputStream;
import java.nio.file.Paths;

/**
 * ScriptExecute is in charge of executing a script file
 */
public class ScriptExecute {
    private DBConnection server;
    
    /***
     * Constructor.
     * @param server
     */
    public ScriptExecute(DBConnection server) {
        this.server = server;
    }

    public String execute(String path, QType.Type type) {
        StringBuilder sb = new StringBuilder();
        String results;

        // get path for script file
        Path scriptFile = Paths.get(path);

        try {
            InputStream in = Files.newInputStream(scriptFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String tmp = null;

            // run on all lines and execute each script
            while ((tmp = reader.readLine()) != null) {
                results = server.sendDataToRemoteServer(tmp, type);
                sb.append(results);
            }
        } catch (IOException err) {
            System.out.println(err.toString());
            return null;
        }

        return  sb.toString();
    }
    
    /***
     * The function runs a script of DML instructions.
     * @param filepath The file where the script is located.
     * @return A string representing the result of the DML instructions given.
     */
    public String DMLScript(String filepath) {
        StringBuilder sb = new StringBuilder();
        String response;
        
        Path file = Paths.get(filepath);
        try (InputStream in = Files.newInputStream(file);
            BufferedReader reader =
              new BufferedReader(new InputStreamReader(in))) {
            String line = null;
            
            //While I have not reached the end of the script...
            while ((line = reader.readLine()) != null) {
                response = server.sendDataToRemoteServer(line, QType.Type.DML);
                sb.append(response);
            }
        } catch (IOException x) {
            System.err.println(x);
        }
        
        return sb.toString();
    }
    
    /***
     * The function runs a script of DDL instructions.
     * @param filepath The file where the script is located.
     * @return A string representing the result of the DDL instructions given.
     */
    public String DDLScript(String filepath) {
        StringBuilder sb = new StringBuilder();
        String response;
        
        Path file = Paths.get(filepath);
        try (InputStream in = Files.newInputStream(file);
            BufferedReader reader =
              new BufferedReader(new InputStreamReader(in))) {
            String line = null;
            
            //While I have not reached the end of the script...
            while ((line = reader.readLine()) != null) {
                response = server.sendDataToRemoteServer(line, QType.Type.DDL);
                sb.append(response);
            }
        } catch (IOException x) {
            System.err.println(x);
        }
        
        return sb.toString();
    }
}
