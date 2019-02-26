package client.ui.javafx;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.tbee.javafx.scene.layout.MigPane;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import client.Client;
import client.ui.GenericUIFunctions;
import client.ui.UIEnums.FORM_CONTEXT;
import common.Const;
import common.exceptions.ServerServiceException;
import common.model.BusinessEntity;
import common.model.Functionality;

public abstract class GenericTab extends Tab implements Serializable, GenericUIFunctions {

	private static final long serialVersionUID = 9025913767736072389L;

	private TableView<BusinessEntity> tableAllItems = new TableView<>();
	private ComboBox<String> cmbConsult = new ComboBox<String>();
	private TextField txtSearchString = new TextField();
	private Button btnSearch = new Button("Search");
	private MigPane formPane = new MigPane("", "[al right][grow]", "[grow]");
	private Button btnRemove = new Button("Remove");
	private Button btnCancel = new Button("Cancel");
	private Button btnSave = new Button("Save");
	private Button btnNew = new Button("New");

	private List<Functionality> cacheTodasFuncionalidadesBanco;

	public GenericTab() {
		MigPane mainTabPane = new MigPane("", "[grow 70][grow 30]", "[][grow][]");
		mainTabPane.add(createSearchPane(), "pushy, wrap");
		mainTabPane.add(tableAllItems, "grow, spany");
		mainTabPane.add(formPane, "top, grow, wrap");
		mainTabPane.add(createControlPane(), "skip 1, growx");
		this.setContent(mainTabPane);
		this.setClosable(false);
		this.tableAllItems.getSelectionModel().setSelectionMode((SelectionMode.MULTIPLE));
		initListeners();
		populateConsultComboBox();
	}

	protected abstract void createTableAllItemsHeader();

	@Override
	public void initListeners() {
		btnNew.setOnAction(createBtnNewClickEvent());
		btnCancel.setOnAction(createBtnCancelClickEvent());
		btnSave.setOnAction(createBtnSaveClickEvent());
		btnRemove.setOnAction(createBtnRemoveClickEvent());
		tableAllItems.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection == null) {
				setContext(FORM_CONTEXT.Proibido);
		    } else {
		    	setContext(FORM_CONTEXT.Editar, 0);
		    }
		});
	}
	
	@Override
	public void populateTableAllItems(List<? extends BusinessEntity> objs) {
		getTableAllItems().getItems().clear();
		getTableAllItems().getItems().addAll(FXCollections.observableArrayList(objs));
		getTableAllItems().autosize();
	}

	
	@Override
	public void setContextoProibido() {
		this.btnCancel.setDisable(true);
		this.btnRemove.setDisable(true);
		this.getBtnSave().setDisable(true);
		this.setEnabledForm(false);
		this.clearForm();
	}

	@Override
	public void setContextoEditar(int selectedRow) {
		this.btnCancel.setDisable(false);
		this.btnRemove.setDisable(false);
		this.getBtnSave().setDisable(false);
		this.setEnabledForm(true);
		try {
			this.fillFormToEdit(selectedRow);
		} catch (RemoteException | ServerServiceException | NotBoundException e) {
			dealWithError(e);
		}
	}

	@Override
	public void setContextoCriar() {
		this.btnCancel.setDisable(false);
		this.btnRemove.setDisable(true);
		this.getBtnSave().setDisable(false);
		this.setEnabledForm(true);
		this.clearForm();
	}

	@Override
	public Long getSelectedItemId() {
		return tableAllItems.getSelectionModel().getSelectedItem().getId();
	}

	@Override
	public void showInfoMessage(String message) {
		Alert alert = new Alert(AlertType.INFORMATION, message);
		alert.showAndWait();
	}

	@Override
	public void showErrorMessage(String message) {
		Alert alert = new Alert(AlertType.ERROR, message);
		alert.showAndWait();
	}

	@Override
	public boolean askConfirmation(String msg) {
		Alert alert = new Alert(AlertType.CONFIRMATION, msg, ButtonType.NO, ButtonType.YES);
		Optional<ButtonType> option = alert.showAndWait();
		if (option.get().equals(ButtonType.OK))
			return true;
		return false;
	}

	private EventHandler<ActionEvent> createBtnRemoveClickEvent() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (askConfirmation(Const.WARN_CONFIRM_DELETE)) {
					actionRemoveItem();
				}
			}
		};
	}

	private EventHandler<ActionEvent> createBtnSaveClickEvent() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (btnRemove.isDisabled()) {
					actionSaveNewItem();
				} else {
					actionUpdateItem();
				}
			}
		};
	}

	private EventHandler<ActionEvent> createBtnCancelClickEvent() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				clearForm();
				setEnabledForm(false);
			}
		};
	}

	private EventHandler<ActionEvent> createBtnNewClickEvent() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					loadData();
				} catch (RemoteException | ServerServiceException | NotBoundException e) {
					/* ignorar */ }
				setContext(FORM_CONTEXT.Criar);
			}
		};
	}

	public LocalDate convertDateToLocalDate(Date date) {
		if (date == null)
			return LocalDate.now();
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	private MigPane createControlPane() {
		MigPane controlPane = new MigPane("", "[][][][]", "");
		controlPane.add(btnRemove, "pushx");
		controlPane.add(btnCancel);
		controlPane.add(getBtnSave());
		controlPane.add(btnNew);
		return controlPane;
	}

	private MigPane createSearchPane() {
		MigPane searchPane = new MigPane("ins 0", "[grow]", "");
		searchPane.add(new Label("Filtrar por:"));
		setCmbConsult(new ComboBox<String>());
		searchPane.add(getCmbConsult());
		searchPane.add(txtSearchString, "growx");
		searchPane.add(btnSearch);
		return searchPane;
	}


	public TableView<BusinessEntity> getTableAllItems() {
		return tableAllItems;
	}

	public MigPane getFormPane() {
		return formPane;
	}

	public void setFormPane(MigPane formPane) {
		this.formPane = formPane;
	}

	public List<Functionality> getCacheTodasFuncBanco() {
		return cacheTodasFuncionalidadesBanco;
	}

	public void atualizarCacheTodasFuncBanco() {
		try {
			cacheTodasFuncionalidadesBanco = Client.getServer().getFunctionalities();
		} catch (RemoteException | ServerServiceException | NotBoundException e) {
		}
	}

	public ComboBox<String> getCmbConsult() {
		return cmbConsult;
	}

	public void setCmbConsult(ComboBox<String> cmbConsult) {
		this.cmbConsult = cmbConsult;
	}

	public Button getBtnSave() {
		return btnSave;
	}

	public void setBtnSave(Button btnSave) {
		this.btnSave = btnSave;
	}
}
