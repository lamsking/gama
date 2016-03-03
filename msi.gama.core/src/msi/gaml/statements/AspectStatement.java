/*********************************************************************************************
 *
 *
 * 'AspectStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.statements;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import gnu.trove.impl.Constants;
import gnu.trove.map.hash.TObjectIntHashMap;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.*;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gaml.descriptions.IDescription;
import msi.gaml.operators.Cast;
import msi.gaml.statements.draw.ShapeDrawingAttributes;
import msi.gaml.types.*;

@symbol(name = { IKeyword.ASPECT }, kind = ISymbolKind.BEHAVIOR, with_sequence = true, unique_name = true, concept = { IConcept.DISPLAY })
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.MODEL })
@facets(
	value = { @facet(name = IKeyword.NAME,
	type = IType.ID,
	optional = true,
	doc = @doc("identifier of the aspect (it can be used in a display to identify which aspect should be used for the given species). Two special names can also be used: 'default' will allow this aspect to be used as a replacement for the default aspect defined in preferences; 'highlighted' will allow the aspect to be used when the agent is highlighted as a replacement for the default (application of a color)")) },
	omissible = IKeyword.NAME)
@doc(
	value = "Aspect statement is used to define a way to draw the current agent. Several aspects can be defined in one species. It can use attributes to customize each agent's aspect. The aspect is evaluate for each agent each time it has to be displayed.",
	usages = { @usage(value = "An example of use of the aspect statement:",
	examples = { @example(value = "species one_species {", isExecutable = false),
		@example(value = "	int a <- rnd(10);", isExecutable = false),
		@example(value = "	aspect aspect1 {", isExecutable = false),
		@example(value = "		if(a mod 2 = 0) { draw circle(a);}", isExecutable = false),
		@example(value = "		else {draw square(a);}", isExecutable = false),
		@example(value = "		draw text: \"a= \" + a color: #black size: 5;", isExecutable = false),
		@example(value = "	}", isExecutable = false), @example(value = "}", isExecutable = false) }) })
public class AspectStatement extends AbstractStatementSequence {

	boolean isHighlightAspect;

	static final TObjectIntHashMap SHAPES = new TObjectIntHashMap() {

		{
			put("circle", 1);
			put("square", 2);
			put("triangle", 3);
			put("sphere", 4);
			put("cube", 5);
			put("point", 6);
		}
	};

	// public static IExecutable HIGHLIGHTED_ASPECT = new IExecutable() {
	//
	// @Override
	// public Rectangle2D executeOn(final IScope scope) throws GamaRuntimeException {
	// IAgent agent = scope.getAgentScope();
	// if ( agent != null && !agent.dead() ) {
	// final IGraphics g = scope.getGraphics();
	// if ( g == null ) { return null; }
	// try {
	// agent.acquireLock();
	// // if ( agent.dead() ) { return null; }
	// // Normally always highlighted
	// if ( agent == scope.getGui().getHighlightedAgent() ) {
	// g.beginHighlight();
	// }
	// final Color c = GamaPreferences.CORE_HIGHLIGHT.getValue();
	// String defaultShape = GamaPreferences.CORE_SHAPE.getValue();
	// int index = SHAPES.get(defaultShape);
	// IShape ag;
	//
	// if ( index != Constants.DEFAULT_INT_NO_ENTRY_VALUE ) {
	// Double defaultSize = GamaPreferences.CORE_SIZE.getValue();
	// ILocation point = agent.getLocation();
	//
	// switch (SHAPES.get(defaultShape)) {
	// case 1:
	// ag = GamaGeometryType.buildCircle(defaultSize, point);
	// break;
	// case 2:
	// ag = GamaGeometryType.buildSquare(defaultSize, point);
	// break;
	// case 3:
	// ag = GamaGeometryType.buildTriangle(defaultSize, point);
	// break;
	// case 4:
	// ag = GamaGeometryType.buildSphere(defaultSize, point);
	// break;
	// case 5:
	// ag = GamaGeometryType.buildCube(defaultSize, point);
	// break;
	// case 6:
	// ag = GamaGeometryType.createPoint(point);
	// break;
	// default:
	// ag = agent.getGeometry();
	// }
	// } else {
	// ag = agent.getGeometry();
	// }
	//
	// final IShape ag2 = ag.copy(scope);
	// final Rectangle2D r = g.drawGamaShape(scope, ag2, c, true, Color.black, false);
	// return r;
	// } catch (GamaRuntimeException e) {
	// // cf. Issue 1052: exceptions are not thrown, just displayed
	// e.printStackTrace();
	// } finally {
	// g.endHighlight();
	// agent.releaseLock();
	// }
	// }
	// return null;
	// }
	//
	// };
	//

	public static GamaColor borderColor = GamaColor.getInt(Color.black.getRGB());
	public static IExecutable DEFAULT_ASPECT = new IExecutable() {

		@Override
		public Rectangle2D executeOn(final IScope scope) throws GamaRuntimeException {
			IAgent agent = scope.getAgentScope();
			if ( agent != null && !agent.dead() ) {
				final IGraphics g = scope.getGraphics();
				if ( g == null ) { return null; }
				try {
					// agent.acquireLock();
					// if ( agent.dead() ) { return null; }
					if ( agent == scope.getGui().getHighlightedAgent() ) {
						g.beginHighlight();
					}
					final GamaColor c = agent.getSpecies().hasVar(IKeyword.COLOR)
						? Cast.asColor(scope, agent.getDirectVarValue(scope, IKeyword.COLOR))
							: GamaColor.getInt(GamaPreferences.CORE_COLOR.getValue().getRGB());
						String defaultShape = GamaPreferences.CORE_SHAPE.getValue();
						int index = SHAPES.get(defaultShape);
						IShape ag;

						if ( index != Constants.DEFAULT_INT_NO_ENTRY_VALUE ) {
							Double defaultSize = GamaPreferences.CORE_SIZE.getValue();
							ILocation point = agent.getLocation();

							switch (SHAPES.get(defaultShape)) {
								case 1:
									ag = GamaGeometryType.buildCircle(defaultSize, point);
									break;
								case 2:
									ag = GamaGeometryType.buildSquare(defaultSize, point);
									break;
								case 3:
									ag = GamaGeometryType.buildTriangle(defaultSize, point);
									break;
								case 4:
									ag = GamaGeometryType.buildSphere(defaultSize, point);
									break;
								case 5:
									ag = GamaGeometryType.buildCube(defaultSize, point);
									break;
								case 6:
									ag = GamaGeometryType.createPoint(point);
									break;
								default:
									ag = agent.getGeometry();
							}
						} else {
							ag = agent.getGeometry();
						}

						final IShape ag2 = ag.copy(scope);
						ShapeDrawingAttributes attributes = new ShapeDrawingAttributes(ag2, c, borderColor);
						final Rectangle2D r = g.drawShape(ag2, attributes);
						return r;
				} catch (GamaRuntimeException e) {
					// cf. Issue 1052: exceptions are not thrown, just displayed
					e.printStackTrace();
				} finally {
					g.endHighlight();
					// agent.releaseLock();
				}
			}
			return null;
		}

	};

	public AspectStatement(final IDescription desc) {
		super(desc);
		setName(getLiteral(IKeyword.NAME, IKeyword.DEFAULT));
		isHighlightAspect = getName().equals("highlighted");
	}

	@Override
	public Rectangle2D executeOn(final IScope scope) {
		IAgent agent = scope.getAgentScope();
		boolean shouldHighlight = agent == scope.getGui().getHighlightedAgent() && !isHighlightAspect;
		if ( agent != null && !agent.dead() ) {
			IGraphics g = scope.getGraphics();
			// hqnghi: try to find scope from experiment
			if ( g == null ) {
				g = GAMA.getExperiment().getAgent().getSimulation().getScope().getGraphics();
			}
			// end-hqnghi
			if ( g == null ) { return null; }
			try {
				// agent.acquireLock();
				if ( scope.interrupted() ) { return null; }
				if ( shouldHighlight ) {
					g.beginHighlight();
				}
				return (Rectangle2D) super.executeOn(scope);
				// Object[] result = new Object[1];
				// if ( scope.execute(this, agent, null, result) && result[0] instanceof Rectangle2D ) { return
				// (Rectangle2D) result[0]; }
				// return null;
			} catch (GamaRuntimeException e) {
				// cf. Issue 1052: exceptions are not thrown, just displayed
				e.printStackTrace();
			} finally {
				if ( shouldHighlight ) {
					g.endHighlight();
				}
				// agent.releaseLock();
			}

		}
		return null;

	}

	// @Override
	// public Rectangle2D drawOverlay(final IScope scope, final IAgent agent) throws GamaRuntimeException {
	// if ( agent != null ) {
	// final IGraphics g = scope.getGraphics();
	// if ( g == null ) { return null; }
	// try {
	// agent.acquireLock();
	// if ( agent.dead() ) { return null; }
	// final Color c =
	// agent.getSpecies().hasVar(IKeyword.COLOR) ? Cast.asColor(scope,
	// agent.getDirectVarValue(scope, IKeyword.COLOR)) : Color.YELLOW;
	// final IShape ag = agent.getGeometry();
	// final IShape ag2 = (IShape) ag.copy(scope);
	// final Rectangle2D r = g.drawGamaShapeOverlay(scope, ag2, c, true, Color.black, 0, false);
	// return r;
	// } finally {
	// agent.releaseLock();
	// }
	// }
	// return null;
	//
	// }

	@Override
	public Rectangle2D privateExecuteIn(final IScope stack) throws GamaRuntimeException {
		Rectangle2D result = null;
		for ( IStatement command : commands ) {
			final Object c = command.executeOn(stack);
			if ( result != null ) {
				if ( c instanceof Rectangle2D ) {
					result = result.createUnion((Rectangle2D) c);
				}
			} else if ( c instanceof Rectangle2D ) {
				result = (Rectangle2D) c;
			}
		}
		return result;
	}
}
