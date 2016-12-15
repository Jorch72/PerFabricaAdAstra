package org.pfaa.chemica.model;


public class MixtureComponent {
	public final IndustrialMaterial material;
	public final double weight; // volume fraction
	
	public MixtureComponent(IndustrialMaterial material, double weight) {
		this.material = material;
		this.weight = weight;
	}
	
	public IndustrialMaterial getMaterial() {
		return this.material;
	}
	
	public MixtureComponent concentrate(double factor) {
		return new MixtureComponent(this.material, this.weight * factor);
	}
	
	public String toString() {
		return this.weight + " * " + this.material.name();
	}
}
