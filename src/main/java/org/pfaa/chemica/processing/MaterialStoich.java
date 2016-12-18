package org.pfaa.chemica.processing;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.pfaa.chemica.model.Condition;
import org.pfaa.chemica.model.IndustrialMaterial;
import org.pfaa.chemica.model.MaterialState;
import org.pfaa.chemica.model.State;

public class MaterialStoich<T extends IndustrialMaterial> {
	public final MaterialState<T> materialState;
	public final float stoich;
	
	protected MaterialStoich(MaterialState<T> materialState, float stoich) {
		this.materialState = materialState;
		this.stoich = stoich;
	}
	
	public static <T extends IndustrialMaterial> MaterialStoich<T> of(float stoich, State state, T material) {
		return new MaterialStoich<T>(state.of(material), stoich);
	}

	public static <T extends IndustrialMaterial> MaterialStoich<T> of(State state, T material) {
		return of(1.0F, state, material);
	}
	
	public static <T extends IndustrialMaterial> MaterialStoich<T> of(T material) {
		return of(material.getProperties(Condition.STP).state, material);
	}

	public static <T extends IndustrialMaterial> MaterialStoich<T> of(float stoich, MaterialState<T> materialState) {
		return new MaterialStoich<T>(materialState, stoich);
	}

	public static <T extends IndustrialMaterial> MaterialStoich<T> of(MaterialState<T> materialState) {
		return of(1.0F, materialState);
	}

	public static <T extends IndustrialMaterial> List<MaterialStoich<T>> of(@SuppressWarnings("unchecked") T... materials) {
		return Arrays.asList(materials).stream().map(MaterialStoich::of).collect(Collectors.toList());
	}

	public T material() {
		return this.materialState.material;
	}

	public State state() {
		return this.materialState.state;
	}
}
