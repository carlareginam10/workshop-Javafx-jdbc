package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.Listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationExcepetion;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	// Inserindo uma depend�ncia de departamento
	// entity � a entidade relacionada a esse formul�rio

	private Seller entity;

	private SellerService service;

	private DepartmentService departmentService;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker dpBirthDate;

	@FXML
	private TextField txtBaseSalary;

	@FXML
	private ComboBox<Department> comboBoxDepartment;

	@FXML
	private Label labelErrorName;
	@FXML
	private Label labelErrorEmail;
	@FXML
	private Label labelErrorBirthDate;
	@FXML
	private Label labelErrorBaseSalary;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	private ObservableList<Department> obsList;

	// implementando o m�todo set do entity
	// Agora o controlador tem uma inst�ncia do departmaento

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void SetServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		// add na lista
		// outros objetos desde que implementem o DataChangeListener podem se inscrever
		// para receber o evento da classe
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("service was null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			// Estou pegando uma referencia pra janela atual e chamo a op��o close pra
			// fechar a janela
			Utils.currentStage(event).close();

		} catch (ValidationExcepetion e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);

		}

	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChange();
		}

	}

	private Seller getFormData() {
		Seller obj = new Seller();
		ValidationExcepetion exception = new ValidationExcepetion("Validation error");

		// est� pegando o id que est� preenchido no form
		// chamando a fun��o pra converter pra inteiro
		obj.setId(Utils.tryParsetoInt(txtId.getText()));
		// trim elimina qualquer espa�o em branco no come�o ou no final
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}

		obj.setName(txtName.getText());

		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		System.out.println("onBtCancelAction");
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO Auto-generated method stub

	}

	public void initializeNodes() {
		// para aceitar apenas n�mero inteiro no Id
		Constraints.setTextFieldInteger(txtId);
		// para aceitar no m�ximo 30 caracteres
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyy");
		initializeComboBoxDepartment();
	}

	// pegar os dados do departamento e popular as caixinhas de texto do formul�rio

	public void updateFormData() {
		// come�ar verificando se minha entidade est� nula
		if (entity == null) {
			throw new IllegalStateException("Entidade est� nula");

		}
		// jogar nas caixas de texto do form os dados que est�o no objeto entity do tipo
		// deparment
		// String.valueOf foi colocado pq a cx de texto trabalha com string, ent�o tenho
		// que converter o Id da entidade que � inteiro para string
		txtId.setText(String.valueOf(entity.getId()));
		// aqui n�o precisa converter pq o name j� uma string
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		// ((LocalDate) dpBirthDate).setValue(entity.getBirthDate());
		if (entity.getDepartment() == null) {
			// para deixar selecionado o primeiro elemento do comboBox
			comboBoxDepartment.getSelectionModel().selectFirst();
		} else {
			comboBoxDepartment.setValue(entity.getDepartment());
		}
	}

	public void loadAssociateObjects() {
		if(departmentService== null) {
			throw new IllegalStateException("Department was null");
		}
		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(obsList);
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
			;

		}
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}

}
