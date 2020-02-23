package thelm.jaopca.localization;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Language;
import thelm.jaopca.api.localization.Localizer;
import thelm.jaopca.api.materials.IMaterial;

public class DefaultLocalizer implements Localizer {

	private DefaultLocalizer() {}

	public static final DefaultLocalizer INSTANCE = new DefaultLocalizer();

	@Override
	public Text localizeMaterialForm(String formTranslationKey, IMaterial material, String overrideKey) {
		Language languageMap = Language.getInstance();
		if(languageMap.hasTranslation(overrideKey)) {
			return new TranslatableText(overrideKey);
		}
		String materialName;
		String materialKey = "jaopca.material."+material.getName();
		if(languageMap.hasTranslation(materialKey)) {
			materialName = languageMap.translate(materialKey);
		}
		else {
			materialName = splitAndCapitalize(material.getName());
		}
		return new TranslatableText(formTranslationKey, materialName);
	}

	public static String splitAndCapitalize(String underscore) {
		return Arrays.stream(StringUtils.split(underscore, '_')).map(StringUtils::capitalize).reduce((s1, s2)->s1+' '+s2).orElse("");
	}
}
