package org.openstreetmap.atlas.checks.validation.intersections;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.openstreetmap.atlas.checks.atlas.predicates.TagPredicates;
import org.openstreetmap.atlas.checks.atlas.predicates.TypePredicates;
import org.openstreetmap.atlas.checks.base.BaseCheck;
import org.openstreetmap.atlas.checks.flag.CheckFlag;
import org.openstreetmap.atlas.checks.utility.Utilities;
import org.openstreetmap.atlas.geography.Polygon;
import org.openstreetmap.atlas.geography.atlas.Atlas;
import org.openstreetmap.atlas.geography.atlas.items.Area;
import org.openstreetmap.atlas.geography.atlas.items.AtlasEntity;
import org.openstreetmap.atlas.geography.atlas.items.AtlasItem;
import org.openstreetmap.atlas.geography.atlas.items.AtlasObject;
import org.openstreetmap.atlas.geography.atlas.items.LineItem;
import org.openstreetmap.atlas.geography.atlas.items.Relation;
import org.openstreetmap.atlas.tags.BoundaryTag;
import org.openstreetmap.atlas.tags.BridgeTag;
import org.openstreetmap.atlas.tags.HighwayTag;
import org.openstreetmap.atlas.tags.IceRoadTag;
import org.openstreetmap.atlas.tags.LandUseTag;
import org.openstreetmap.atlas.tags.ManMadeTag;
import org.openstreetmap.atlas.tags.PowerTag;
import org.openstreetmap.atlas.tags.RouteTag;
import org.openstreetmap.atlas.tags.TunnelTag;
import org.openstreetmap.atlas.tags.WaterwayTag;
import org.openstreetmap.atlas.tags.annotations.validation.Validators;
import org.openstreetmap.atlas.utilities.collections.MultiIterable;
import org.openstreetmap.atlas.utilities.configuration.Configuration;

/**
 * Flags line items (edges or lines) that are crossing water bodies invalidly.
 * {@code LineCrossingWaterBodyCheck#canCrossWaterBody(AtlasItem)} and
 * {@code Utilities#haveExplicitLocationsForIntersections(Polygon, AtlasItem)} is used to decide
 * whether a crossing is valid or not.
 *
 * @author mertk
 * @author savannahostrowski
 */
public class LineCrossingWaterBodyCheck extends BaseCheck<Long>
{
    private static final String LINEAR_INSTRUCTION = "Linear item {0,number,#} is crossing water body invalidly.";
    private static final String WATERBODY_INSTRUCTION = "The water body with id {0,number,#} has invalid crossings.";
    private static final List<String> FALLBACK_INSTRUCTIONS = Arrays.asList(WATERBODY_INSTRUCTION,
            LINEAR_INSTRUCTION);
    private static final String ADDRESS_PREFIX_KEY = "addr";
    private static final String WAS = "was:";
    private static final long serialVersionUID = 6048659185833217159L;

    /**
     * Checks if given {@link AtlasItem} can cross a water body
     *
     * @param crossingItem
     *            {@link AtlasItem} crossing
     * @return whether given {@link AtlasItem} can cross a water body
     */
    private static boolean canCrossWaterBody(final AtlasEntity crossingItem)
    {
        // In the following cases, given item can cross a water body
        // It is a waterway
        return crossingItem.getTag(WaterwayTag.KEY).isPresent()
                // Item is a boundary
                || crossingItem.getTag(BoundaryTag.KEY).isPresent()
                // Item is referring to a land use
                || crossingItem.getTag(LandUseTag.KEY).isPresent()
                // Item has a bridge tag with value of YES, VIADUCT, AQUEDUCT, BOARDWALK, COVERED,
                // LOW_WATER_CROSSING or MOVABLE
                || Validators.isOfType(crossingItem, BridgeTag.class, BridgeTag.YES,
                        BridgeTag.VIADUCT, BridgeTag.AQUEDUCT, BridgeTag.BOARDWALK,
                        BridgeTag.COVERED, BridgeTag.LOW_WATER_CROSSING, BridgeTag.MOVABLE,
                        BridgeTag.SUSPENSION)
                // It is an embankment
                || TagPredicates.IS_EMBANKMENT.test(crossingItem)
                // It goes underwater
                || TagPredicates.GOES_UNDERWATER.test(crossingItem)
                // It goes underground
                || TagPredicates.GOES_UNDERGROUND.test(crossingItem)
                // It is a pier
                || TagPredicates.IS_PIER.test(crossingItem)
                // It is a tunnel
                || TunnelTag.isTunnel(crossingItem)
                // It is a power line
                || TagPredicates.IS_POWER_LINE.test(crossingItem)
                // It is a man-made feature and one of pier, breakwater, groyne, dyke, or embankment
                || Validators.isOfType(crossingItem, ManMadeTag.class, ManMadeTag.PIER,
                        ManMadeTag.BREAKWATER, ManMadeTag.EMBANKMENT, ManMadeTag.GROYNE,
                        ManMadeTag.DYKE)
                // It is a ferry route
                || RouteTag.isFerry(crossingItem)
                // It has a highway tag of proposed or construction
                || Validators.isOfType(crossingItem, HighwayTag.class, HighwayTag.PROPOSED,
                        HighwayTag.CONSTRUCTION)
                // It has a tag starting with addr
                || crossingItem.containsKeyStartsWith(Collections.singleton(ADDRESS_PREFIX_KEY))
                || Validators.isOfType(crossingItem, PowerTag.class, PowerTag.LINE,
                        PowerTag.MINOR_LINE)
                || Validators.isOfType(crossingItem, IceRoadTag.class, IceRoadTag.YES)
                || Validators.isOfType(crossingItem, ManMadeTag.class, ManMadeTag.PIPELINE)
                || crossingItem.getTags().containsKey("ford")
                        && crossingItem.getOsmTags().get("ford").equals("yes")
                || crossingItem.getOsmTags().containsKey("winter_road")
                        && crossingItem.getTags().get("winter_road").equals("yes")
                || crossingItem.getOsmTags().containsKey("snowmobile")
                        && crossingItem.getTags().get("snowmobile").equals("yes")
                || crossingItem.getOsmTags().containsKey("ski")
                        && crossingItem.getTags().get("ski").equals("yes");
    }

    public LineCrossingWaterBodyCheck(final Configuration configuration)
    {
        super(configuration);
    }

    @Override
    public boolean validCheckForObject(final AtlasObject object)
    {
        return TypePredicates.IS_AREA.test(object) && TagPredicates.IS_WATER_BODY.test(object);
    }

    @Override
    protected Optional<CheckFlag> flag(final AtlasObject object)
    {
        // First retrieve the crossing edges and lines
        final Area objectAsArea = (Area) object;
        final Polygon areaAsPolygon = objectAsArea.asPolygon();
        final Atlas atlas = object.getAtlas();
        final Iterable<AtlasEntity> crossingLinearItems = new MultiIterable<>(
                atlas.edgesIntersecting(areaAsPolygon), atlas.linesIntersecting(areaAsPolygon));

        // Assume there is no invalid crossing
        boolean hasInvalidCrossings = false;

        // Still let's create a flag in case of an invalid crossing
        final CheckFlag newFlag = new CheckFlag(getTaskIdentifier(object));
        newFlag.addObject(object);
        newFlag.addInstruction(this.getLocalizedInstruction(0, object.getOsmIdentifier()));

        // Go through crossing items and collect invalid crossings
        // NOTE: Due to way sectioning same OSM way could be marked multiple times here. However,
        // MapRoulette will display way-sectioned edges in case there is an invalid crossing.
        // Therefore, if an OSM way crosses a water body multiple times in separate edges, then
        // each edge will be marked explicitly.
        for (final AtlasEntity crossingLineItem : crossingLinearItems)
        {
            // If no OSM tags associated with line
            if (crossingLineItem instanceof LineItem)
            {
                final Map<String, String> osmTags = crossingLineItem.getOsmTags();
                if (osmTags.isEmpty())
                {
                    final Set<Relation> relations = crossingLineItem.relations();
                    // and it is not part of any relation, then infer it as part of a
                    // boundary/coastline
                    // relation that is not ingested in the atlas. Such items needn't be flagged
                    // or if the line is part of relation that has key natural or tag,
                    // 'place=village'
                    if (relations.isEmpty() || relations.stream()
                            .filter(relation -> relation.isMultiPolygon())
                            .filter(relation -> relation.getOsmTags().containsKey("natural")
                                    || relation.getOsmTags().containsKey("place")
                                    || relation.getOsmTags().containsKey("landuse")
                                    || relation.getOsmTags().containsKey("waterway"))
                            .count() != 0)
                    {
                        continue;
                    }
                }
                else if (hasCanCrossKeys(osmTags))
                {
                    continue;
                }
            }
            // Check whether crossing linear item can actually cross
            if (!(canCrossWaterBody(crossingLineItem)
                    || Utilities.haveExplicitLocationsForIntersections(areaAsPolygon,
                            (LineItem) crossingLineItem)))
            {
                // Update the flag
                newFlag.addObject(crossingLineItem);
                newFlag.addInstruction(
                        this.getLocalizedInstruction(1, crossingLineItem.getOsmIdentifier()));

                // Set indicator to make sure we return invalid crossings
                hasInvalidCrossings = true;
            }
        }

        // If there is an invalid crossing, return the previously created flag
        if (hasInvalidCrossings)
        {
            return Optional.of(newFlag);
        }

        return Optional.empty();
    }

    private boolean hasCanCrossKeys(final Map<String, String> osmTags)
    {
        return osmTags.containsKey(WAS + "power") || osmTags.containsKey(WAS + "admin_level")
                || osmTags.containsKey(WAS + "boundary") || osmTags.containsKey("note");
    }

    @Override
    protected List<String> getFallbackInstructions()
    {
        return FALLBACK_INSTRUCTIONS;
    }
}
