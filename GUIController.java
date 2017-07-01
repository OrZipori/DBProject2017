import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Controller for DDL and DML GUI client.
 */
public class GUIController implements Initializable {
    private final DBConnection server;
    private File homePath = null;
    
    @FXML
    TextArea queryDML;
    @FXML
    TextArea queryDDL;
    @FXML
    TextArea responseDML;
    @FXML
    TextArea responseDDL;
    @FXML
    VBox tablesBox;

    /**
     * Constructor.
     */
    public GUIController() {
        super();
        this.server = new DBConnection();
        //this.server = null;
    }
    
    /**
     * Initialize the controller.
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // get the list of tables
        String[] arr = this.server.getDBTables();

        for (int i = 0; i < arr.length; ++i) {
            Button tmp = new Button();
            tmp.setText(arr[0]);
            tmp.setId(arr[0]);

            tmp.setOnAction();
        }


    }

    /**
     * Sends request to server and appends response in a separate thread.
     * @param query
     * @param responseArea
     * @param type
     */
    private void workerThread(String query, TextArea responseArea, QType.Type type){
        // Because the thread is an inner class we need to redeclare the variables to final
        final QType.Type t = type;
        final TextArea txtArea = responseArea;
        final String q = query;

        Thread worker = new Thread(new Runnable(){
            @Override
            public void run(){
                String results;

                System.out.println("Thread Started Query :\n" + q + "\n");
                results = server.sendDataToRemoteServer(q, t);
                System.out.println("Thread End");
                // append the results
                txtArea.appendText(results + "\n");
            }
        });

        // start running
        worker.start();
    }

    /**
     * Sends DML query to the server and run the response in different thread.
     * @param event
     */
    @FXML
    private void sendQueryDDL(ActionEvent event){
        String qry = queryDDL.getText();

        // if not empty query
        if(qry.length() > 0){
            // clean query text field
            queryDDL.setText("");

            // run the response in different thread so GUI won't stuck
            this.workerThread(qry, responseDDL, QType.Type.DDL);
        }
    }
    
    /**
     * Sends DML query to the server and run the response in different thread.
     * @param event 
     */
    @FXML
    private void sendQueryDML(ActionEvent event){
        String qry = queryDML.getText();

        // if not empty query
        if(qry.length() > 0){
            // clean query text field
            queryDML.setText("");

            // run the response in different thread so GUI won't stuck
            this.workerThread(qry, responseDML, QType.Type.DML);
        }
    }

    /**
     * Use Open Dialog to allow user to select its file.
     * @param title title
     * @return path
     */
    private String getScriptFile(String title){
        FileChooser fc = new FileChooser();
        fc.setTitle(title);

        // for next time uses
        if (this.homePath != null) {
            fc.setInitialDirectory(this.homePath);
        }

        // open the dialog
        File file = fc.showOpenDialog(queryDDL.getScene().getWindow());
        this.homePath = file.getParentFile();

        return file.getPath();
    }

    /**
     * Execute a DML script from a file.
     * @param event 
     */
    @FXML
    private void executeDMLScript(ActionEvent event){
        ScriptExecute se = new ScriptExecute(this.server);
        String script = this.getScriptFile("Please Provide DML script");
        String results = se.execute(script, QType.Type.DML);
        
        responseDML.appendText(results + "\n");
    }
    
    /**
     * Execute a DDL script from a file.
     * @param event
     */
    @FXML
    private void executeDDLScript(ActionEvent event) {
        ScriptExecute se = new ScriptExecute(this.server);
        String script = this.getScriptFile("Please Provide DDL script");
        String results = se.execute(script, QType.Type.DDL);
        
        responseDDL.appendText(results + "\n");
    }

    @FXML
    private void executeSQ(ActionEvent event) {

    }

    @FXML
    private void setTable(ActionEvent event) {

    }
}
