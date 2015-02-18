/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

/**
 *
 * @author Søren
 */
public interface ClientObserver {
    
    void messageArrived(String mes);
    void usersUpdated(String users);
    void closedConnection(Boolean close);
}
