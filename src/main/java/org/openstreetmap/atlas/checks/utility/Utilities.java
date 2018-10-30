package org.openstreetmap.atlas.checks.utility;

import java.util.Set;

import org.openstreetmap.atlas.geography.Location;
import org.openstreetmap.atlas.geography.PolyLine;
import org.openstreetmap.atlas.geography.Polygon;
import org.openstreetmap.atlas.geography.atlas.items.LineItem;

/**
 * @author cuthbertm
 * @author mkalender
 */
public final class Utilities
{
    /**
     * Verifies intersections of given {@link Polygon} and {@link LineItem} are explicit
     * {@link Location}s for both items
     *
     * @param areaCrossed
     *            {@link Polygon} being crossed
     * @param crossingItem
     *            {@link LineItem} crossing
     * @return whether given {@link Polygon} and {@link LineItem}'s intersections are actual
     *         {@link Location}s for both items
     */
    public static boolean haveExplicitLocationsForIntersections(final Polygon areaCrossed,
            final LineItem crossingItem)
    {
        // Find out intersections
        final PolyLine crossingItemAsPolyLine = crossingItem.asPolyLine();
        final Set<Location> intersections = areaCrossed.intersections(crossingItemAsPolyLine);

        // Verify intersections are explicit locations for both geometries
        for (final Location intersection : intersections)
        {
            if (!areaCrossed.contains(intersection)
                    || !crossingItemAsPolyLine.contains(intersection))
            {
                return false;
            }
        }
        return true;
    }

    private Utilities()
    {
    }
}
