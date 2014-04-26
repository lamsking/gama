/*********************************************************************************************
 * 
 * 
 * 'TextLayer.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.outputs.layers;

import java.awt.Color;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.outputs.layers.*;
import msi.gama.runtime.IScope;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
public class TextLayer extends AbstractLayer {

	public TextLayer(final ILayerStatement layer) {
		super(layer);
	}

	@Override
	public void privateDrawDisplay(final IScope scope, final IGraphics g) {
		TextLayerStatement model = (TextLayerStatement) this.definition;
		String text = model.getText();
		Color color = model.getColor();
		String f = model.getFontName();
		Integer s = model.getStyle();
		g.drawString(text, color, null, null, f, s, null, true);
	}

	@Override
	public String getType() {
		return "Text layer";
	}

}
