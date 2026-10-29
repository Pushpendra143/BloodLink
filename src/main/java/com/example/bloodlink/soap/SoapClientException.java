package com.example.bloodlink.soap;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

// becomes a SOAP Fault (Client) with the message as the fault string
@SoapFault(faultCode = FaultCode.CLIENT)
public class SoapClientException extends RuntimeException {

    public SoapClientException(String message) {
        super(message);
    }
}
