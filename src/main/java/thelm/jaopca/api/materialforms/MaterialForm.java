package thelm.jaopca.api.materialforms;

import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.materials.IMaterial;

public interface MaterialForm {

	Form getForm();

	IMaterial getMaterial();
}
