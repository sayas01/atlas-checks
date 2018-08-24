package org.openstreetmap.atlas.checks.validation.tag;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.openstreetmap.atlas.checks.base.BaseCheck;
import org.openstreetmap.atlas.checks.flag.CheckFlag;
import org.openstreetmap.atlas.geography.atlas.items.AtlasObject;
import org.openstreetmap.atlas.geography.atlas.items.Edge;
import org.openstreetmap.atlas.tags.AccessTag;
import org.openstreetmap.atlas.tags.HighwayTag;
import org.openstreetmap.atlas.tags.MotorVehicleTag;
import org.openstreetmap.atlas.tags.MotorcarTag;
import org.openstreetmap.atlas.tags.VehicleTag;
import org.openstreetmap.atlas.tags.annotations.validation.Validators;
import org.openstreetmap.atlas.utilities.configuration.Configuration;

/**
 * Flags car navigable highways with non-car access and metric highways with car access.
 *
 * @author sayana
 */
public class ReverseCarAccessibilityCheck extends BaseCheck
{
    private static final String CAR_NAVIGABLE_HIGHWAY_INSTRUCTION = "This OSM way {0,number,#} is “car-navigable”"
            + " with limited car access tag, please verify and make proper correction if needed.";
    private static final String METRIC_HIGHWAY_INSTRUCTION = "This OSM way  {0,number,#} is “non car-navigable”"
            + " with access tag for cars, please verify and make proper correction if needed.";
    private static final List<String> FALLBACK_INSTRUCTIONS = Arrays
            .asList(CAR_NAVIGABLE_HIGHWAY_INSTRUCTION, METRIC_HIGHWAY_INSTRUCTION);
    private static final String YES = "yes";
    private static final long serialVersionUID = 8896036998080132728L;

    /**
     * Default constructor
     *
     * @param configuration
     *            {@link Configuration} required to construct any Check
     */
    ReverseCarAccessibilityCheck(final Configuration configuration)
    {
        super(configuration);
    }

    @Override
    public boolean validCheckForObject(final AtlasObject object)
    {
        return object instanceof Edge && !AccessTag.isNo(object)
                && (object.getTag(MotorVehicleTag.KEY).isPresent()
                || object.getTag(MotorcarTag.KEY).isPresent()
                || object.getTag(VehicleTag.KEY).isPresent())
                && Validators.isOfType(object, HighwayTag.class, HighwayTag.values());
    }

    @Override
    protected Optional<CheckFlag> flag(final AtlasObject object)
    {
        final boolean isAccessible = isCarAccessible((Edge) object);
        // Checks if navigable highway with non-car access
        if (isAccessible && HighwayTag.isMetricHighway(object))
        {
            this.markAsFlagged(object.getOsmIdentifier());
            return Optional.of(this.createFlag(object,
                    this.getLocalizedInstruction(1, object.getOsmIdentifier())));
        }
        // Checks if metric highway with car-access
        else if (!isCarAccessible((Edge) object)
                && HighwayTag.isCarNavigableHighway(((Edge) object).highwayTag()))
        {
            this.markAsFlagged(object.getOsmIdentifier());
            return Optional.of(this.createFlag(object,
                    this.getLocalizedInstruction(0, object.getOsmIdentifier())));
        }
        return Optional.empty();
    }

    @Override
    protected List<String> getFallbackInstructions()
    {
        return FALLBACK_INSTRUCTIONS;
    }

    protected boolean isCarAccessible(final Edge object)
    {
        if (object.getTag(MotorcarTag.KEY).isPresent())
        {
            return object.getTag(MotorcarTag.KEY).get().contentEquals(YES);
        }
        if (object.getTag(MotorVehicleTag.KEY).isPresent())
        {
            return object.getTag(MotorVehicleTag.KEY).get().matches(YES);
        }
        if (object.getTag(VehicleTag.KEY).isPresent())
        {
            return object.getTag(VehicleTag.KEY).get().matches(YES);
        }
        return false;
    }
}
