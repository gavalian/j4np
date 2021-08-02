/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.utils.dsl;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This is an interface to describe commands inside
 * of a class. to be used in run-time environment
 * to execute methods from a class given string
 * representation of the command.
 * @author gavalian
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DSLCommand {
    /**
     * name of the command to be added to auto completion list.
     * @return 
     */
    String   command();
    /**
     * information about what the command does.
     * @return 
     */
    String info();
    /**
     *  This array describes default values that can be passed
     * to the command. to use defaults, in the command line
     * one should use "!" symbol.
     * @return 
     */
    String[] defaults();
    /**
     * Descriptions of the input parameters. this will be used to print
     * out help for the command as well as tell what that parameter should
     * be is there was a failure to parse it from string.
     * @return 
     */
    String[] descriptions();
}
