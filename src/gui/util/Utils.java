package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {
	
	//evento que o bot�o recebeu
	public static Stage currentStage (ActionEvent event){
		
		//event.getSource() � muito gen�rico, por isso � feito um cast para Node
		//getScene() pega a cena
		//.getWindow() pega a janela
		return (Stage)((Node)event.getSource()).getScene().getWindow();
	}
	
	public static Integer tryParsetoInt(String str) {
		try {
			return Integer.parseInt(str);			
		} catch (NumberFormatException e) {
			return null;
		}
		
	}

}
