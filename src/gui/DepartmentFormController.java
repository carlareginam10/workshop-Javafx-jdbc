package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;

public class DepartmentFormController implements Initializable {
	
	//Inserindo uma depend�ncia de departamento
	//entity � a entidade relacionada a esse formul�rio
	
	private Department entity;
	
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
	
	
	//implementando o m�todo set do entity
	//Agora o controlador tem uma inst�ncia do departmaento
	
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	
	
	
	
	@FXML
	public void onBtSaveAction() {
		System.out.println("onBtSaveAction");
	}
	
	@FXML
	public void onBtCancelAction() {
		System.out.println("onBtCancelAction");
	}
	

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO Auto-generated method stub
		
	}
	
	public void initializeNodes() {
		//para aceitar apenas n�mero inteiro no Id
		Constraints.setTextFieldInteger(txtId);
		//para aceitar no m�ximo 30 caracteres
		Constraints.setTextFieldMaxLength(txtName, 30);
		
	}
	
	
		//pegar os dados do departamento e popular as caixinhas de texto do formul�rio
	
	public void updateFormData() {
		//come�ar verificando se minha entidade est� nula
		if(entity == null) {
			throw new IllegalStateException("Entidade est� nula");
			
		}
		//jogar nas caixas de texto do form os dados que est�o no objeto entity do tipo deparment
		//String.valueOf foi colocado pq a cx de texto trabalha com string, ent�o tenho que converter o Id da entidade que � inteiro para string
		txtId.setText(String.valueOf(entity.getId()));
		//aqui n�o precisa converter pq o name j� uma string
		txtName.setText(entity.getName());
	}
	
	

}
