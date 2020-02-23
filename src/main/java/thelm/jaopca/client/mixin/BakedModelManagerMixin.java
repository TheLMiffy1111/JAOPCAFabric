package thelm.jaopca.client.mixin;

import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import thelm.jaopca.client.JAOPCAClient;

@Mixin(BakedModelManager.class)
public class BakedModelManagerMixin {

	@Shadow
	protected Map<Identifier, BakedModel> models;

	@Inject(method = "apply", at = @At(value = "FIELD", shift = At.Shift.AFTER,
			target = "missingModel:Lnet/minecraft/client/render/model/BakedModel;",
			opcode = Opcodes.PUTFIELD))
	public void onApply(ModelLoader loader, ResourceManager resourceManager, Profiler profiler, CallbackInfo info) {
		JAOPCAClient.INSTANCE.onModelBake(models);
	}
}
