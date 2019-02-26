package client.ui.javafx;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import client.Client;
import client.exceptions.UICheckFieldException;
import client.ui.UIEnums;
import client.ui.UIEnums.ABAS;
import common.Const;
import common.exceptions.ServerServiceException;
import common.model.BusinessEntity;
import common.model.Functionality;
import common.model.Perfil;

public class ProfileTab extends GenericTab {

	private static final long serialVersionUID = -1464374223408646670L;
	
	private TextField txtName = new TextField();
	private TextField txtDescription = new TextField();
	private DatePicker dpCreationDate = new DatePicker();
	private TableView<TableModelPermission> tablePermission = new TableView<TableModelPermission>();
	
	public ProfileTab() {
		super();
		this.setText(ABAS.Perfil.toString());
		initPnlForm();
		createTableAllItemsHeader();
		createTablePermissionHeader();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createTablePermissionHeader() {
		TableColumn permissionColumn = new TableColumn("Permission");
		permissionColumn.setCellValueFactory(new PropertyValueFactory<>("checked"));
		permissionColumn.setCellFactory(new Callback<TableColumn<TableModelPermission, Boolean>, TableCell<TableModelPermission, Boolean>>() {
	        public TableCell<TableModelPermission, Boolean> call(TableColumn<TableModelPermission, Boolean> p) {
	            return new CheckBoxTableCell<TableModelPermission, Boolean>();
	        }
	    });
        TableColumn pluginColumn = new TableColumn<>("Plugin");
        pluginColumn.setEditable(true);
		pluginColumn.setCellValueFactory(new PropertyValueFactory<>("plugin"));
		TableColumn nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		TableColumn descriptionColumn = new TableColumn<>("Description");
		descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
		TableColumn creationDateColumn = new TableColumn<>("Creation Date");
		creationDateColumn.setCellValueFactory(new PropertyValueFactory<>("dataCriacao"));
		tablePermission.getColumns().addAll(pluginColumn, nameColumn, descriptionColumn, creationDateColumn, permissionColumn);
        tablePermission.setEditable(true);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void createTableAllItemsHeader() {
		TableColumn nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		TableColumn descriptionColumn = new TableColumn<>("Description");
		descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
		TableColumn creationDateColumn = new TableColumn<>("Creation Date");
		creationDateColumn.setCellValueFactory(new PropertyValueFactory<>("dataCriacao"));
		getTableAllItems().getColumns().addAll(nameColumn, descriptionColumn, creationDateColumn);
	}

	@Override
	public void loadData() throws RemoteException, ServerServiceException, NotBoundException {
		populateTableAllItems(Client.getServer().getPerfis());
		atualizarCacheTodasFuncBanco();
		tablePermission.getItems().clear();
		List<TableModelPermission> perms = new ArrayList<TableModelPermission>();
		for(Functionality func : getCacheTodasFuncBanco()) {
			perms.add(new TableModelPermission(func.getId(), func.getName(), func.getDescription(), func.getDataCriacao(), func.getPlugin()));
		}
		tablePermission.getItems().addAll(FXCollections.observableArrayList(perms));
		tablePermission.autosize();
	}

	@Override
	public void initPnlForm() {
		getFormPane().add(new Label("Name: "));
		getFormPane().add(txtName, "growx, wrap");
		getFormPane().add(new Label("Description: "));
		getFormPane().add(txtDescription, "growx, wrap");
		getFormPane().add(new Label("Creation Date: "));
		dpCreationDate.setDisable(true);
		getFormPane().add(dpCreationDate, "growx, wrap");
		getFormPane().add(tablePermission, "grow, spanx");
	}

	@Override
	public BusinessEntity createObjToBeSaved() {
		String name = txtName.getText();
		String desc = txtDescription.getText();
		Date creationDate = new Date();
		Perfil perfil = new Perfil(null, name, desc, creationDate);
		perfil.setPermissoes(getPermissions());
		return perfil;
	}

	private List<Functionality> getPermissions() {
		List<Functionality> out = new ArrayList<Functionality>();
		tablePermission.getItems().forEach(perm -> {
			if(perm.getChecked().getValue()) { 
				Functionality func = new Functionality(perm.getId(), perm.getName(), perm.getDescription(), perm.getDataCriacao());
				func.setPlugin(perm.getPlugin());
				out.add(func);
			}
		});
		return out;
	}

	@Override
	public void populateConsultComboBox() {
		this.getCmbConsult().getItems().add(UIEnums.FILTROS_PERFIL.Nome.toString());
		this.getCmbConsult().getItems().add(UIEnums.FILTROS_PERFIL.Descrição.toString());
		this.getCmbConsult().getItems().add(UIEnums.FILTROS_PERFIL.Data.toString());
	}

	@Override
	public void fillFormToEdit(int selectedRow) throws RemoteException, ServerServiceException, NotBoundException {
		Perfil perfil = (Perfil)getTableAllItems().getSelectionModel().getSelectedItem();
		txtName.setText(perfil.getName());
		txtDescription.setText(perfil.getDescription());
		dpCreationDate.setValue(convertDateToLocalDate(perfil.getDataCriacao()));
		List<Functionality> permissions = Client.getServer().searchPermissionsByPerfilId(perfil.getId());
		checkPermissions(permissions);
	}

	private void checkPermissions(List<Functionality> permissions) {
		for(Functionality func : permissions) {
			int pos = 0;
			for(TableModelPermission perm : tablePermission.getItems()) {
				if(perm.getId().equals(func.getId())) {
					tablePermission.getItems().get(pos).setChecked(new SimpleBooleanProperty(true));
					break;
				};
				pos++;
			};
		};
	}

	@Override
	public void clearForm() {
		txtName.setText("");
		txtDescription.setText("");
		dpCreationDate.setValue(LocalDate.now());
		tablePermission.getItems().forEach(perm -> {
			perm.setChecked(new SimpleBooleanProperty(false));
		});
	}

	@Override
	public void setEnabledForm(boolean b) {
		txtName.setDisable(!b);
		txtDescription.setDisable(!b);
		tablePermission.setDisable(!b);
	}

	@Override
	public boolean checkFieldsOnCreate() throws UICheckFieldException {
		String value = txtName.getText();
		if(value == null || value.isEmpty() || value.equals("")) throw new UICheckFieldException(Const.INFO_EMPTY_FIELD.replace("?1", "nome"));
		return true;
	}

	@Override
	public List<? extends BusinessEntity> realizarBusca(String atributo, String termo)
			throws RemoteException, ServerServiceException, NotBoundException {
		return Client.getServer().getPerfis();
	}

	@Override
	public void realizarDelete(Long id) throws RemoteException, ServerServiceException, NotBoundException {
		Client.getServer().deletePerfil(id);
	}

	@Override
	public void realizarCreate(BusinessEntity objToSave)
			throws RemoteException, ServerServiceException, NotBoundException {
		Client.getServer().createPerfil((Perfil)objToSave);
	}

	@Override
	public void realizarUpdate(BusinessEntity objToSave)
			throws RemoteException, ServerServiceException, NotBoundException {
		Client.getServer().updatePerfil((Perfil)objToSave);
	}
}
