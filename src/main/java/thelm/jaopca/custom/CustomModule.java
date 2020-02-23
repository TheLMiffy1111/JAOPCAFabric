package thelm.jaopca.custom;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import thelm.jaopca.api.config.DynamicSpecConfig;
import thelm.jaopca.api.forms.FormRequest;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.Module;
import thelm.jaopca.api.modules.ModuleData;

public class CustomModule implements Module {

	private static final Logger LOGGER = LogManager.getLogger();
	public static CustomModule instance;

	private Gson gson = new Gson();
	private final List<BiConsumer<IMaterial, DynamicSpecConfig>> customConfigDefiners = new ArrayList<>();
	private final List<FormRequest> formRequests = new ArrayList<>();

	public CustomModule() {
		if(instance == null) {
			instance = this;
		}
	}

	@Override
	public String getName() {
		return "custom";
	}

	public void setGson(Gson gson) {
		this.gson = gson;
	}

	public void addCustomConfigDefiner(BiConsumer<IMaterial, DynamicSpecConfig> customConfigDefiner) {
		customConfigDefiners.add(customConfigDefiner);
	}

	public void setCustomFormConfigFile(File customFormConfigFile) {
		formRequests.clear();
		try(InputStreamReader reader = new InputStreamReader(new FileInputStream(customFormConfigFile), StandardCharsets.UTF_8)) {
			FormRequest[] requests = gson.fromJson(reader, FormRequest[].class);
			if(requests != null) {
				Collections.addAll(formRequests, requests);
			}
		}
		catch(Exception e) {
			LOGGER.error("Unable to read custom json", e);
		}
	}

	@Override
	public List<FormRequest> getFormRequests() {
		return formRequests;
	}

	@Override
	public void defineMaterialConfig(ModuleData moduleData, Map<IMaterial, DynamicSpecConfig> configs) {
		for(BiConsumer<IMaterial, DynamicSpecConfig> customConfigDefiner : customConfigDefiners) {
			for(IMaterial material : moduleData.getMaterials()) {
				customConfigDefiner.accept(material, configs.get(material));
			}
		}
	}
}
