package org.pfaa.chemica.model;

import java.util.Arrays;
import java.util.List;

public class Extraction {
	public final IndustrialMaterial extractant;
	public final Mixture extract;
	public final Mixture residuum;
	
	public Extraction(IndustrialMaterial extractant, Mixture extract, Mixture residuum) {
		super();
		this.extract = extract;
		this.residuum = residuum;
		this.extractant = extractant;
	}
	
	public List<Mixture> getProducts() {
		return Arrays.asList(this.residuum, this.extract);
	}
}
