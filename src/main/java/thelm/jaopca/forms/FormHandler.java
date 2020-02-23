package thelm.jaopca.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.forms.FormRequest;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.Module;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.modules.ModuleHandler;

public class FormHandler {

	private FormHandler() {}

	private static final Logger LOGGER = LogManager.getLogger();
	private static final TreeMap<String, Form> FORMS = new TreeMap<>();
	private static final List<FormRequest> FORM_REQUESTS = new ArrayList<>();

	public static Map<String, Form> getFormMap() {
		return FORMS;
	}

	public static Collection<Form> getForms() {
		return FORMS.values();
	}

	public static Form getForm(String name) {
		return FORMS.get(name);
	}

	public static boolean containsForm(String name) {
		return FORMS.containsKey(name);
	}

	public static void collectForms() {
		for(Module module : ModuleHandler.getModuleMap().values()) {
			List<FormRequest> list = module.getFormRequests();
			if(list != null && !list.isEmpty()) {
				list.stream().filter(request->request.getModule() == module).forEach(FORM_REQUESTS::add);
			}
		}
		for(FormRequest request : FORM_REQUESTS) {
			for(Form form : request.getForms()) {
				if(FORMS.putIfAbsent(form.getName(), form) != null) {
					throw new IllegalStateException(String.format("Form name conflict: %s for modules %s and %s",
							form.getName(), FORMS.get(form.getName()).getModule().getName(), form.getModule().getName()));
					//LOGGER.fatal("Form name conflict: {} for modules {} and {}",
					//		form.getName(), FORMS.get(form.getName()).getModule().getName(), form.getModule().getName());
				}
				form.getType().addForm(form);
			}
		}
	}

	public static void computeValidMaterials() {
		for(FormRequest request : FORM_REQUESTS) {
			if(request.isGrouped()) {
				List<IMaterial> materials = MaterialHandler.getMaterials().stream().filter(request::isMaterialGroupValid).collect(Collectors.toList());
				for(Form form : request.getForms()) {
					form.setMaterials(materials);
				}
			}
			else {
				for(Form form : request.getForms()) {
					List<IMaterial> materials = MaterialHandler.getMaterials().stream().filter(form::isMaterialValid).collect(Collectors.toList());
					form.setMaterials(materials);
				}
			}
		}
	}
}
