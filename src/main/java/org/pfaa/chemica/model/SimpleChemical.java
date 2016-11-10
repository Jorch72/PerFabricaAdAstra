package org.pfaa.chemica.model;

import java.awt.Color;

import org.pfaa.chemica.model.ChemicalStateProperties.Aqueous;
import org.pfaa.chemica.model.ChemicalStateProperties.Gas;
import org.pfaa.chemica.model.ChemicalStateProperties.Liquid;
import org.pfaa.chemica.model.ChemicalStateProperties.Solid;
import org.pfaa.chemica.model.Compound.Compounds;


public class SimpleChemical implements Chemical {

	private Formula formula;
	private String oreDictKey;
	private Fusion fusion;
	private Vaporization vaporization;
	
	private ChemicalStateProperties solid;
	private ChemicalStateProperties liquid;
	private ChemicalStateProperties gas;
	private ChemicalStateProperties aqueous;
	
	public SimpleChemical(Formula formula, String oreDictKey, Solid solid) {
		this(formula, oreDictKey, solid, null, null, null, null, null);
	}
	public SimpleChemical(Formula formula, Aqueous aqueous) {
		this(formula, null, null, null, null, null, null, aqueous);
	}
	public SimpleChemical(Formula formula, String oreDictKey, Solid solid, Fusion fusion, 
			Liquid liquid,	Vaporization vaporization, Gas gas) {
		this(formula, oreDictKey, solid, fusion, liquid, vaporization, gas, null);
	}
	
	public SimpleChemical(Formula formula, String oreDictKey, Solid solid, Fusion fusion, 
			Liquid liquid,	Vaporization vaporization, Gas gas, Aqueous aqueous) 
	{
		this.formula = formula;
		this.oreDictKey = oreDictKey == null ? formula.toString() : oreDictKey;
		this.solid = solid;
		this.fusion = fusion;
		this.liquid = liquid;
		this.vaporization = vaporization;
		this.gas = gas == null ? null : new Gas(gas, formula.getMolarMass());
		this.aqueous = aqueous == null ? this.inferAqueous() : aqueous;
	}
	
	@Override
	public String getOreDictKey() {
		return oreDictKey;
	}

	@Override
	public Fusion getFusion() {
		return fusion;
	}

	@Override
	public Vaporization getVaporization() {
		return vaporization;
	}

	@Override
	public Formula getFormula() {
		return formula;
	}

	private State getStateForCondition(Condition condition) {
		if (condition.aqueous && this.isWaterSoluble(condition)) {
			return State.AQUEOUS;
		} else if (this.vaporization != null && condition.temperature >= this.vaporization.getTemperature(condition.pressure)) {
			return State.GAS;
		} else if (this.fusion != null && condition.temperature >= this.fusion.getTemperature()) {
			return State.LIQUID;
		} else {
			return State.SOLID;
		}
	}
	
	@Override
	public ChemicalConditionProperties getProperties(Condition condition) {
		State state = this.getStateForCondition(condition);
		return this.getProperties(condition, state);
	}
	
	@Override
	public ChemicalConditionProperties getProperties(Condition condition, State state) {
		return new ChemicalConditionProperties(this.getStateProperties(state), condition);
	}

	private ChemicalStateProperties getStateProperties(State state) {
		switch(state) {
		case SOLID:
			return solid;
		case LIQUID:
			return liquid;
		case GAS:
			return gas;
		case AQUEOUS:
			return aqueous;
		default:
			throw new IllegalArgumentException("Unknown state: " + state);
		}
	}
	
	@Override
	public String name() {
		return formula.toString();
	}
	
	@Override
	public Mixture mix(IndustrialMaterial material, double weight) {
		return new SimpleMixture(this).mix(material, weight);
	}
	
	private boolean isWaterSoluble(Condition condition) {
		Reaction dissolution = this.getDissolution();
		return dissolution != null && dissolution.isSpontaneous(condition);
	}
	
	public Reaction getDissolution() {
		return this.aqueous == null ? null :  
				Reaction.inWaterOf(1, this, State.SOLID).yields(1, this, State.AQUEOUS);
	}
	
	private Reaction getDissociation() {
		Formula.Part cation = this.getFormula().getFirstPart();
		Formula.Part anion = this.getFormula().getLastPart();
		boolean simpleSalt = cation.ion != null && anion.ion != null && this.getFormula().getParts().size() == 2;
		if (!simpleSalt) {
			return null;
		}
		return Reaction.inWaterOf(1, this, State.SOLID).
				yields(cation.stoichiometry, cation.ion).
				and(anion.stoichiometry, anion.ion);
	}
	
	private boolean isWaterSolubleSalt() {
		Reaction dissolution = this.getDissociation();
		if (dissolution == null) {
			return false;
		}
		int et = dissolution.getEquilibriumTemperature();
		return et > Compounds.H2O.getFusion().getTemperature() && et < Compounds.H2O.getVaporization().getTemperature();
	}
	
	private Aqueous inferAqueous() {
		if (!isWaterSolubleSalt()) {
			return null;
		}
		Ion cation = this.getFormula().getCation();
		Ion anion = this.getFormula().getAnion();
		ChemicalConditionProperties cationProps = cation.getProperties(Condition.STP);
		ChemicalConditionProperties anionProps = anion.getProperties(Condition.STP);
		Color color = cationProps.color;
		if (color == null) 
			color = anionProps.color;
		double enthalpy = cationProps.enthalpy + anionProps.enthalpy;
		double entropy = cationProps.entropy + anionProps.entropy;
		Thermo thermo = new Thermo(enthalpy, entropy);
		Hazard hazard = this.getStateProperties(State.SOLID).getHazard();
		return new Aqueous(color, thermo, hazard);
	}
}
