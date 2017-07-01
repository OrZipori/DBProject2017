import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

/**
 * Class to launch the GUI.
 */
public class MainLauncher extends Application {
    
    /**
     * Start the GUI.
     * @param stage
     * @throws Exception 
     */
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("GUI.fxml"));
        Scene mainGUI = new Scene(root);

        stage.setTitle("DB Project 2017 GUI");
        stage.setScene(mainGUI);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Main function.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }  
}
