package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import client.Client;
import common.Const;
import exception.ServerServiceException;
import exception.UICheckFieldException;
import model.Plugin;
import net.miginfocom.swing.MigLayout;

public class AbaPlugin extends AbaGenerica {

	private static final long serialVersionUID = -8445952309777454337L;
	
	private JLabel lblNomePlugin = new JLabel("Plugin: ");
	private JLabel lblDescricao = new JLabel("Descrição: ");
	private JLabel lblDataCriacao = new JLabel("Data de criação: ");
	private JTextField txtNomePlugin = new JTextField(15);
	private JTextField txtDescricao = new JTextField(15);
	private JTextField txtDataCriacao = new JTextField(15);

	public AbaPlugin(JFrame parentFrame) {
		super(parentFrame);
		initPnlForm();
		setContextoEditar(false);
		
		// iniciando listeners
		ListSelectionListener selectItemList = new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				int linhaSelecionada = getTblResultado().getSelectedRow();
				if (linhaSelecionada > -1) {
					txtNomePlugin.setText(getTblResultado().getValueAt(linhaSelecionada, 0).toString());
					txtDescricao.setText(getTblResultado().getValueAt(linhaSelecionada, 1).toString());
					txtDataCriacao.setText(getTblResultado().getValueAt(linhaSelecionada, 2).toString());
					setContextoEditar(true);
				};
			}
		};
		ActionListener saveClick = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				/* 	se o botão 'novo' estiver habilitado, então é pq não foi clickado e,
					consequentemente, não representa um novo item, mas sim um update. */
				if(getBtnNovo().isEnabled()) {
					//Client.getServer().
				} else {
					try {
						if(checkFieldsOnCreate()) {
							Plugin plg = new Plugin();
							plg.setNome(txtNomePlugin.getText());
							plg.setDescricao(txtDescricao.getText());
							plg.setDataCriacao(txtDataCriacao.getText());
							Client.getServer().createPlugin(plg);
							loadData();
							getBtnNovo().setEnabled(true);
						};
					}
					catch (UICheckFieldException err) { exibirDialogInfo(err.getMessage()); }
					catch (ServerServiceException err) { exibirDialogError(err.getMessage()); } 
					catch (RemoteException err) { exibirDialogError(Const.ERROR_REMOTE_EXCEPT); } 
					catch (NotBoundException err) { exibirDialogError(Const.ERROR_NOTBOUND_EXCEPT); }
				}
			}
		};
		initListeners(selectItemList, saveClick);
	}
	
	private void initPnlForm() {
		JPanel pnlForm = new JPanel(new MigLayout("","[right][grow]",""));
		pnlForm.add(lblNomePlugin);
		pnlForm.add(txtNomePlugin, "wrap, growx");
		pnlForm.add(lblDescricao);
		pnlForm.add(txtDescricao, "wrap, growx");
		pnlForm.add(lblDataCriacao);
		pnlForm.add(txtDataCriacao, "wrap, growx");		
		registerForm(pnlForm);
	}
	
	@Override
	public void setContextoEditar(boolean setar) {
		super.setContextoEditar(setar);
		txtNomePlugin.setEditable(setar);
		txtDescricao.setEditable(setar);
		txtDataCriacao.setEditable(false);
	}
	
	@Override
	public Vector<String> createItemsCmbConsulta() {
		Vector<String> out = new Vector<String>();
		out.add(FILTROS_PLUGIN.Nome.toString());
		out.add(FILTROS_PLUGIN.Descrição.toString());
		out.add(FILTROS_PLUGIN.Data.toString());
		return out;
	}
	
	@Override
	public Vector<String> gerarHeader() {
		Vector<String> header = new Vector<String>();
		header.add("NOME");
		header.add("DESCRIÇÃO");
		header.add("DATA DE CRIAÇÃO");
		return header;
	}
	
	/**
	 * Método para popular tabela de resultados de busca com lista de plugins
	 * @param plugins - Lista contendo os plugins a serem apresentados na tabela
	 * @param tipo - tipo para gerar Header da tabela
	 */
	public void popularTabelaResultado(List<Plugin> plugins) {
		Vector<Vector<Object>> dadosFinal = new Vector<Vector<Object>>();
		Vector<Object> linha;
		for(Plugin plg : plugins) {
			linha = new Vector<Object>();
			linha.add(plg.getNome());
			linha.add(plg.getDescricao());
			linha.add(plg.getDataCriacao());
			dadosFinal.add(linha);
		};
		this.getTblResultado().setModel(new DefaultTableModel(dadosFinal, gerarHeader()));
	}

	@Override
	public void loadData() throws RemoteException, ServerServiceException, NotBoundException {
		setContextoEditar(false);
		List<Plugin> plugins = Client.getServer().getPlugins();
		popularTabelaResultado(plugins);
		
	}

	@Override
	public boolean checkFieldsOnCreate() throws UICheckFieldException {
		String campo;
		campo = this.txtNomePlugin.getText();
		if(campo == null || campo.length() <= 0) {
			throw new UICheckFieldException(Const.INFO_EMPTY_FIELD.replace("?", "nome"));
		}
		return true;
	}	
}
