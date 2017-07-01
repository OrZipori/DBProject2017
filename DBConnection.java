import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * DBConnection class serves as the middle man between the remote server
 * and the GUI client.
 */
public class DBConnection {
    static final String L_ERROR = "LOGICAL ERROR";
    static final String S_ERROR = "BAD QUERY STRUCTURE";
    private Connection conn;
    private List<String> noOutputDML;
    private List<String> outputDDL;
    
    /**
     * Constructor. Initializes filter lists for DML and DDL methods.
     * The lists are used to distinguish between executeUpdate and executeQuery methods.
     */
    public DBConnection(){
        // connect to server
        this.connect();

        // create the list of commands that don't return output to show
        this.noOutputDML = new ArrayList();
        // create the list of commands that does return output to show
        this.outputDDL = new ArrayList();

        this.noOutputDML.add("INSERT");
        this.noOutputDML.add("UPDATE");
        this.noOutputDML.add("DELETE");

        this.outputDDL.add("SHOW");
        this.outputDDL.add("DESCRIBE");
    }
    
    /**
     * Connects to remote SQL server.
     * Reads login credentials from conf.txt file.
     */
    private void connect() {
        this.conn = null;
        // get the driver for connection
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("No jdbc driver");
            return;
        }

        // parse the conf.txt and connect to the server
        try {
            String[] arr = new String[3];
            arr[0] = null;
            arr[1] = null;
            arr[2] = null;
            BufferedReader reader = new BufferedReader(new FileReader("conf.txt"));
            String tmp;
            int i = 0;

            while ((tmp = reader.readLine()) != null) {
                arr[i] = tmp;
                i++;
                // if the conf is invalid
                if (i > 2) break;
            }

            // check if all 3 details exists
            if (i != 3) {
                System.out.println("Bad conf.txt");
                return;
            }

            reader.close();

            // connect to DB
            this.conn = DriverManager.getConnection(arr[0], arr[1], arr[2]);
            System.out.println("Connected !!");
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * formats response from server.
     * @param query
     * @param results
     * @param err
     * @return
     */
    public String outputResults(String query, String results, String err){
        String output;

        // check for errors
        if (err == null) {
            err = "";
        } else {
            err = err + "\n";
        }

        // if error is logical or structural
        if (S_ERROR.equals(results) || L_ERROR.equals(results)) {
            results = results + ": \n" + err;
        }

        // by format
        output = "-->" + query + "\n";
        output = output + "OUTPUT:\n" + results + "\n";

        return output;
    }

    private String buildResults(String query) {
        String results;
        StringBuilder sb = new StringBuilder();
        String tmp;

        // try and build results
        try {
            // create the statement to work with
            Statement statement = this.conn.createStatement();
            // create results set
            ResultSet rs = statement.executeQuery(query);

            int colsNum = rs.getMetaData().getColumnCount();

            // run over the result set and build the query results
            while (rs.next()) {
                for (int i = 1; i < colsNum; ++i){
                    tmp = rs.getString(i) + ", ";
                    sb.append(tmp);
                }

                sb.append(rs.getString(colsNum));
                sb.append("\n");
            }

            results = sb.toString();
        } catch (SQLException err) {
            System.out.println(err.getMessage());
            // handling the error
            String error = err.toString();

            if(error.toLowerCase().contains("syntax"))
            {
                results = S_ERROR;
            }
            else{
                results = L_ERROR;
            }
            // format error
            return outputResults(query, results, error);
        }
        // return query results
        return results;
    }

    private String executeWithNoOutput(String query) {
        try {
            Statement statement = this.conn.createStatement();
            // get number of affected rows
            int num = statement.executeUpdate(query);

            return "Rows affected: " + Integer.toString(num);
        } catch (SQLException err) {
            System.out.println(err.toString());
            return null;
        }
    }

    private String parseDDL(String query, String command) {
        // check to see if command is inside the output list
        if(this.outputDDL.contains(command)) {
            return buildResults(query);
        } else {
            return executeWithNoOutput(query);
        }
    }

    private String parseDML(String query, String command) {
        // check to see if command is inside the no output list
        if(this.noOutputDML.contains(command)) {
            return executeWithNoOutput(query);
        } else {
            return buildResults(query);
        }
    }

    public String sendDataToRemoteServer(String query, QType.Type type) {
        String command = query.split(" ")[0].toUpperCase();

        if (type == QType.Type.DDL) {
            return this.parseDDL(query, command);
        } else {
            return this.parseDML(query, command);
        }
    }

    public String[] getDBTables() {
        String tables = this.parseDDL("SHOW TABLES", "SHOW");
        String[] arr = tables.split("\n");
        return arr;
    }
}
