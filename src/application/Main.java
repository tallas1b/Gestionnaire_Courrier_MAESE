package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class Main extends Application {
	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("Main.fxml"));
			
			
//			AnchorPane root = null;
//			FXMLLoader loader = null;
//
//			try
//			{
//			   // URL url = getClass().getResource("Main.fxml");
//			    loader = new FXMLLoader(getClass().getResource("Main.fxml"));
//			    root = (AnchorPane) loader.load();
//			    //controller = loader.getController();
//			}
//			catch (Exception e)
//			{
//			    System.err.println(e.getMessage());
//			    return;
//			}
			
			
			primaryStage.setTitle("Horus Gestionnaire Courrier MIAAESE");
			primaryStage.getIcons().add(new Image("/images/shield.jpg"));
			primaryStage.setResizable(false);
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
 