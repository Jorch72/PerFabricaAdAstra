package org.pfaa.chemica.integration;

import java.util.Collections;
import java.util.List;

import org.pfaa.chemica.model.Strength;
import org.pfaa.chemica.processing.TemperatureLevel;
import org.pfaa.chemica.registration.RecipeRegistration;
import org.pfaa.chemica.registration.RecipeRegistry;
import org.pfaa.chemica.util.ChanceStack;

import cpw.mods.fml.common.Loader;
import mods.railcraft.api.crafting.IRockCrusherRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import net.minecraft.item.ItemStack;

public class RailcraftIntegration {
	
	public static void init() {
		if (Loader.isModLoaded(ModIds.RAILCRAFT)) {
			RecipeRegistration.getTarget().addRegistry(new RailcraftRecipeRegistry());
		}
	}
	
	public static class RailcraftRecipeRegistry implements RecipeRegistry {

		@Override
		public void registerGrindingRecipe(ItemStack input, ItemStack output, List<ChanceStack> secondaries,
				Strength strength) {
			IRockCrusherRecipe recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(input, true, false);
			recipe.addOutput(output, 1.0F);
			for (ChanceStack secondary : secondaries) {
				recipe.addOutput(secondary.itemStack, secondary.chance);
			}
		}

		@Override
		public void registerCrushingRecipe(ItemStack input, ItemStack output, Strength strength) {
			this.registerGrindingRecipe(input, output, Collections.<ChanceStack>emptyList(), strength);
		}

		@Override
		public void registerSmeltingRecipe(ItemStack input, ItemStack output, ItemStack flux, TemperatureLevel temp) {
			int ticks = RecipeCostUtils.blastTicksForTemperatureLevel(temp);
			RailcraftCraftingManager.blastFurnace.addRecipe(input, true, false, ticks, output);
		}

		@Override
		public void registerCastingRecipe(ItemStack input, ItemStack output, int temp) {}

		// Other machines: coke oven
	}

}