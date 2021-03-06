/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sgcv.rest.jsf.web.cliente.extract;

/**
 *
 * @author alvarenga
 */
import com.sgcv.rest.jsf.web.model.Cliente;
import com.sgcv.rest.jsf.web.cliente.common.RestClient;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import java.io.IOException;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import com.sgcv.rest.jsf.web.cliente.util.GsonConverter;
 
import javax.ws.rs.core.MediaType;
import org.primefaces.event.FileUploadEvent;
 
/**
 * Bean for extracting document properties (metadata).
 * The next bean demonstrates how to communicate with the remote service via POST.
 * We intent to send the content of uploaded file. I use the PrimeFaces' FileUpload component,
 * so that the content can be extracted as InputStream from the listener's parameter FileUploadEvent.
 * This is not important here, you can also use any other web frameworks to get the file content
 * (also as byte array). More important is to see how to deal with RESTful Client classes
 * FormDataMultiPart and FormDataBodyPart. 
 * 
 * Last but not least, I would like to demonstrate how to send a GET request
 * with any query string (URL parameters). The next method asks the remote
 * service by URL which looks as http://localhost:8080/Rest-JSF-Web-PrimeFaces/webresources/extract?file=<some file path>
 */

/**
 * La próxima bean demuestra cómo comunicarse con el servicio remoto a través de POST.
 * Tenemos la intención de enviar el contenido del archivo cargado.
 * Utilizando el componente FileUpload de PrimeFaces ', por lo que el contenido se puede
 * extraer como InputStream del parámetro del listener FileUploadEvent. Esto no es importante en este caso,
 * también se puede utilizar cualquier otro tipo de marcos de Internet para obtener el contenido
 * del archivo (también como matriz de bytes).
 * Más importante es ver cómo hacer frente a las clases de cliente RESTful FormDataMultiPart y FormDataBodyPart.
 **/
@ManagedBean
@ViewScoped
public class ExtractBean implements Serializable {
 
    @ManagedProperty(value = "#{restClient}")
    private RestClient restClient;
 
    private String path;
 
    //Manejo de Carga de archivos
    public void handleFileUpload(FileUploadEvent event) throws IOException {
         //devuelve el nombre de archivo con la ruta completa en JSF2.0 con PrimeFaces 5.0
        String fileName = event.getFile().getFileName();
 
        FormDataMultiPart fdmp = new FormDataMultiPart();
        FormDataBodyPart fdbp = new FormDataBodyPart(FormDataContentDisposition.name("file").fileName(fileName).build(),
                event.getFile().getInputstream(), MediaType.APPLICATION_OCTET_STREAM_TYPE);
        fdmp.bodyPart(fdbp);
 
        WebResource resource = restClient.getWebResource("cliente");
        ClientResponse response = resource.accept("application/json").type(MediaType.MULTIPART_FORM_DATA).post(
                ClientResponse.class, fdmp);
 
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed service call: HTTP error code : " + response.getStatus());
        }
        // get extracted document as JSON
        String jsonExtract = response.getEntity(String.class);
 
        // convert to Document instance
        Cliente doc = GsonConverter.getGson().fromJson(jsonExtract, Cliente.class);
        // ...
        
    }
    
     //cómo enviar una petición GET con cualquier cadena de consulta (parámetros de URL)
    public void extractFile() {
        WebResource resource = restClient.getWebResource("extract");
        ClientResponse response = resource.queryParam("file", path).accept("application/json").get(
          ClientResponse.class);

        if (response.getStatus() != 200) {
         throw new RuntimeException("Failed service call: HTTP error code : " + response.getStatus());
        }

        // get extracted document as JSON
        String jsonExtract = response.getEntity(String.class);

        // convert to Document instance
        Cliente doc = GsonConverter.getGson().fromJson(jsonExtract, Cliente.class);

        //...
   }

    // getter / setter
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
 
}
