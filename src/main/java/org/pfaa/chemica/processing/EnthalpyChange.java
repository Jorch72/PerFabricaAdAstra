package org.pfaa.chemica.processing;

import java.util.Collections;
import java.util.List;

import org.pfaa.chemica.model.Condition;
import org.pfaa.chemica.model.State;

public class EnthalpyChange implements UnitOperation {

	private MaterialStoich<?> input;
	private MaterialStoich<?> output;
	
	public EnthalpyChange(MaterialStoich<?> input) {
		this.input = input;
	}

	public EnthalpyChange yields(MaterialStoich<?> output) {
		this.output = output;
		return this;
	}
	
	public MaterialStoich<?> getInput() {
		return this.input;
	}

	public MaterialStoich<?> getOutput() {
		return this.output;
	}

	@Override
	public List<MaterialStoich<?>> getInputs() {
		return Collections.singletonList(this.getInput());
	}

	@Override
	public List<MaterialStoich<?>> getOutputs() {
		return Collections.singletonList(this.getOutput());
	}

	@Override
	public double getEnergy() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Condition getCondition() {
		// TODO Auto-generated method stub
		return null;

	}
	
	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public static interface Type extends UnitOperation.Type {
		enum Direction {
			GAIN,
			LOSS;
		}
		enum Axis {
			TEMPERATURE,
			PRESSURE,
			STATE;
		}
		
		enum EnthalpyChangeTypes implements Type {
			GAS_HEATING(Axis.TEMPERATURE, State.GAS, Direction.GAIN, State.GAS),
			GAS_COOLING(Axis.TEMPERATURE, State.GAS, Direction.LOSS, State.GAS),
			LIQUID_HEATING(Axis.TEMPERATURE, State.LIQUID, Direction.GAIN, State.LIQUID),
			LIQUID_COOLING(Axis.TEMPERATURE, State.LIQUID, Direction.LOSS, State.LIQUID),
			SOLID_HEATING(Axis.TEMPERATURE, State.SOLID, Direction.GAIN, State.SOLID),
			SOLID_COOLING(Axis.TEMPERATURE, State.SOLID, Direction.LOSS, State.SOLID),
			
			COMPRESSION(Axis.PRESSURE, State.GAS, Direction.GAIN, State.GAS),
			DECOMPRESSION(Axis.PRESSURE, State.GAS, Direction.LOSS, State.GAS),
			
			CONDENSATION(Axis.STATE, State.GAS, Direction.LOSS, State.LIQUID),
			VAPORIZATION(Axis.STATE, State.LIQUID, Direction.GAIN, State.GAS),
			FREEZING(Axis.STATE, State.LIQUID, Direction.LOSS, State.SOLID),
			MELTING(Axis.STATE, State.SOLID, Direction.GAIN, State.LIQUID),
			SUBLIMATION(Axis.STATE, State.SOLID, Direction.GAIN, State.GAS),
			DESUBLIMATION(Axis.STATE, State.GAS, Direction.LOSS, State.SOLID);
		
			private State inputState, outputState;
			private Direction heatChange;
			private Axis axis;
			
			private EnthalpyChangeTypes(Axis axis, State inputState, Direction heatChange, State outputState) {
				this.axis = axis;
				this.inputState = inputState;
				this.heatChange = heatChange;
				this.outputState = outputState;
			}
			
			@Override
			public Axis getAxis() {
				return this.axis;
			}
			
			@Override
			public State getInputState() {
				return this.inputState;
			}
		
			@Override
			public State getOutputState() {
				return this.outputState;
			}
		
			@Override
			public Direction getDirection() {
				return this.heatChange;
			}
		}

		public Type.Axis getAxis();
		public State getInputState();
		public State getOutputState();
		public Type.Direction getDirection();
	}
}
