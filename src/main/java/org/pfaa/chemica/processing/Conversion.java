package org.pfaa.chemica.processing;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.pfaa.chemica.model.Condition;
import org.pfaa.chemica.model.State;
import org.pfaa.chemica.processing.Form.Forms;

import com.google.common.collect.Sets;

public interface Conversion {
	Type getType();
	
	List<MaterialStoich<?>> getInputs();
	List<MaterialStoich<?>> getOutputs();
	
	default double getEnergy() {
		double totalStoich = this.getInputs().stream().mapToDouble((input) -> input.stoich).sum();
		return this.getInputs().stream().mapToDouble((input) -> {
			return input.material().getEnthalpyChange(this.getCondition()) * input.stoich / totalStoich;
		}).sum();
	}
	default Condition getCondition() {
		return Condition.STP;
	}
	
	default Form getOutputForm(Form inputForm) {
		if (inputForm == Forms.MILLIBUCKET) {
			/*
			 * If all inputs are fluid, then we need to decide on the output form. If the outputs
			 * are also all fluid, then we could scale so that the min coefficient is 1 mB. But it
			 * seems simpler if the input is of a standard size, that satifies the following:
			 * 1) Large enough to give enough precision for mixtures with minor components,
			 * 2) Small enough to support smaller process scales,
			 * 3) Likely corresponds to a solid output, if there are any.
			 * 
			 * We select two standard forms: dust and tiny dust. 
			 * For precision, we use dust when there are minor components.
			 * Otherwise, we use tiny dust, likely the highest resolution for processes involving solids.
			 * Even when there are no solids, consistency is good. 
			 * Both of those are likely supported by any solid.
			 */
			boolean hasMinorComponents = this.getOutputs().stream().anyMatch((output) -> output.stoich < 1);
			return hasMinorComponents ? Forms.DUST : Forms.DUST_TINY;
		}
		return inputForm;
	}
	
	default Stream<Form> getOutputForms(Form inputForm) {
		return Stream.of(this.getOutputForm(inputForm)).filter(Objects::nonNull);
	}
	
	default Stream<MaterialStoich<?>> getBonusOutputs(Form form) {
		return Stream.empty();
	}
	
	/* This assumes that there is one common form/scale across all inputs and across all outputs.
	 * That does not necessarily mean that all MaterialStacks will end up as the same form,
	 * but it does constrain this somewhat.
	 */
	
	default Stream<MaterialRecipe> getRecipes() {
		List<MaterialStoich<?>> solidInputs = this.getInputs().stream().
				filter((stoich) -> stoich.state() == State.SOLID).collect(Collectors.toList());  
		Set<Form> inputForms;
		if (solidInputs.isEmpty()) {
			inputForms = Collections.singleton(Forms.MILLIBUCKET);
		} else {
			inputForms = solidInputs.stream().map((input) -> CanonicalForms.of(input.material())).
					reduce(Sets::intersection).get();
		}
		return inputForms.stream().map((inputForm) -> {
			Stream<Form> outputForms = this.getOutputForms(inputForm);
			Stream<MaterialRecipe> recipes = outputForms.map((outputForm) -> this.getRecipe(inputForm, outputForm));
			if (this.bidirectional()) {
				recipes = Stream.concat(recipes, recipes.map(MaterialRecipe::reverse));
			}
			return recipes;
		}).reduce(Stream.empty(), Stream::concat);
	}

	default MaterialRecipe getRecipe(Form inputForm, Form outputForm) {
		Stream<MaterialStoich<?>> inputs = this.getInputs().stream();
		Stream<MaterialStoich<?>> outputs = this.getOutputs().stream();
		float formRatio = ((float)inputForm.getNumberPerBlock()) / outputForm.getNumberPerBlock();
		if (formRatio > 1) {
			inputs = inputs.map((input) -> input.scale(formRatio));
		} else if (formRatio < 1) {
			outputs = outputs.map((output) -> output.scale(1 / formRatio));
		}
		outputs = Stream.concat(outputs, this.getBonusOutputs(outputForm));
		return MaterialRecipe.converts(inputForm.of(inputs)).to(outputForm.of(outputs)).
			at(this.getCondition()).given((int)(this.getEnergy() * inputForm.scaleTo(Forms.MOLAR)));
	}
	
	default boolean bidirectional() { return false; }
	
	public interface Type {
		String name();
	}
}
