package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import exception.DBConnectException;
import exception.DBConsultException;
import model.Functionality;
import model.Plugin;
import model.User;

/**
 * Define a interface remota do servidor
 */
public interface ServerInterface extends Remote {

  /** nome para referenciar remotamente o servidor */
  String REFERENCE_NAME = "ServerInterface";

  int RMI_PORT = 1099;

  /**
   * Obtém todos os usuários do sistema.
   * 
   * @return lista com os usuários
   * @throws DBConnectException 
   * @throws DBConsultException 
   * @throws RemoteException .
   */
  List<User> getUsers() throws RemoteException, DBConnectException, DBConsultException;
  
  /**
   * Obtém todos os plugins do sistema.
   * 
   * @return lista com os plugins
   * @throws DBConnectException 
   * @throws DBConsultException 
   * @throws RemoteException .
   */
  List<Plugin> getPlugins() throws RemoteException, DBConnectException, DBConsultException;
  
  /**
   * Obtém todas as funcionalidades do sistema.
   * 
   * @return lista com as funcionalidades
   * @throws RemoteException .
   */
  List<Functionality> getFunctionalities() throws RemoteException;

}
