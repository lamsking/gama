/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.views.actions;

import msi.gama.gui.swt.GamaIcons;
import msi.gama.gui.views.*;
import org.eclipse.jface.action.*;

/**
 * The class SnapshotItem.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public class ZoomOutItem extends GamaViewItem {

	/**
	 * @param view
	 */
	ZoomOutItem(final GamaViewPart view) {
		super(view);
		if ( !(view instanceof IViewWithZoom) ) { throw new IllegalArgumentException(); }
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IAction action = new GamaAction("Zoom out", "Zoom out", IAction.AS_PUSH_BUTTON, GamaIcons.action_zoomout) {

			@Override
			public void run() {

				IViewWithZoom view = (IViewWithZoom) getView();
				if ( view == null ) { return; }
				view.zoomOut();

			}
		};
		return new ActionContributionItem(action);
	}
}
