package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.Listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {
	
	private DepartmentService service;

	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Department, String> tableColumnName;
	
	@FXML
	private Button btNew;
	
	private ObservableList<Department> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		//createDialogForm(String asoluteName, Stage parentStage) estou chamando a função no clique do botão
		// e passando dois paramentros, o primeiro é o caminho do formulário e o segundo é o pai da janela
		Stage parentStage = Utils.currentStage(event);
		//como é um botao pra cadastrar um novo dptp o form começa vazio
		//então instancio um dpto vazio:
		Department obj = new Department();
		//tenho que injetar o objeto no controlador do form, pra isso passo o parametro obj
		createDialogForm(obj,"/gui/DepartmentForm.fxml", parentStage);
	}
	
	public void setDepartmentService (DepartmentService service) {
		this.service = service;
	}
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();
		
	}



	private void initializeNodes() {
		//iniciar a o compartamento das colunas
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
				
	}
	
	public void updateTableView() {
		if(service ==null) {
			throw new IllegalStateException("Service is null");
		}
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);
	}
	
	//FUNÇÃO PARA CARREGAR A JANELA DO FORMULÁRIO PARA PREENCHER UM NOVO DEPARTAMENTO
	// ESSA FUNÇÃO DEVERÁ SER CHAMADA NO BOTÃO NOVO NA TELA DE DEPARTAMENTOS - VER LINHA 46
	//quando criamos uma janela de dialogo temos que informar qual Stage criou essa janela : Stage parentStage
	private void createDialogForm(Department obj, String asoluteName, Stage parentStage) {
		//instanciar a janela de diáloago
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(asoluteName));
			Pane pane = loader.load();
			
			//o método vai injetar o dpto no controlador da tela de form
			//para isso é necessário pegar uma referencia para o controlador
			DepartmentFormController controller = loader.getController();
			//injetando o departamento no controlador
			controller.setDepartment(obj);
			//Me inscrevendo para escutar o evento do ondatachange
			controller.subscribeDataChangeListener(this);
			
			// carregar o obj no form
			controller.updateFormData();
			
			//Injetando DepartmentService
			controller.SetDepartmentService(new DepartmentService());
						
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
			
			//quem vai ser a cena do stage? como é um novo Stage vai ser uma nova cena
			//o elemento raiz da cena é o pane
			dialogStage.setScene(new Scene(pane));
			//setResizable serve pra dizer que a caixa de dialago pode ou não ser redimensionada aceita true ou false
			dialogStage.setResizable(false);
			
			//quem é o Stage pai dessa janela?
			dialogStage.initOwner(parentStage);
			
			// initModality diz se a janela vai ser modal ou se vai ter outro comportamento
			//Modality.WINDOW_MODAL funciona como modal, enquando não fechar ela não pode acessar outra janela
			dialogStage.initModality(Modality.WINDOW_MODAL);
			
			dialogStage.showAndWait();
			
			
			
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error Loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChange() {
		updateTableView();
		
	}
	

}
