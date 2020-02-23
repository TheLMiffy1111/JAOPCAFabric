package thelm.jaopca.forms;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableList;

import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.forms.FormRequest;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.Module;
import thelm.jaopca.modules.ModuleHandler;

public class FormRequestImpl implements FormRequest {

	private final Module module;
	private final List<Form> forms;
	private boolean grouped = false;

	public FormRequestImpl(Module module, Form... forms) {
		this.module = Objects.requireNonNull(module);
		this.forms = Arrays.stream(Objects.requireNonNull(forms)).
				filter(Objects::nonNull).
				filter(form->form.getModule() == module).
				map(Form::lock).collect(ImmutableList.toImmutableList());
		for(Form form : this.forms) {
			form.setRequest(this);
		}
	}

	@Override
	public Module getModule() {
		return module;
	}

	@Override
	public List<Form> getForms() {
		return forms;
	}

	@Override
	public boolean isGrouped() {
		return grouped;
	}

	@Override
	public FormRequest setGrouped(boolean grouped) {
		this.grouped = grouped;
		return this;
	}

	@Override
	public boolean isMaterialGroupValid(IMaterial material) {
		return !ModuleHandler.getModuleData(module).getRejectedMaterials().contains(material) &&
				forms.stream().filter(form->!form.skipGroupedCheck()).anyMatch(form->form.isMaterialValid(material));
	}
}
