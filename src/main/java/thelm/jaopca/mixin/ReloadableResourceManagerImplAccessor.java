package thelm.jaopca.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceReloadListener;
	
@Mixin(ReloadableResourceManagerImpl.class)
public interface ReloadableResourceManagerImplAccessor {

	@Accessor
	public List<ResourceReloadListener> getListeners();
}
