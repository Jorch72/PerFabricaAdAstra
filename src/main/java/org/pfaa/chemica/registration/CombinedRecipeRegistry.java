package org.pfaa.chemica.registration;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pfaa.chemica.model.Condition;
import org.pfaa.chemica.model.Strength;
import org.pfaa.core.item.ChanceStack;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class CombinedRecipeRegistry implements RecipeRegistry {

	private Map<String,RecipeRegistry> registries = new HashMap<String,RecipeRegistry>();
	private CombinedGenericRecipeRegistry materialRegistry = new CombinedGenericRecipeRegistry();
	
	public void putRegistry(String key, RecipeRegistry registry) {
		registries.put(key, registry);
		materialRegistry.putRegistry(key, registry.getGenericRecipeRegistry());
	}
	
	public RecipeRegistry getRegistry(String key) {
		return registries.get(key);
	}
	
	public Set<String> getRegistryNames() {
		return Collections.unmodifiableSet(registries.keySet());
	}

	@Override
	public void registerGrindingRecipe(ItemStack input, ItemStack output, List<ChanceStack> secondaries,
			Strength strength) {
		for (RecipeRegistry registry : registries.values()) {
			registry.registerGrindingRecipe(input, output, secondaries, strength);
		}
	}

	@Override
	public void registerCrushingRecipe(ItemStack input, ItemStack output, ChanceStack dust, Strength strength) {
		for (RecipeRegistry registry : registries.values()) {
			registry.registerCrushingRecipe(input, output, dust, strength);
		}
	}

	@Override
	public void registerSmeltingRecipe(ItemStack input, ItemStack output, ItemStack flux, int temp) {
		for (RecipeRegistry registry : registries.values()) {
			registry.registerSmeltingRecipe(input, output, flux, temp);
		}
	}

	@Override
	public void registerCastingRecipe(ItemStack input, ItemStack output, ItemStack flux, int temp, int energy) {
		for (RecipeRegistry registry : registries.values()) {
			registry.registerCastingRecipe(input, output, flux, temp, energy);
		}
	}

	@Override
	public void registerCastingRecipe(FluidStack input, ItemStack output) {
		for (RecipeRegistry registry : registries.values()) {
			registry.registerCastingRecipe(input, output);
		}
	}

	@Override
	public void registerMeltingRecipe(ItemStack input, FluidStack output, int temp, int energy) {
		for (RecipeRegistry registry : registries.values()) {
			registry.registerMeltingRecipe(input, output, temp, energy);
		}
	}

	@Override
	public void registerFreezingRecipe(FluidStack input, ItemStack output, int temp, int energy) {
		for (RecipeRegistry registry : registries.values()) {
			registry.registerFreezingRecipe(input, output, temp, energy);
		}
	}

	@Override
	public void registerBoilingRecipe(FluidStack input, FluidStack output, int temp, int energy) {
		for (RecipeRegistry registry : registries.values()) {
			registry.registerBoilingRecipe(input, output, temp, energy);
		}
	}

	@Override
	public void registerCondensingRecipe(FluidStack input, FluidStack output, int temp, int energy) {
		for (RecipeRegistry registry : registries.values()) {
			registry.registerCondensingRecipe(input, output, temp, energy);
		}
	}

	@Override
	public void registerCoolingRecipe(FluidStack input, FluidStack output, int heat) {
		for (RecipeRegistry registry : registries.values()) {
			registry.registerCoolingRecipe(input, output, heat);
		}
	}

	@Override
	public void registerPrecipitationRecipe(FluidStack input, ItemStack output, int energy) {
		for (RecipeRegistry registry : registries.values()) {
			registry.registerPrecipitationRecipe(input, output, energy);
		}
	}

	@Override
	public void registerSmeltingRecipe(ItemStack input, FluidStack output, ItemStack flux, int temp) {
		for (RecipeRegistry registry : registries.values()) {
			registry.registerSmeltingRecipe(input, output, flux, temp);
		}
	}

	@Override
	public void registerAlloyingRecipe(ItemStack output, ItemStack base, List<ItemStack> solutes, int temp, int energy) {
		for (RecipeRegistry registry : registries.values()) {
			registry.registerAlloyingRecipe(output, base, solutes, temp, energy);
		}
	}

	@Override
	public void registerAlloyingRecipe(FluidStack output, List<FluidStack> inputs) {
		for (RecipeRegistry registry : registries.values()) {
			registry.registerAlloyingRecipe(output, inputs);
		}
	}

	@Override
	public void registerRoastingRecipe(List<ItemStack> inputs, ItemStack output, FluidStack gas, int temp) {
		for (RecipeRegistry registry : registries.values()) {
			registry.registerRoastingRecipe(inputs, output, gas, temp);
		}
	}

	@Override
	public void registerMixingRecipe(List<ItemStack> solidInputs, FluidStack fluidInput, FluidStack fluidInput2, 
			ItemStack solidOutput, FluidStack liquidOutput, FluidStack gasOutput, 
			Condition condition, Set<ItemStack> catalyst) {
		for (RecipeRegistry registry : registries.values()) {
			registry.registerMixingRecipe(solidInputs, fluidInput, fluidInput2, 
					solidOutput, liquidOutput, gasOutput, condition, catalyst);
		}
	}
	
	@Override
	public void registerMixingRecipe(List<ItemStack> inputs, ItemStack output) {
		for (RecipeRegistry registry : registries.values()) {
			registry.registerMixingRecipe(inputs, output);
		}
	}

	@Override
	public void registerMechanicalSeparationRecipe(ItemStack input, List<ChanceStack> outputs) {
		for (RecipeRegistry registry : registries.values()) {
			registry.registerMechanicalSeparationRecipe(input, outputs);
		}
	}

	@Override
	public void registerDistillationRecipe(FluidStack input, List<FluidStack> outputs, Condition condition) {
		for (RecipeRegistry registry : registries.values()) {
			registry.registerDistillationRecipe(input, outputs, condition);
		}
	}
	
	@Override
	public GenericRecipeRegistry getGenericRecipeRegistry() {
		return this.materialRegistry;
	}
}
