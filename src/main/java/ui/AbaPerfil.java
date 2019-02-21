package ui;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import client.Client;
import common.Const;
import exception.ServerServiceException;
import exception.UICheckFieldException;
import model.BusinessEntity;
import model.Functionality;
import model.Perfil;
import net.miginfocom.swing.MigLayout;

public class AbaPerfil extends AbaGenerica {

	private static final long serialVersionUID = -6213919187419684978L;
	
	private JLabel lblNomePerfil = new JLabel("Nome: ");
	private JLabel lblDescricao = new JLabel("Descrição: ");
	private JLabel lblDataCriacao = new JLabel("Data de criação: ");
	private JTextField txtNomePerfil = new JTextField(15);
	private JTextField txtDescricao = new JTextField(15);
	private UtilDateModel dateModel = new UtilDateModel();
	private Properties dateProperties = new Properties();
	private JDatePanelImpl datePanel = new JDatePanelImpl(dateModel, dateProperties);
	private JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
	private TableWithCheckBox tblFuncs = new TableWithCheckBox();
	private JScrollPane tblFuncsScroll;
	
	public AbaPerfil(JFrame parentFrame) {
		super(parentFrame);
		
		initPnlForm();
		setContextoEditar(false);
	}
	
	/**
	 * Método para salvar as permissões que foram concedidas utilizando os checkbox
	 * @param perfilId - id do perfil ao qual as permissões estão associadas
	 * @throws RemoteException
	 * @throws ServerServiceException
	 * @throws NotBoundException
	 */
	private List<Functionality> criarPermissoesConcedidas() {
		List<Functionality> out = new ArrayList<Functionality>();
		for(int i=0; i < tblFuncs.getRowCount(); i++) {
			Boolean valor = Boolean.valueOf(tblFuncs.getValueAt(i, 5).toString());
			if(valor) {
				Long id = Long.parseLong(tblFuncs.getValueAt(i, 0).toString());
				out.add(new Functionality(id, null, null, null));
			}
		}
		return out;
	}
	
	/**
	 * Método para popular tabela de permissões
	 * @param perfs - Lista contendo as permissões a serem apresentados na tabela
	 * @param tipo - tipo para gerar Header da tabela
	 */
	private void popularTabelaPermissoes(List<Functionality> permsConcedidas) {
		Vector<Vector<Object>> dadosFinal = new Vector<Vector<Object>>();
		Vector<Object> linha;
		checkCacheTodasFuncBancoEmpty();
		for(Functionality func : getCacheTodasFuncBanco()) {
			linha = new Vector<Object>();
			linha.add(func.getId());
			linha.add(func.getName());
			linha.add(func.getDescription());
			linha.add(func.getDataCriacaoToString());
			linha.add(func.getPlugin().toString());
			linha.add(hasPermission(func.getId(), permsConcedidas));
			dadosFinal.add(linha);
		};
		this.tblFuncs.setModel(new DefaultTableModel(dadosFinal, gerarHeaderPermissao()));
		this.setJTableColumnInsivible(tblFuncs, 0);
	}
	
	/**
	 * Método para checar se determinada funcionalidade esta na lista de permissões.
	 * @param funcId - id da funcionalidade a ser verificada
	 * @param allFuncsWithPermission - lista com as permissões concedidas
	 * @return true se funcionalidade for permitida, false caso contrário
	 */
	private boolean hasPermission(Long funcId, List<Functionality> allFuncsWithPermission) {
		for(Functionality perm : allFuncsWithPermission) {
			if(funcId.equals(perm.getId())) return true;
		}
		return false;
	}
	
	/**
	 * Método para gerar HEADER da tbl de permissões
	 * @return - header com as colunas que devem aparecer
	 */
	private Vector<String> gerarHeaderPermissao() {
		Vector<String> header = new Vector<String>();
		header.add("ID");
		header.add("NOME");
		header.add("DESCRIÇÃO");
		header.add("DATA DE CRIAÇÃO");
		header.add("PLUGIN");
		header.add("PERMITIDO");
		return header;
	}
	
	/**
	 * Método para setar tabela de permissões quando for para editar um perfil
	 * @param setar - irá definir se é para editar perms existentes (true) ou se é um novo perfil (false)
	 * @param perfilId - id do perfil selecionado (só necessário se setar == true) ou null
	 */
	private void setEditarTblPermissoes(boolean setar, Long perfilId) {
		List<Functionality> perms = new ArrayList<Functionality>();
		try { 
			checkCacheTodasFuncBancoEmpty();
			if(setar) perms = Client.getServer().searchPermissionsByPerfilId(perfilId);
		} catch (Exception err) { }
		finally { popularTabelaPermissoes(perms); }
	}
	
	@Override
	public void initPnlForm() {
		JPanel pnlForm = new JPanel(new MigLayout("","[right][grow]",""));
		pnlForm.add(lblNomePerfil);
		pnlForm.add(txtNomePerfil, "wrap, growx");
		pnlForm.add(lblDescricao);
		pnlForm.add(txtDescricao, "wrap, growx");
		pnlForm.add(lblDataCriacao);
		datePicker.getComponent(1).setEnabled(false); // setar datePicker disabled
		pnlForm.add(datePicker, "wrap, growx");
		tblFuncs.setAutoCreateRowSorter(true);
		this.tblFuncsScroll = new JScrollPane(tblFuncs);
		pnlForm.add(tblFuncsScroll, "wrap, spanx, growx");
		registerForm(pnlForm);
	}
	
	@Override
	public void setContextoCriar(boolean setar) {
		super.setContextoCriar(setar);
		txtNomePerfil.setText("");
		txtDescricao.setText("");
		setDataModelFromStringDate(dateModel, Const.DATA_FORMAT.format(new Date()));
		setEditarTblPermissoes(false, null);
		txtNomePerfil.setEditable(setar);
		txtDescricao.setEditable(setar);
		tblFuncs.setEnabled(setar);
	}
	
	@Override
	public void setContextoEditar(boolean setar) {
		super.setContextoEditar(setar);
		if(!setar) {
			txtNomePerfil.setText("");
			txtDescricao.setText("");
			dateModel.setSelected(false);
			setEditarTblPermissoes(false, null);
		} else {
			Long perfilId = Long.parseLong(this.getTblResultado().getValueAt(this.getTblResultado().getSelectedRow(), 0).toString());
			setEditarTblPermissoes(true, perfilId);
		}
		txtNomePerfil.setEditable(setar);
		txtDescricao.setEditable(setar);
		tblFuncs.setEnabled(setar);
	}
	
	@Override
	public Vector<String> createItemsCmbConsulta() {
		Vector<String> out = new Vector<String>();
		out.add(UIEnums.FILTROS_PERFIL.Nome.toString());
		out.add(UIEnums.FILTROS_PERFIL.Descrição.toString());
		out.add(UIEnums.FILTROS_PERFIL.Data.toString());
		return out;
	}
	
	@Override
	public Vector<String> gerarHeaderTabelaResultado() {
		Vector<String> header = new Vector<String>();
		header.add("ID");
		header.add("NOME");
		header.add("DESCRIÇÃO");
		header.add("DATA DE CRIAÇÃO");
		return header;
	}
	
	@Override
	public void loadData() throws RemoteException, ServerServiceException, NotBoundException {
		setContextoEditar(false);
		atualizarCacheTodasFuncBanco();
		List<? extends BusinessEntity> perfs = Client.getServer().getPerfis();
		popularTabelaResultado(perfs);
	}

	@Override
	public boolean checkFieldsOnCreate() throws UICheckFieldException {
		String campo = this.txtNomePerfil.getText();
		if(campo == null || campo.length() <= 0) {
			throw new UICheckFieldException(Const.INFO_EMPTY_FIELD.replace("?", "nome"));
		}
		return true;
	}
	
	@Override
	public void popularTabelaResultado(List<? extends BusinessEntity> objs) {
		Vector<Vector<Object>> dadosFinal = new Vector<Vector<Object>>();
		for(Object obj : objs) {
			Perfil perf = (Perfil)obj;
			Vector<Object> linha = new Vector<Object>();
			linha.add(perf.getId());
			linha.add(perf.getName());
			linha.add(perf.getDescription());
			linha.add(perf.getDataCriacaoToString());
			dadosFinal.add(linha);
		};
		this.getTblResultado().setModel(new DefaultTableModel(dadosFinal, gerarHeaderTabelaResultado()));
		setJTableColumnInsivible(this.getTblResultado(), 0);
	}
	
	@Override
	public String converComboChoiceToDBAtributte(String cmbChoice) {
		if(cmbChoice.equals(UIEnums.FILTROS_PERFIL.Nome.toString())) return UIEnums.FILTROS_PERFIL.Nome.getValue();
		if(cmbChoice.equals(UIEnums.FILTROS_PERFIL.Descrição.toString())) return UIEnums.FILTROS_PERFIL.Descrição.getValue();
		if(cmbChoice.equals(UIEnums.FILTROS_PERFIL.Data.toString())) return UIEnums.FILTROS_PERFIL.Data.getValue();
		return "";
	}

	@Override
	public List<? extends BusinessEntity> realizarBusca(String atributo, String termo) throws RemoteException, ServerServiceException, NotBoundException {
		return Client.getServer().searchPerfis(atributo, termo);
	}

	@Override
	public void realizarDelete(Long id) throws RemoteException, ServerServiceException, NotBoundException {
		Client.getServer().deletePerfil(id);
	}
	
	@Override
	public void realizarCreate(BusinessEntity objToSave) throws RemoteException, ServerServiceException, NotBoundException {
		Client.getServer().createPerfil((Perfil)objToSave);
	}

	@Override
	public void realizarUpdate(BusinessEntity objToSave) throws RemoteException, ServerServiceException, NotBoundException {
		Client.getServer().updatePerfil((Perfil)objToSave);
	}

	@Override
	public BusinessEntity createObjToBeSaved() {
		String name = txtNomePerfil.getText();
		String desc = txtDescricao.getText();
		Perfil perf = new Perfil(null, name, desc, null);
		perf.setPermissoes(criarPermissoesConcedidas());
		return perf;
	}

	@Override
	public void setFormEdicao(int linhaSelecionada) {
		Object campo =  getTblResultado().getValueAt(linhaSelecionada, 1);
		txtNomePerfil.setText(( campo != null ? campo.toString() : ""));
		campo =  getTblResultado().getValueAt(linhaSelecionada, 2);
		txtDescricao.setText(( campo != null ? campo.toString() : ""));
		String data = getTblResultado().getValueAt(linhaSelecionada, 3).toString();
		if(!data.equals("")) setDataModelFromStringDate(dateModel, data);
		else dateModel.setSelected(false);
	}
}
