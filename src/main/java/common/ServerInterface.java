package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import exception.ServerServiceException;
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
   * Insere novo usuário no sistema.
   * 
   * @throws ServerServiceException 
   * @throws RemoteException
   */
  void createUser(User user) throws RemoteException, ServerServiceException;
  
  /**
   * Insere novo plugin no sistema.
   * 
   * @throws ServerServiceException 
   * @throws RemoteException
   */
  void createPlugin(Plugin plg) throws RemoteException, ServerServiceException;
  
  /**
   * Obtém todos os usuários do sistema.
   * 
   * @return lista com os usuários
   * @throws RemoteException .
   * @throws ServerServiceException
   */
  List<User> getUsers() throws RemoteException, ServerServiceException;
  
  /**
   * Obtém todos os plugins do sistema.
   * 
   * @return lista com os plugins
   * @throws RemoteException .
   * @throws ServerServiceException
   */
  List<Plugin> getPlugins() throws RemoteException, ServerServiceException;
  
  /**
   * Obtém todas as funcionalidades do sistema.
   * 
   * @return lista com as funcionalidades
   * @throws RemoteException .
   * @throws ServerServiceException
   */
  List<Functionality> getFunctionalities() throws RemoteException, ServerServiceException;

  

}
