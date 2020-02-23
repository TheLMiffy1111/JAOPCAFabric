package thelm.jaopca.api.items;

import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.materials.IMaterial;

public interface ItemCreator {

	MaterialFormItem create(Form form, IMaterial material, ItemFormSettings settings);
}
