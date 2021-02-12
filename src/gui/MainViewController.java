package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;
import model.services.SellerService;

public class MainViewController implements Initializable {
	
	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	public void onMenuItemSellerAction () {
		loadView("/gui/SellerList.fxml", (SellerListController controller) -> {
			controller.setSellerService(new SellerService());
			controller.updateTableView();
		} );
	}
	
	
	
	public void onMenuItemDepartmentAction () {
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		} );
	}
	
	public void onMenuItemAboutAction () {
		//essa função  x ->{} não executa nada, por enquanto foi passada apenas para não dar erro no loadView que espera um parametro
		loadView("/gui/About.fxml", x ->{});
	}
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	private synchronized <T> void loadView(String asoluteName, Consumer<T> initializingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(asoluteName));
			VBox newVBox = loader.load();
			Scene mainScene = Main.getMainScene();
			//pega o primeiro elemento da view
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			//as 2 linhas abaixo servem para executar  a função passada na linha 38  (DepartmentListController controller) 
			
			//retorna o controlador do tipo que eu chamar na linha 37 ou 44 
			T controller = loader.getController();
			
			//executa a ação initializingAction que foi passada como parametro
			initializingAction.accept(controller);
			
			
			
			
		} catch (IOException e) {
			Alerts.showAlert("IOException", null,  e.getMessage(), Alert.AlertType.ERROR);
		}
		
		
	}
	
	

}
