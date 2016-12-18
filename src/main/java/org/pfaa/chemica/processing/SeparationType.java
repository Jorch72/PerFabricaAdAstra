package org.pfaa.chemica.processing;

import org.pfaa.chemica.model.Mixture;
import org.pfaa.chemica.model.State;

public interface SeparationType extends MassTransferType<Mixture,Separation> { 
	enum SeparationAxis {
		DENSITY,
		MOLECULAR_SIZE,
		PARTICLE_SIZE,
		VAPORIZATION_POINT,
		MELTING_POINT,
		SOLUBILITY,
		ADSORPTIVITY,
		CONDUCTIVITY,
		MAGNETIC_SUSCEPTIBILITY
		;
	}
	
	SeparationType.SeparationAxis getSeparationAxis();
	
	State getInputState();
	State getAddedState();
	State getSeparatedState();
	State getResidualState();
	
	/*
	 * Most of these techniques imply phase segregation, i.e., they yield
	 * separate streams, not just a heterogeneous (multi-phase) mixture. That often but
	 * not always requires a (mechanical) segregation step that is so trivial as to not warrant
	 * explicit description. Thus, it usually makes sense to describe a separation as going
	 * from a single input stream to multiple output streams, but we will still provide
	 * operations for mechanical phase segregation. A notable exception is solid + liquid
	 * mixtures; precipitating a solid is not enough -- it must be dried. Thus, the solid is
	 * always a mixture with some liquid.  
	*/
	
	enum SeparationTypes implements SeparationType {
		// These state transitions can obviously occur by either temperature or pressure changes
		CONDENSATION(SeparationAxis.VAPORIZATION_POINT, State.GAS, State.LIQUID),
		VAPORIZATION(SeparationAxis.VAPORIZATION_POINT, State.LIQUID, State.GAS),
		DESUBLIMATION(SeparationAxis.VAPORIZATION_POINT, State.GAS, State.SOLID),
		SUBLIMATION(SeparationAxis.VAPORIZATION_POINT, State.SOLID, State.GAS),
		// Distillation combines vaporization and condensation steps, but we model it as one
		DISTILLATION(SeparationAxis.VAPORIZATION_POINT, State.LIQUID, State.LIQUID),
		DRYING(SeparationAxis.VAPORIZATION_POINT, State.LIQUID, null, State.SOLID, State.GAS),
		FREEZING(SeparationAxis.MELTING_POINT, State.LIQUID, State.SOLID),
		MELTING(SeparationAxis.MELTING_POINT, State.SOLID, State.LIQUID),
		
		ABSORPTION(SeparationAxis.SOLUBILITY, State.GAS, State.LIQUID, State.LIQUID, State.GAS),
		STRIPPING(SeparationAxis.SOLUBILITY, State.LIQUID, State.GAS, State.GAS, State.LIQUID),
		LIQUID_LIQUID_EXTRACTION(SeparationAxis.SOLUBILITY, State.LIQUID, State.LIQUID, State.LIQUID, State.LIQUID),
		LEACHING(SeparationAxis.SOLUBILITY, State.SOLID, State.LIQUID, State.AQUEOUS, State.SOLID),
		PRECIPITATION(SeparationAxis.SOLUBILITY, State.LIQUID, State.SOLID),
		DEGASIFICATION(SeparationAxis.SOLUBILITY, State.LIQUID, State.GAS),
		/* Froth flotation: 
		 * Concentrating sulfide ores by froth flotation requires first adsorbing the sulfide mineral
		 * to a "collector", so that the complex has a non-polar surface and thus is floatable.
		 */
		FLOTATION(SeparationAxis.SOLUBILITY, State.LIQUID, State.GAS, State.SOLID, State.LIQUID),
		
		LIQUID_ADSORPTION(SeparationAxis.ADSORPTIVITY, State.LIQUID, State.LIQUID, State.LIQUID, State.LIQUID),
		GAS_ADSORPTION(SeparationAxis.ADSORPTIVITY, State.GAS, State.LIQUID, State.LIQUID, State.GAS),
		LIQUID_CHROMATOGRAPHY(SeparationAxis.ADSORPTIVITY, State.LIQUID, State.LIQUID, State.LIQUID, State.LIQUID),
		GAS_CHROMATOGRAPHY(SeparationAxis.ADSORPTIVITY, State.GAS, State.GAS, State.GAS, State.GAS),
		PRESSURE_SWING_ADSPORPTION(SeparationAxis.ADSORPTIVITY, State.GAS, State.GAS),
		
		/* Molecular filters via pressure differential across membrane */
		REVERSE_OSMOSIS(SeparationAxis.MOLECULAR_SIZE, State.LIQUID, State.LIQUID),
		GAS_PERMEATION(SeparationAxis.MOLECULAR_SIZE, State.GAS, State.GAS),
		
		/* Cyclones, spirals, etc */
		GAS_GRAVITY(SeparationAxis.DENSITY, State.GAS, State.GAS),
		LIQUID_GRAVITY(SeparationAxis.DENSITY, State.LIQUID, State.LIQUID),
		/* Jigs, shaking tables */
		SOLID_GRAVITY(SeparationAxis.DENSITY, State.SOLID, State.SOLID),
		
		/* Vapor-liquid separation vessels */
		LIQUID_FROM_GAS(SeparationAxis.DENSITY, State.GAS, State.LIQUID),
		GAS_FROM_LIQUID(SeparationAxis.DENSITY, State.LIQUID, State.GAS),
		
		/* Sedimentation, followed by physical phase separation */
		LIQUID_DECANTATION(SeparationAxis.DENSITY, State.LIQUID, State.LIQUID),
		SEDIMENTARY_DECANTATION(SeparationAxis.DENSITY, State.LIQUID, State.SOLID),
		
		LIQUID_FILTRATION(SeparationAxis.PARTICLE_SIZE, State.LIQUID, State.SOLID),
		GAS_FILTRATION(SeparationAxis.PARTICLE_SIZE, State.GAS, State.SOLID),
		
		ELECTROSTATIC(SeparationAxis.CONDUCTIVITY, State.SOLID, State.SOLID),
		MAGNETIC(SeparationAxis.MAGNETIC_SUSCEPTIBILITY, State.SOLID, State.SOLID);
		
		private SeparationAxis separationAxis;	
		private State inputState, addedState, separatedState, residualState;
		
		private SeparationTypes(SeparationAxis separationAxis, State inputState) {
			this(separationAxis, inputState, inputState);
		}
		
		private SeparationTypes(SeparationAxis separationAxis, State inputState, State separatedState) {
			this(separationAxis, inputState, null, separatedState, inputState);
		}
		
		private SeparationTypes(SeparationAxis separationAxis,
				State inputState, State addedState, State separatedState, State residualState) {
			this.separationAxis = separationAxis;
			this.inputState = inputState;
			this.addedState = addedState;
			this.separatedState = separatedState;
			this.residualState = residualState;
		}
		
		@Override
		public SeparationAxis getSeparationAxis() {
			return this.separationAxis;
		}
		
		@Override
		public State getInputState() {
			return this.inputState;
		}

		@Override
		public State getSeparatedState() {
			return this.separatedState;
		}

		@Override
		public State getResidualState() {
			return this.residualState;
		}

		@Override
		public State getAddedState() {
			return this.addedState;
		}
		
		@Override
		public Separation of(MaterialStoich<Mixture> input) {
			return new Separation(this, input);
		}
	}
}