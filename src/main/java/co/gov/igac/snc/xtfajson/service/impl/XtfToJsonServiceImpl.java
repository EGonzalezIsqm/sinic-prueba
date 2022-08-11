package co.gov.igac.snc.xtfajson.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import co.gov.igac.snc.xtfajson.util.AzureStorage;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.settings.Settings;
import ch.interlis.ili2c.Ili2cException;
import ch.interlis.ili2c.Main;
import ch.interlis.ili2c.config.Configuration;
import ch.interlis.ili2c.config.FileEntry;
import ch.interlis.ili2c.config.FileEntryKind;
import ch.interlis.ili2c.generator.ImdGenerator;
import ch.interlis.ili2c.gui.UserSettings;
import ch.interlis.ili2c.metamodel.Table;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ilirepository.IliManager;
import ch.interlis.iom_j.itf.ModelUtilities;
import ch.interlis.iox.IoxException;
import ch.interlis.iox_j.IoxUtility;
import co.gov.igac.snc.xtfajson.dto.MensajeKafkaDTO;
import co.gov.igac.snc.xtfajson.dto.PeticionDTO;
import co.gov.igac.snc.xtfajson.dto.RespuestaDTO;
import co.gov.igac.snc.xtfajson.exception.ExcepcionLecturaDeArchivo;
import co.gov.igac.snc.xtfajson.exception.ExcepcionPropertiesNoExiste;
import co.gov.igac.snc.xtfajson.exception.ExcepcionesDeNegocio;
import co.gov.igac.snc.xtfajson.service.IKafkaProducerService;
import co.gov.igac.snc.xtfajson.service.IxtfToJsonService;
import co.gov.igac.snc.xtfajson.util.Propiedades;
import co.gov.igac.snc.xtfajson.util.Utilidades;

@Service
public class XtfToJsonServiceImpl implements IxtfToJsonService{

	private String fileXTF;
	private String pathXTF;
	private String out = "";
	List<String> outFiles;
	
	@Autowired
	private Propiedades propiedades;
	
	@Autowired
	Utilidades utilidades;

    @Autowired
    AzureStorage azureStorage;
	
	@Autowired
	private IKafkaProducerService producer;
	
	private final Log log = LogFactory.getLog(getClass());

	@Override
	public ResponseEntity<RespuestaDTO> convertir(PeticionDTO peticion) 
			throws ExcepcionLecturaDeArchivo, ExcepcionesDeNegocio {

        log.info("Inicio convertir xtf a json");
		Map<String, String> peticionDescargarArchivo = new HashMap<>();
		peticionDescargarArchivo.put("rutaStorage", peticion.getRutaArchivo());
		peticionDescargarArchivo.put("nombreArchivo", peticion.getNombreArchivo());
		
		//ResponseEntity<String> respuesta = utilidades.consumirApi(peticionDescargarArchivo, propiedades.getDescargarArchivo());
		/*
		if(!respuesta.getStatusCode().is2xxSuccessful()) {
			throw new ExcepcionesDeNegocio(respuesta.getBody().toString(),
					"Error consumiendo " + peticionDescargarArchivo,
					HttpStatus.CONFLICT);
		}*/
		
		//String rutaLocal = respuesta.getBody().toString();


        log.info("Descargar xtf " + peticion.getRutaArchivo() + "/" + peticion.getNombreArchivo());
        String rutaLocal = this.azureStorage.descargarArchivo(peticion.getRutaArchivo(),
                peticion.getNombreArchivo(),
                propiedades.getRutaDescarga());
		File file = new File(rutaLocal);
		
		this.fileXTF = file.getName();
		this.pathXTF = file.getPath();
		
		String uploadedFilesPath = propiedades.getUploadedFiles();

		// saca el modelo
		Map<String, String> classesModels = new HashMap<>();

		
			Path tmpDirectory = null;
			try {
				tmpDirectory = Files.createTempDirectory(Paths.get(propiedades.getUploads()), propiedades.getTmpDirectoryPrefix());
			} catch (IOException ex) {
				throw new ExcepcionLecturaDeArchivo(ex.getMessage());
			}

			ArrayList<String> iliModels = this.getIliModels(pathXTF);


            log.info("iliModels - size (" + iliModels.size() +") " + iliModels.toString() );
			iliModels.forEach((iliModel) -> {

				log.info("\n\nforEach ili: " + iliModel);
				TransferDescription td2 = this.getTansfDesc(iliModel);

				if (td2 != null) {

					this.td2imd(iliModel, td2, propiedades.getWorkingDir());
					String nameIliModel = new File(iliModel).getName();
					Map<String, String> classes = this.getClassesTransDesc(td2,
							nameIliModel.substring(0, nameIliModel.lastIndexOf('.')));
					if (!classes.isEmpty()) {
						classesModels.putAll(classes);
					}
				}
			});

			this.outFiles = this.translate(pathXTF, classesModels);

			// check generate files
			HashMap<?, ?> items = this.checkGenerateFile(outFiles);


			log.info("\n\nwriteOutIli2Json ");
			// get output to write
			out += this.writeOutIli2Json(tmpDirectory.getFileName().getName(0).toString(), fileXTF, items);

			if (out.lastIndexOf(",") != -1) {
				out = out.substring(0, out.lastIndexOf(","));
			}

			uploadedFilesPath = uploadedFilesPath + "[" + out + "]";
		

		log.info("\n\nFin -  uploadedFilesPath: " + uploadedFilesPath);
		RespuestaDTO respuestaDTO = RespuestaDTO.builder()
				.rutaOrigen(file.getParent())
				.outJson(outFiles)
				.origen(peticion.getOrigen())
				.build();

		
		eliminarArchivo(file);
		producer.enviarMensaje(new MensajeKafkaDTO("OK",respuestaDTO));
		
		return ResponseEntity.ok(respuestaDTO);
	}
	
	private void eliminarArchivo(File archivo) {
		if(archivo.exists()){
			log.info("Eliminar archivo " + archivo.getPath() + ": " + archivo.delete());
		}
	}

    private String[] getIliDirs() {

        String ili_repository = "https://repositorio.proadmintierra.info/";
        String ilidir_separator = ";";
        String default_ilidirs = propiedades.getIliDir() + ilidir_separator + ili_repository;

        return default_ilidirs.split(ilidir_separator);
    }

    private void setDefaultIli2cPathMap(Settings settings) {
        HashMap<String, String> pathmap = new HashMap<String, String>();
        pathmap.put(propiedades.getIliDir(), null);
        settings.setTransientObject(UserSettings.ILIDIRS_PATHMAP, pathmap);
    }

    private String getVersion() {
        return "4.7.7-20180208";
    }

    private List<String> getModelsXFT(String pathXTF) {
        List<String> modelsList = null;
        try {
            File fXTF = new File(pathXTF);

            if (fXTF.exists()) {
                modelsList = IoxUtility.getModels(fXTF);
            }
        } catch (IoxException e) {
            log.error(e.getMessage());
        }

        return modelsList;
    }

    private ArrayList<String> getIliModels(String pathXTF) {

        ArrayList<String> listIliFiles = new ArrayList<>();
        EhiLogger.getInstance().setTraceFilter(false);
        IliManager m = new IliManager();

        // get models and repositories
        ArrayList<String> requiredModels = new ArrayList<String>(getModelsXFT(pathXTF));
        log.info("modelos requeridos - (" + requiredModels.size() + ") " + requiredModels.toString());
        requiredModels.forEach(iliModel -> {
            log.info("Modelo - " + iliModel);
            if(!new File(iliModel).exists()){
                try {
                    log.info("  > Descargando " + iliModel );
                    this.azureStorage.descargarArchivo(propiedades.getAzureModelos(),
                            iliModel + ".ili",
                            propiedades.getIliDir());
                } catch (ExcepcionesDeNegocio e) {
                    throw new RuntimeException(e);
                }
            }
        });
        log.info("requiere modelos:  ("+requiredModels.size()+") "+requiredModels.toString());
        String[] requiredRepositories = getIliDirs();

        // Set repositories
        //m.setRepositories(new String[]{ILI_DIR, "https://repositorio.proadmintierra.info/"});
        m.setRepositories(requiredRepositories);

        try {
            Configuration config = m.getConfig(requiredModels, 0.0);
            if (config != null) {
                //ch.interlis.ili2c.Ili2c.logIliFiles(config);

                Iterator<?> filei = config.iteratorFileEntry();
                while (filei.hasNext()) {

                    FileEntry file = (FileEntry) filei.next();
                    // get ili file model
                    listIliFiles.add(file.getFilename());
                    EhiLogger.logState("ilifile <" + file.getFilename() + ">");
                }
            }
        } catch (Ili2cException e) {
            EhiLogger.logError(e);
        }

        return listIliFiles;
    }

    private List<String> executeCommand(List<String> command) {

        ArrayList<String> outCommand = new ArrayList<>();

        try {

            ProcessBuilder builder = new ProcessBuilder(command);
            //builder.inheritIO();
            //pb.directory(new File(workingDir));

            Process process = builder.start();
            process.waitFor();

            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                outCommand.add(line);
            }

        } catch (IOException | InterruptedException ex) {
            log.error(ex.getMessage());
            ex.printStackTrace();
        }

        return outCommand;
    }

    private TransferDescription getTansfDesc(String pathIli) {

        UserSettings settings = new UserSettings();
        setDefaultIli2cPathMap(settings);

        String uploadDir = FilenameUtils.getFullPath(pathIli);

        settings.setIlidirs(uploadDir + ";" + "http://models.interlis.ch/" + ";" + propiedades.getIliDir());

        Configuration config = new Configuration();
        FileEntry file = new FileEntry(pathIli, FileEntryKind.ILIMODELFILE);

        config.addFileEntry(file);
        config.setAutoCompleteModelList(true);

        TransferDescription td = Main.runCompiler(config, settings);

        return td;
    }

    private Map<String, String> getClassesTransDesc(TransferDescription td, String nameModel) {

        HashMap<?, ?> items = ModelUtilities.getTagMap(td);
        Map<String, String> mapClasses = new HashMap<String, String>();

        for (Iterator<?> it = items.entrySet().iterator(); it.hasNext();) {

            Map.Entry<String, Table> entry = (Map.Entry<String, Table>) it.next();
            String key = entry.getKey();
            //Table value = entry.getValue();

            if (key.split("\\.").length > 0) {
                if (!"INTERLIS".equals(key.split("\\.")[0])) {
                    mapClasses.put(key, nameModel);
                }
            }
        }

        return mapClasses;
    }

    private void td2imd(String pathIli, TransferDescription td) {

        String APP_NAME = "ili2json";

        Configuration config = new Configuration();
        File iliFile = new File(pathIli);
        String dirIliFile = iliFile.getParent();
        String imdFile = iliFile.getName().split(".ili")[0] + ".imd";
        String pathImdFile = dirIliFile + File.separator + imdFile;

        // set config
        config.setOutputFile(pathImdFile);

        boolean validTd = td != null ? Boolean.TRUE : Boolean.FALSE;

        // convertion to imd
        if (validTd) {
            ImdGenerator.generate(
                    new File(config.getOutputFile()),
                    td,
                    APP_NAME + "-" + getVersion());
        } else {

            // It's not possible convert file
            log.error("It's not possible convert file");
        }

    }

    private void td2imd(String pathIli, TransferDescription td, String outputDir) {

        String APP_NAME = "ili2json";
        Configuration config = new Configuration();
        File iliFile = new File(pathIli);
        String imdFile = iliFile.getName().split(".ili")[0] + ".imd";
        String pathImdFile = outputDir + File.separator + imdFile;

        // set config
        config.setOutputFile(pathImdFile);

        boolean validTd = td != null ? Boolean.TRUE : Boolean.FALSE;

        // convertion to imd
        if (validTd) {
            ImdGenerator.generate(
                    new File(config.getOutputFile()),
                    td,
                    APP_NAME + "-" + getVersion());
        } else {

            // It's not possible convert file
            log.error("It's not possible convert file");
        }

    }

    private List<String> getTablesXTF(String pathXTF) {

        List<String> parameters = new ArrayList<>();
        //parameters.add(propiedades.getOgrPath() + File.separator + "ogrinfo");
        parameters.add("ogrinfo");
        parameters.add("-q");
        parameters.add("-so");
        parameters.add(pathXTF);

        List<String> outTables = this.executeCommand(parameters);

        List<String> tables = new ArrayList<>();

        outTables.forEach((outTable) -> {
            tables.add(outTable.split(":")[1].trim());
        });

        return tables;
    }

    private String table2json(String pathXTF, String table, String model) {
        File fileXTF = new File(pathXTF);
        String workingDir = fileXTF.getParent();

        String format = "GeoJSON";
        
        String outName = workingDir + File.separator + 
                         fileXTF.getName().substring(0,fileXTF.getName().lastIndexOf('.')) + "_" + 
                         table.substring(table.lastIndexOf('.') + 1) + ".json";

        List<String> parameters = new ArrayList<>();
        parameters.add(propiedades.getOgrPath() + File.separator + "ogr2ogr");
        parameters.add("-skipfailures");
        parameters.add("-f");
        parameters.add(format);
        parameters.add(outName);
        parameters.add(pathXTF);

        if (!model.isEmpty()) {
            parameters.add(",");
            parameters.add(workingDir + File.separator + model + ".imd");
        }

        parameters.add(table);
        this.executeCommand(parameters);

        // Convertion without models
        // Alert: This can generate data loss
        return outName;
    }

    private List<String> translate(String pathXTF, Map classesModels) {

    	log.info("\n\ntranslate...");
        List<String> jsonTables = new ArrayList<>();
        List<String> tables = this.getTablesXTF(pathXTF);

    	log.info("\n\ntables: " +tables.size());
    	
        tables.forEach((table) -> {
            if (classesModels.containsKey(table)) {
                jsonTables.add(this.table2json(pathXTF, table, (String) classesModels.get(table)));
            } else {
                jsonTables.add(this.table2json(pathXTF, table, ""));
            }

    		log.info("\n\ntranslate table: "+table);
        });

		log.info("\n\ntranslate jsonTable: "+jsonTables);
        return jsonTables;
    }

    private HashMap<String, Entry> checkGenerateFile(List<String> outputFiles) {
    	

		log.info("\n\ncheckGenerateFile...");
		
        HashMap<String, Entry> checkedFiles = new HashMap<>();

        for (String outputFile : outputFiles) {

    		log.info("\n\nfor - outputFile: " + outputFile);
    		
            File outFile = new File(outputFile);

            if (outFile.exists()) {

                BufferedReader bufferedReader;
                try {
                    bufferedReader = new BufferedReader(new FileReader(outFile));
                    JsonObject json = new Gson().fromJson(bufferedReader, JsonObject.class);

                    JsonElement element = json.get("features");

                    if (!(element instanceof JsonNull)) {

                        // Get num of items
                        int countElements = json.get("features").getAsJsonArray().size();

                        if (countElements > 0) {

                            Map.Entry<Integer, String> entryProperties;

                            if (((JsonArray) element).get(0).getAsJsonObject().get("geometry") instanceof JsonNull) {
                                entryProperties = new AbstractMap.SimpleEntry<>(countElements, "Table");

                            } else {

                                JsonObject geomObjectJson = (JsonObject) ((JsonArray) element).get(0).getAsJsonObject().get("geometry");
                                String typeGeom = geomObjectJson.get("type").getAsString();

                                entryProperties = new AbstractMap.SimpleEntry<>(countElements, typeGeom);

                            }

                            checkedFiles.put(outputFile, entryProperties);

                        } else {
                            // Delete if have zero items
                            outFile.delete();
                        }
                    } else {
                        // Delete file if dont have info
                        outFile.delete();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }
            }
        }

        return checkedFiles;
    }

    private String writeOutIli2Json(String dirOutput, String filenameInput, HashMap items) {

        String out = "";

        out += "{\"result_id\":\"" + dirOutput + "\",\"transfer\":\"" + filenameInput + "\"";

        out += ",\"spatial_datasets\": [";

        String sep = "";

        for (Iterator<?> it = items.entrySet().iterator(); it.hasNext();) {

            Map.Entry entry = (Map.Entry) it.next();

            String key = (String) entry.getKey();
            File fileKey = new File(key);

            //int coutItems = (int) entry.getValue();
            Map.Entry entryProperties = (Map.Entry) entry.getValue();

            if (!"Table".equals((String) entryProperties.getValue())) {
                //String outItem = sep+"\"item\": {";
                String outItem = sep + "{";
                outItem += "\"key\" : \"" + fileKey.getName()
                        + "\", \"count\": " + (Integer) entryProperties.getKey()
                        + ", \"type\": \"" + (String) entryProperties.getValue();
                outItem += "\"}";

                out += outItem;
                sep = ",";

            }
        }

        out = out + "]";

        out += ",\"alphanumeric_datasets\": [";
        sep = "";

        for (Iterator<?> it = items.entrySet().iterator(); it.hasNext();) {

            Map.Entry entry = (Map.Entry) it.next();

            String key = (String) entry.getKey();
            File fileKey = new File(key);

            //int coutItems = (int) entry.getValue();
            Map.Entry entryProperties = (Map.Entry) entry.getValue();

            if ("Table".equals((String) entryProperties.getValue())) {
                //String outItem = sep+"\"item\": {";
                String outItem = sep + "{";
                outItem += "\"key\" : \"" + fileKey.getName()
                        + "\", \"count\": " + (Integer) entryProperties.getKey()
                        + ", \"type\": \"" + (String) entryProperties.getValue();
                outItem += "\"}";

                out += outItem;
                sep = ",";

            }
        }

        out = out + "]";
        out = out + "},";

        return out;
    }

}
