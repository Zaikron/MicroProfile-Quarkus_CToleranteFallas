package com.zaikron.controller;

import com.zaikron.model.ips;
import org.eclipse.microprofile.faulttolerance.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.net.InetAddress;
import java.net.UnknownHostException;

//@CircuitBreaker(failureRatio = 0.1, delay = 15000L) // Establece un limite de fallas, dara error si se alcanza el limite de fallas, uno de cada 10
//@Bulkhead(value = 1) //Establece numero de peticiones simultaneas
//@Timeout(value = 5000L) //Si el metodo tarda mas de 5s en responder ejecuta el error
//@Retry(maxRetries = 4) //Se ejecuta tantas a veces en un limite hasta que sea correcto, dara error si fallas en todas

@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
public class IPController {
    List<ips> ipsList = new ArrayList<ips>();
    Logger LOGGER =     Logger.getLogger("Demologger");
    int idx = 1;

    public IPController(){
        ips ip1 = new ips("www.google.com");
        ips ip2 = new ips("www.examplezz.com");
        ips ip3 = new ips("www.youtube.com");
        ipsList.add(ip1);
        ipsList.add(ip2);
        ipsList.add(ip3);
    }

    @GET
    @Retry(maxRetries = 4)
    @Fallback(fallbackMethod = "getPersonFallbackList") //Camino alternativo
    public String getPing(){
        LOGGER.info("Ejecutando ping ");
        System.out.println("Index ip: " + idx + " ");
        String ipAddress = ipsList.get(idx).getIP(); // La dirección IP que deseas hacer ping
        boolean isPing = false;
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            if(inetAddress.isReachable(5000)){
                isPing = true;
            }else{
                isPing = false;
            }
            doFail(ipAddress, isPing);
        }
        catch (UnknownHostException e) {}
        catch (Exception e) {}
        doFail(ipAddress, isPing);
        return "Haciendo ping a : " + ipAddress;
    }

    public String getPersonFallbackList(){
        LOGGER.warning("(FallBackList)");
        return "(FallBackList)No se pudo resolver la direccion IP";
    }

    public void doFail(String ipAddress, boolean ping){
        if (ping == false) {
            LOGGER.warning("(Fail)Error al hacer ping: " + ipAddress + " ");
            throw new RuntimeException("Haciendo que la implementacion falle");
        }
    }

}
