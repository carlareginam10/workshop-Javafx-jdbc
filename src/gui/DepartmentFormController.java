package gui;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.Listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationExcepetion;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {
	
	//Inserindo uma dependência de departamento
	//entity é a entidade relacionada a esse formulário
	
	private Department entity;
	
	private DepartmentService service;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	
	//implementando o método set do entity
	//Agora o controlador tem uma instância do departmaento
	
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	public void SetDepartmentService (DepartmentService service) {
		this.service = service;
	}
	
	public void subscribeDataChangeListener( DataChangeListener listener){
		//add na lista
		//outros objetos desde que implementem o DataChangeListener podem se inscrever para receber o evento da classe
		dataChangeListeners.add(listener);
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if(entity ==null) {
			throw new IllegalStateException("Entity was null");
		}
		if(service ==null) {
			throw new IllegalStateException("service was null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			//Estou pegando uma referencia pra janela atual e chamo a opção close pra fechar a janela
			Utils.currentStage(event).close();
			
		} 
		catch (ValidationExcepetion e) {
			setErrorMessages(e.getErrors());
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
			
		}
		
		
	}
	
	private void notifyDataChangeListeners() {
		for(DataChangeListener listener : dataChangeListeners) {
			listener.onDataChange();
		}
		
	}

	private Department getFormData() {
		Department obj = new Department();
		ValidationExcepetion exception = new ValidationExcepetion("Validation error");
		
		//está pegando o id que está preenchido no form
		//chamando a função pra converter pra inteiro
		obj.setId(Utils.tryParsetoInt(txtId.getText()));
		//trim elimina qualquer espaço em branco no começo ou no final
		if(txtName.getText()==null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty"); 
		}
		
		obj.setName(txtName.getText());
		
		if(exception.getErrors().size() > 0) {
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
		//para aceitar apenas número inteiro no Id
		Constraints.setTextFieldInteger(txtId);
		//para aceitar no máximo 30 caracteres
		Constraints.setTextFieldMaxLength(txtName, 30);
		
	}
	
	
		//pegar os dados do departamento e popular as caixinhas de texto do formulário
	
	public void updateFormData() {
		//começar verificando se minha entidade está nula
		if(entity == null) {
			throw new IllegalStateException("Entidade está nula");
			
		}
		//jogar nas caixas de texto do form os dados que estão no objeto entity do tipo deparment
		//String.valueOf foi colocado pq a cx de texto trabalha com string, então tenho que converter o Id da entidade que é inteiro para string
		txtId.setText(String.valueOf(entity.getId()));
		//aqui não precisa converter pq o name já uma string
		txtName.setText(entity.getName());
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if(fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));;
			
		}
	}
	
	
	

}
