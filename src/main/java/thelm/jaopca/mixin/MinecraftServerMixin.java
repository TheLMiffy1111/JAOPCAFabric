package thelm.jaopca.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.JsonElement;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelGeneratorType;
import thelm.jaopca.JAOPCA;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

	@Inject(method = "upgradeWorld", at = @At("HEAD"))
	public void onUpgradeWorld(String name, CallbackInfo info) {
		JAOPCA.INSTANCE.onStartServer((MinecraftServer)(Object)this);
	}
}
