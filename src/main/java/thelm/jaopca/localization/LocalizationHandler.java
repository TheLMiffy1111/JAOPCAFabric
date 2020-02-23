package thelm.jaopca.localization;

import java.util.Objects;
import java.util.TreeMap;

import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.LanguageDefinition;
import thelm.jaopca.api.localization.Localizer;
import thelm.jaopca.utils.MiscHelperImpl;

public class LocalizationHandler {

	private static final TreeMap<String, Localizer> LOCALIZERS = new TreeMap<>();

	public static void registerLocalizer(Localizer localizer, String... languages) {
		Objects.requireNonNull(localizer);
		for(String language : Objects.requireNonNull(languages)) {
			LOCALIZERS.put(language, localizer);
		}
	}

	public static Localizer getCurrentLocalizer() {
		String language = "en_us";
		language = MiscHelperImpl.INSTANCE.callWhenOn(EnvType.CLIENT, ()->()->{
			MinecraftClient mc = MinecraftClient.getInstance();
			if(mc != null) {
				LanguageDefinition lang = mc.getLanguageManager().getLanguage();
				if(lang != null) {
					return lang.getCode();
				}
			}
			return "en_us";
		});
		return LOCALIZERS.computeIfAbsent(language, key->DefaultLocalizer.INSTANCE);
	}
}
