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
	
	//Inserindo uma dependência de departamento
	//entity é a entidade relacionada a esse formulário
	
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
	
	
	//implementando o método set do entity
	//Agora o controlador tem uma instância do departmaento
	
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
	
	

}
