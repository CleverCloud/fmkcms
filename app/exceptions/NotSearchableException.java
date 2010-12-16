/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package exceptions;

/**
 *
 * @author judu
 */
public class NotSearchableException extends Exception {

    /**
     * Creates a new instance of <code>NotSearchableException</code> without detail message.
     */
    public NotSearchableException() {
    }


    /**
     * Constructs an instance of <code>NotSearchableException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public NotSearchableException(String msg) {
        super(msg);
    }
}
