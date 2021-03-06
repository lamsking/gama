/*******************************************************************************************************
 *
 * msi.gaml.expressions.ConstantExpression.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.expressions;

import msi.gama.common.util.StringUtils;
import msi.gama.runtime.IScope;
import msi.gama.util.ICollector;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;

/**
 * ConstantValueExpr.
 *
 * @author drogoul 22 août 07
 */

public class ConstantExpression extends AbstractExpression {

	Object value;

	public ConstantExpression(final Object val, final IType<?> t, final String name) {
		value = val;
		type = t;
		setName(name);
	}

	public ConstantExpression(final Object val, final IType<?> t) {
		this(val, t, val == null ? "nil" : val.toString());
	}

	public ConstantExpression(final Object val) {
		this(val, GamaType.of(val));
	}

	@Override
	public Object _value(final IScope scope) {
		return value;
	}

	@Override
	public boolean isConst() {
		return true;
	}

	@Override
	public String toString() {
		return value == null ? "nil" : value.toString();
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return StringUtils.toGaml(value, includingBuiltIn);
	}

	/**
	 * @see msi.gaml.expressions.IExpression#getDocumentation()
	 */
	@Override
	public String getDocumentation() {
		return "Literal expression of type " + getGamlType().getTitle();
	}

	@Override
	public String getTitle() {
		return literalValue();
	}

	@Override
	public boolean shouldBeParenthesized() {
		return false;
	}

	/**
	 * Method collectPlugins()
	 * 
	 * @see msi.gama.common.interfaces.IGamlDescription#collectPlugins(java.util.Set)
	 */
	// @Override
	// public void collectMetaInformation(final GamlProperties meta) {
	//
	// }

	@Override
	public void collectUsedVarsOf(final SpeciesDescription species, final ICollector<VariableDescription> result) {}

}
