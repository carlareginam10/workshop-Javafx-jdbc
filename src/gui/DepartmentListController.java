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
		//createDialogForm(String asoluteName, Stage parentStage) estou chamando a fun��o no clique do bot�o
		// e passando dois paramentros, o primeiro � o caminho do formul�rio e o segundo � o pai da janela
		Stage parentStage = Utils.currentStage(event);
		//como � um botao pra cadastrar um novo dptp o form come�a vazio
		//ent�o instancio um dpto vazio:
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
	
	//FUN��O PARA CARREGAR A JANELA DO FORMUL�RIO PARA PREENCHER UM NOVO DEPARTAMENTO
	// ESSA FUN��O DEVER� SER CHAMADA NO BOT�O NOVO NA TELA DE DEPARTAMENTOS - VER LINHA 46
	//quando criamos uma janela de dialogo temos que informar qual Stage criou essa janela : Stage parentStage
	private void createDialogForm(Department obj, String asoluteName, Stage parentStage) {
		//instanciar a janela de di�loago
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(asoluteName));
			Pane pane = loader.load();
			
			//o m�todo vai injetar o dpto no controlador da tela de form
			//para isso � necess�rio pegar uma referencia para o controlador
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
			
			//quem vai ser a cena do stage? como � um novo Stage vai ser uma nova cena
			//o elemento raiz da cena � o pane
			dialogStage.setScene(new Scene(pane));
			//setResizable serve pra dizer que a caixa de dialago pode ou n�o ser redimensionada aceita true ou false
			dialogStage.setResizable(false);
			
			//quem � o Stage pai dessa janela?
			dialogStage.initOwner(parentStage);
			
			// initModality diz se a janela vai ser modal ou se vai ter outro comportamento
			//Modality.WINDOW_MODAL funciona como modal, enquando n�o fechar ela n�o pode acessar outra janela
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
