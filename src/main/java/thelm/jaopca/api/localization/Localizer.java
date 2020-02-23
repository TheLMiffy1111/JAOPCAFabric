package thelm.jaopca.api.localization;

import net.minecraft.text.Text;
import thelm.jaopca.api.materials.IMaterial;

public interface Localizer {

	Text localizeMaterialForm(String formTranslationKey, IMaterial material, String overrideKey);
}
