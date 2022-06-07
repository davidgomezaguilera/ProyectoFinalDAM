package com.example.tourguide.controladores;

/**
 * Created by francis on 15/08/17.
 */

@SuppressWarnings("serial")
public class ServidorPHPException extends Exception
{
    /**
     * Constructor de la clase
     * @param message Mensaje
     */
    public ServidorPHPException(String message)
    {
        super(message);
    }
}
