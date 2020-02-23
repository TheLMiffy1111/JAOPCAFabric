package thelm.jaopca.api.blocks;

import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.materials.IMaterial;

public interface BlockCreator {

	MaterialFormBlock create(Form form, IMaterial material, BlockFormSettings settings);
}
