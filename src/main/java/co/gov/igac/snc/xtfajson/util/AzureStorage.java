package co.gov.igac.snc.xtfajson.util;

import co.gov.igac.snc.xtfajson.exception.ExcepcionesDeNegocio;
import com.azure.core.http.rest.PagedIterable;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.storage.file.datalake.*;
import com.azure.storage.file.datalake.models.ListPathsOptions;
import com.azure.storage.file.datalake.models.PathItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class AzureStorage {

    private final Log log = LogFactory.getLog(getClass());

    private Propiedades prop;

    private DataLakeFileSystemClient fileSystemClient;

    @Autowired
    public AzureStorage(Propiedades prop) {
        this.prop = prop;
        this.getDataLakeServiceClient();
    }

    private void getDataLakeServiceClient() {

        String endpoint = "https://" + prop.getAccountName() + ".dfs.core.windows.net";

        ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(prop.getClientId())
                .clientSecret(prop.getClientSecret())
                .tenantId(prop.getTenantId())
                .build();

        DataLakeServiceClientBuilder builder = new DataLakeServiceClientBuilder();

        this.fileSystemClient = builder.credential(clientSecretCredential)
                .endpoint(endpoint)
                .buildClient()
                .getFileSystemClient(prop.getContenedor());
    }

    public Boolean subirArchivoGrande(String rutaArchivo, String rutaStorage, String nombreArchivo)
            throws  ExcepcionesDeNegocio {

        if(!new File(rutaArchivo).exists()) {
            throw new ExcepcionesDeNegocio("El archivo no extiste: " + rutaArchivo,
                    "Archivo inexistente",
                    HttpStatus.CONFLICT
            );
        }

        DataLakeFileClient fileClient = fileSystemClient.getDirectoryClient(rutaStorage).getFileClient(nombreArchivo);

        if(fileClient.exists()) {
            throw new ExcepcionesDeNegocio("Ya existe el archivo: " + fileClient.getFilePath(),
                    "Archivo existente",
                    HttpStatus.CONFLICT
            );
        }



        fileClient.uploadFromFile(rutaArchivo);

        return fileClient.exists();

    }



    public String descargarArchivo(String rutaStorage, String nombreArchivo, String rutaDescarga)
            throws  ExcepcionesDeNegocio {

        DataLakeFileClient fileClient = fileSystemClient.getDirectoryClient(rutaStorage)
                .getFileClient(nombreArchivo);

        if(!fileClient.exists()) {
            return "";
            /*throw new ExcepcionesDeNegocio("El archivo no existe en azureStorage: " + fileClient.getFilePath(),
                    "Archivo inexistente",
                    HttpStatus.CONFLICT
            );*/
        }

        File file = new File(rutaDescarga + nombreArchivo);
        if(file.exists()) {
            return file.getPath();
        }
        OutputStream targetStream;
        try {
            targetStream = new FileOutputStream(file);
            fileClient.read(targetStream);
            targetStream.close();
        } catch (FileNotFoundException ex) {
            log.error(ex);
            throw new ExcepcionesDeNegocio("Error al descargar archivo " + ex.getMessage(), "Error al descargar archivo", HttpStatus.CONFLICT);
        } catch (IOException ex) {
            log.error(ex);
            throw new ExcepcionesDeNegocio("Error al descargar archivo " + ex.getMessage(), "Error al descargar archivo", HttpStatus.CONFLICT);
        }
        return file.getPath();
    }
}
