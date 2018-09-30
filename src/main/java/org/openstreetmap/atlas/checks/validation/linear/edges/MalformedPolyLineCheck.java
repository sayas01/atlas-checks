package org.openstreetmap.atlas.checks.validation.linear.edges;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.openstreetmap.atlas.checks.base.BaseCheck;
import org.openstreetmap.atlas.checks.flag.CheckFlag;
import org.openstreetmap.atlas.geography.atlas.items.AtlasObject;
import org.openstreetmap.atlas.geography.atlas.items.Edge;
import org.openstreetmap.atlas.geography.atlas.items.Line;
import org.openstreetmap.atlas.geography.atlas.items.LineItem;
import org.openstreetmap.atlas.tags.NaturalTag;
import org.openstreetmap.atlas.tags.WaterTag;
import org.openstreetmap.atlas.tags.WaterwayTag;
import org.openstreetmap.atlas.utilities.collections.Iterables;
import org.openstreetmap.atlas.utilities.configuration.Configuration;
import org.openstreetmap.atlas.utilities.scalars.Distance;

/**
 * Flag lines that have only one point, or none, and the ones that are too long or too short.
 *
 * @author matthieun
 * @author cuthbertm
 */
public class MalformedPolyLineCheck extends BaseCheck<Long>
{
    private static final Distance MAXIMUM_LENGTH = Distance.kilometers(100);
    private static final int MAXIMUM_POINTS = 500;
    private static final String MAX_LENGTH_INSTRUCTION = "Line is {0}, which is longer than the maximum of {1}";
    private static final String MAX_POINTS_INSTRUCTION = "Line contains {0} points more than maximum of {1}";
    private static final String SHORT_LENGTH_INSTRUCTION = "Line is {0}, which is shorter than the minimum of {1}";
    private static final String MAX_POINTS_MAX_LENGTH_INSTRUCTION = "Line contains {0} points more than maximum of {1} and line is {2}, which is longer than the maximum of {3}";
    private static final List<String> FALLBACK_INSTRUCTIONS = Arrays.asList(MAX_POINTS_INSTRUCTION,
            MAX_LENGTH_INSTRUCTION, SHORT_LENGTH_INSTRUCTION, MAX_POINTS_MAX_LENGTH_INSTRUCTION);
    private static final long serialVersionUID = -6190296606600063334L;
    private static final int THREE = 3;

    public MalformedPolyLineCheck(final Configuration configuration)
    {
        super(configuration);
    }

    @Override
    public boolean validCheckForObject(final AtlasObject object)
    {
        return object instanceof Edge && ((Edge) object).isMasterEdge()
                || object instanceof Line && !this.isFlagged(object.getOsmIdentifier());
    }

    @Override
    protected Optional<CheckFlag> flag(final AtlasObject object)
    {
        final LineItem line = (LineItem) object;
        final Map<String, String> tags = line.getTags();
        final int numberPoints = Iterables.asList(line.getRawGeometry()).size();
        final Distance length = line.asPolyLine().length();
        // We exclude certain complex PolyLines from the check.
        if (isComplexPolyLine(tags) || isMemberOfRelationWithWaterTag(line))
        {
            return Optional.empty();
        }
        if (numberPoints > MAXIMUM_POINTS && length.isGreaterThan(MAXIMUM_LENGTH))
        {
            return Optional.of(createFlag(object, this.getLocalizedInstruction(THREE, numberPoints,
                    MAXIMUM_POINTS, length, MAXIMUM_LENGTH)));
        }
        if (numberPoints < 1 || numberPoints > MAXIMUM_POINTS)
        {
            return Optional.of(createFlag(object,
                    this.getLocalizedInstruction(0, numberPoints, MAXIMUM_POINTS)));
        }
        if (length.isGreaterThan(MAXIMUM_LENGTH))
        {
            return Optional.of(
                    createFlag(object, this.getLocalizedInstruction(1, length, MAXIMUM_LENGTH)));
        }
        else if (length.isLessThan(Distance.ONE_METER))
        {
            return Optional.of(createFlag(object,
                    this.getLocalizedInstruction(2, length, Distance.ONE_METER)));
        }
        return Optional.empty();
    }

    @Override
    protected List<String> getFallbackInstructions()
    {
        return FALLBACK_INSTRUCTIONS;
    }

    /**
     * Coastlines and rivers should be excluded from the check, as they can be irregular and long,
     * but still valid polylines. This method checks if a polyline is part of river or coastline.
     * 
     * @param tags
     * @return {@code true} if this object is meets the criteria for a complex polyline
     */
    private boolean isComplexPolyLine(final Map<String, String> tags)
    {
        final String naturalTagValue = tags.get(NaturalTag.KEY);
        final String waterwayTagValue = tags.get(WaterwayTag.KEY);
        return NaturalTag.COASTLINE.name().equalsIgnoreCase(naturalTagValue)
                // waterway=river
                || WaterwayTag.RIVER.name().equalsIgnoreCase(waterwayTagValue)
                // waterway=riverbank
                || WaterwayTag.RIVERBANK.name().equalsIgnoreCase(waterwayTagValue)
                // waterway=stream
                || WaterwayTag.STREAM.name().equalsIgnoreCase(waterwayTagValue)
                // waterway=canal
                || WaterwayTag.CANAL.name().equalsIgnoreCase(waterwayTagValue)
                // natural=water
                || NaturalTag.WATER.name().equalsIgnoreCase(naturalTagValue)
                        // water=river
                        && WaterTag.RIVER.name().equalsIgnoreCase(tags.get(WaterTag.KEY));
    }

    /**
     * Checks if {@link LineItem} is part of relation having WaterTag associated with it
     * 
     * @param line
     * @return {@code true} if the LineItem is part of relation with WaterTag
     */
    private boolean isMemberOfRelationWithWaterTag(final LineItem line)
    {
        return line.relations().stream().anyMatch(relation -> NaturalTag.WATER.name()
                .equalsIgnoreCase(relation.getTags().get(NaturalTag.KEY)));
    }
}
