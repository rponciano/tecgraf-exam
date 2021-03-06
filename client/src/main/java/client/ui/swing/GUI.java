package client.ui.swing;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import client.ui.UIEnums;
import common.Const;
import common.exceptions.ServerServiceException;

public class GUI implements Serializable {
	private static final long serialVersionUID = -188826826138600878L;
	
	JFrame frame;
	JTabbedPane abasContainer;
	AbaGenerica abaUsuario;
	AbaGenerica abaPlugin;
	AbaGenerica abaFunc;
	AbaGenerica abaPerfil;
	
	public GUI() {
		frame = new JFrame();
		abasContainer = new JTabbedPane();
		abaUsuario = new AbaUsuario(frame);
		abaPlugin = new AbaPlugin(frame);
		abaFunc = new AbaFuncionalidade(frame);
		abaPerfil = new AbaPerfil(frame);
	}
	
	/**
	 * Inicializa a GUI
	 */
	public void init() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		abasContainer.add(abaUsuario, UIEnums.ABAS.Usuário.toString());
		abasContainer.add(abaPlugin, UIEnums.ABAS.Plugin.toString());
		abasContainer.add(abaFunc, UIEnums.ABAS.Funcionalidade.toString());
		abasContainer.add(abaPerfil, UIEnums.ABAS.Perfil.toString());
		frame.setContentPane(abasContainer);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		abasContainer.setSelectedIndex(1); // trocar para segunda aba
		
		// ao clicar na aba, verificar se ela possui tabela populada. Caso não possuir, então carregar dados
		abasContainer.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
				int index = sourceTabbedPane.getSelectedIndex();
				try {  
					/* foi removida a abordagem de não carregar caso já houvesse carga na tabela, pois
					 * se um item é excluido ou alterado as tabelas não seriam recarregadas em outras maqs. 
					 * Por exemplo: como o caso de excluir um plugin e suas funcionalidades juntos. Então 
					 * é preciso recarregar as funcs, pois foram alteradas com a remoção do plugin.
					 */
					if(sourceTabbedPane.getTitleAt(index).equals(UIEnums.ABAS.Usuário.toString())) {
						abaUsuario.loadData();
					}
					else if(sourceTabbedPane.getTitleAt(index).equals(UIEnums.ABAS.Plugin.toString())) {
						abaPlugin.loadData();
					}
					else if(sourceTabbedPane.getTitleAt(index).equals(UIEnums.ABAS.Funcionalidade.toString())) {
						abaFunc.loadData();
					}
					else if(sourceTabbedPane.getTitleAt(index).equals(UIEnums.ABAS.Perfil.toString())) {
						abaPerfil.loadData();
					}
				} 
				catch (ServerServiceException err) { exibirDialogError(err.getMessage()); } 
				catch (RemoteException err) { exibirDialogError(Const.ERROR_REMOTE_EXCEPT); } 
				catch (NotBoundException err) { exibirDialogError(Const.ERROR_NOTBOUND_EXCEPT); }
			};
		});
		
		abasContainer.setSelectedIndex(0); // voltar para primeira e, assim, disparar evento para carregar dados
	}


	/**
	 * Método para exibir DialogBox de algum <b>erro</b>.
	 * @param msg - mensagem a ser exibida no DialogBox
	 */
	private void exibirDialogError(String msg) {
		JOptionPane.showMessageDialog(frame, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}
}
