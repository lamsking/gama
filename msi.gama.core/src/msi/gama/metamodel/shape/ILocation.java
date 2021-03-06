/*******************************************************************************************************
 *
 * msi.gama.metamodel.shape.ILocation.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.metamodel.shape;

import com.vividsolutions.jts.geom.Coordinate;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gaml.types.IType;

/**
 * The class ILocation.
 *
 * @author drogoul
 * @since 15 d�c. 2011
 *
 */
@SuppressWarnings ("rawtypes")
@vars ({ @variable (
		name = IKeyword.X,
		type = IType.FLOAT,
		doc = { @doc ("Returns the x ordinate of this point") }),
		@variable (
				name = IKeyword.Y,
				type = IType.FLOAT,
				doc = { @doc ("Returns the y ordinate of this point") }),
		@variable (
				name = IKeyword.Z,
				type = IType.FLOAT,
				doc = { @doc ("Returns the z ordinate of this point") }) })
public interface ILocation extends IShape, Comparable {

	@getter (IKeyword.X)
	public abstract double getX();

	public abstract void setX(double x);

	@getter (IKeyword.Y)
	public abstract double getY();

	public abstract void setY(double y);

	// public abstract boolean equals(final Coordinate o);
	@getter (IKeyword.Z)
	public abstract double getZ();

	public abstract void setZ(double z);

	public abstract void setLocation(final double... coords);

	public abstract void add(ILocation p);

	@Override
	public abstract double euclidianDistanceTo(ILocation targ);

	@Override
	public ILocation copy(IScope scope);

	public abstract GamaPoint toGamaPoint();

	public abstract ILocation yNegated();

	public boolean equalsWithTolerance(Coordinate c, double tolerance);

	public ILocation withPrecision(int i);

}