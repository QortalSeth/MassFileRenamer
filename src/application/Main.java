package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
		    AnchorPane root = FXMLLoader.load(Main.class.getResource("/application/MassFileRenamer.fxml"));
		    primaryStage.setTitle("Mass File Renamer");
	        primaryStage.setScene(new Scene(root));
		    primaryStage.show();
		    
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
