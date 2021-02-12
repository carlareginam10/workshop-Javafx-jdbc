package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.Listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {
	
	private SellerService service;	

	@FXML
	private TableView<Seller> tableViewSeller;
	
	@FXML
	private TableColumn<Seller, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Seller, String> tableColumnName;
	
	@FXML
	private TableColumn<Seller, String> tableColumnEmail;
	
	@FXML
	private TableColumn<Seller, Date> tableColumnBirthDate;
	
	@FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE;
	
	@FXML
	private Button btNew;
	
	private ObservableList<Seller> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		//createDialogForm(String asoluteName, Stage parentStage) estou chamando a fun��o no clique do bot�o
		// e passando dois paramentros, o primeiro � o caminho do formul�rio e o segundo � o pai da janela
		Stage parentStage = Utils.currentStage(event);
		//como � um botao pra cadastrar um novo dptp o form come�a vazio
		//ent�o instancio um dpto vazio:
		Seller obj = new Seller();
		//tenho que injetar o objeto no controlador do form, pra isso passo o parametro obj
		createDialogForm(obj,"/gui/SellerForm.fxml", parentStage);
	}
	
	public void setSellerService (SellerService service) {
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
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
	//	Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
	//	Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
				
	}
	
	public void updateTableView() {
		if(service ==null) {
			throw new IllegalStateException("Service is null");
		}
		List<Seller> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewSeller.setItems(obsList);
		//Chama a fun��o que cria os botoes de editar
		initEditButtons() ;
		initRemoveButtons();
	}
	
	//FUN��O PARA CARREGAR A JANELA DO FORMUL�RIO PARA PREENCHER UM NOVO DEPARTAMENTO
	// ESSA FUN��O DEVER� SER CHAMADA NO BOT�O NOVO NA TELA DE DEPARTAMENTOS - VER LINHA 46
	//quando criamos uma janela de dialogo temos que informar qual Stage criou essa janela : Stage parentStage
	private void createDialogForm(Seller obj, String asoluteName, Stage parentStage) {
		//instanciar a janela de di�loago
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(asoluteName));
			Pane pane = loader.load();
			
			//o m�todo vai injetar o dpto no controlador da tela de form
			//para isso � necess�rio pegar uma referencia para o controlador
			SellerFormController controller = loader.getController();
			//injetando o departamento no controlador
			controller.setSeller(obj);
			//Me inscrevendo para escutar o evento do ondatachange
			controller.subscribeDataChangeListener(this);
			
			// carregar o obj no form
			controller.updateFormData();
			
			//Injetando SellerService
			controller.SetSellerService(new SellerService());
						
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Seller data");
			
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

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}
	
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void  removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("confirmation", "Are you sure to delet?");
		//para acessar o objeto que est� dentro do Optional tenho que chamar o .get
		if(result.get() == ButtonType.OK) {
			if(service ==null) {
				throw new IllegalStateException("Service was null");
				
			}
			try {
				service.remove(obj);
				updateTableView();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
			
		}
		
	}
	

}
