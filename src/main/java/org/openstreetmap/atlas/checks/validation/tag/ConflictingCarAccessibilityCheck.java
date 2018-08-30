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
 * This check flags {@link Edge} with conflicting tag combination resulting from combining
 * car-navigable/non-car-navigable highway with different modes of transportation tags.
 *
 * @author sayas01
 */
public class ConflictingCarAccessibilityCheck extends BaseCheck
{
    private static final String CAR_NAVIGABLE_HIGHWAY_INSTRUCTION = "This OSM way {0,number,#} has a car navigable highway tag value combined with a restrictive car access tag value, please verify and make proper corrections if needed.";
    private static final String CONDITIONAL = ":conditional";
    private static final String METRIC_HIGHWAY_INSTRUCTION = "This OSM way  {0,number,#} has a non-car navigable highway tag value combined with an open car access tag value, please verify and make proper corrections if needed.";
    private static final String NO = "no";
    private static final String OVERRIDING_TAG_INSTRUCTION = "This OSM way  {0,number,#} is designated for specific vehicle use, consider to add access=NO to prevent general uses.";
    private static final List<String> FALLBACK_INSTRUCTIONS = Arrays.asList(
            CAR_NAVIGABLE_HIGHWAY_INSTRUCTION, METRIC_HIGHWAY_INSTRUCTION,
            OVERRIDING_TAG_INSTRUCTION);
    private static final List<String> TAGS_OVERRIDING_ACCESSIBILITY_DEFAULT = Arrays.asList("bus",
            "minibus", "motorcycle", "psv", "public_transportation", "good", "hsv", "agricultural",
            "snowmobile", "hov", "emergency", "disabled", "hazmat");
    private static final String YES = "yes";
    private static final long serialVersionUID = 8896036998080132728L;
    private List<String> tagsFilter;

    /**
     * The default constructor that must be supplied. The Atlas Checks framework will generate the
     * checks with this constructor, supplying a configuration that can be used to adjust any
     * parameters that the check uses during operation.
     *
     * @param configuration the JSON configuration for this check
     */
    public ConflictingCarAccessibilityCheck(final Configuration configuration)
    {
        super(configuration);
        this.tagsFilter = (List<String>) configurationValue(configuration, "overriding.filter",
                TAGS_OVERRIDING_ACCESSIBILITY_DEFAULT);
    }

    /**
     * This function validates if given {@link AtlasObject} is valid for the check.
     *
     * @param object the {@link AtlasObject} supplied by the Atlas-Checks framework for evaluation
     * @return {@code true} if this object should be checked
     */
    @Override
    public boolean validCheckForObject(final AtlasObject object)
    {
        // Checks if the object is an instance of Edge
        return object instanceof Edge
                // Make sure that AccessTag for the object is not "no"
                && !AccessTag.isNo(object)
                // Make sure that the object has either MotorVehicleTag or MotorcarTag or VehicleTag
                // and does not contain any conditional restrictions on these tags
                && ((object.getTag(MotorVehicleTag.KEY).isPresent()
                && !object.getTag(MotorVehicleTag.KEY + CONDITIONAL).isPresent())
                || (object.getTag(MotorcarTag.KEY).isPresent()
                && !object.getTag(MotorcarTag.KEY + CONDITIONAL).isPresent())
                || (object.getTag(VehicleTag.KEY).isPresent())
                && !object.getTag(VehicleTag.KEY + CONDITIONAL).isPresent())
                // Make sure that only master edges are considered
                && ((Edge) object).isMasterEdge()
                // Make sure that the object has a Highway tag
                && Validators.isOfType(object, HighwayTag.class, HighwayTag.values())
                // Make sure that way sectioned duplicates are not considered
                && !this.isFlagged(object.getOsmIdentifier());
    }

    /**
     * This is the actual function that will check to see whether the object needs to be flagged.
     *
     * @param object the {@link AtlasObject} supplied by the Atlas-Checks framework for evaluation
     * @return an optional {@link CheckFlag} object that contains the problem object and
     * instructions on how to fix it, or the reason the object was flagged
     */
    @Override
    protected Optional<CheckFlag> flag(final AtlasObject object)
    {
        final Optional<Boolean> carAccessible = checkIfCarAccessible((Edge) object);
        boolean isOverridingTag = false;
        // If carAccessible is empty, do not consider the object for flagging
        if (!carAccessible.isPresent())
        {
            return Optional.empty();
        }
        for (final String string : object.getOsmTags().keySet())
        {
            if (tagsFilter.contains(string))
            {
                isOverridingTag = true;
                break;
            }
        }
        if (isOverridingTag && !carAccessible.get() && HighwayTag.isCarNavigableHighway(object))
        {
            this.markAsFlagged(object.getOsmIdentifier());
            return Optional.of(this.createFlag(object,
                    this.getLocalizedInstruction(2, object.getOsmIdentifier())));
        }
        // Checks if the object is tagged as metric highway that is non-car navigable but is car
        // accessible
        else if (!isOverridingTag && carAccessible.get() && HighwayTag.isMetricHighway(object)
                && !HighwayTag.isCarNavigableHighway(object))
        {
            this.markAsFlagged(object.getOsmIdentifier());
            return Optional.of(this.createFlag(object,
                    this.getLocalizedInstruction(1, object.getOsmIdentifier())));
        }
        // Checks if the object is tagged as car navigable but is non-car accessible
        else if (!carAccessible.get() && HighwayTag.isCarNavigableHighway(object))
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

    /**
     * This function evaluates if the given {@link AtlasObject} is car accessible or not.
     *
     * @param object the {@link AtlasObject} that needs to be checked for car accessibility.
     * @return an optional {@link Boolean} to indicate if the {@link AtlasObject} is car accessible
     * or not. If the Optional is empty, do not consider the {@link AtlasObject} for further
     * check
     */
    private Optional<Boolean> checkIfCarAccessible(final Edge object)
    {
        String tagValue = null;
        // If the object has MotorcarTag, then set tagValue to value of the MotorcarTag key
        if (object.getTag(MotorcarTag.KEY).isPresent())
        {
            tagValue = object.getTag(MotorcarTag.KEY).get();
        }
        // If the object has no MotorcarTag but has MotorVehicleTag key, then set tagValue to value
        // of the MotorVehicleTag key
        else if (object.getTag(MotorVehicleTag.KEY).isPresent())
        {
            tagValue = object.getTag(MotorVehicleTag.KEY).get();
        }
        // If the object has no MotorcarTag and MotorVehicleTag but has VehicleTag, then set
        // tagValue
        // to value of the VehicleTag key
        else if (object.getTag(VehicleTag.KEY).isPresent())
        {
            tagValue = object.getTag(VehicleTag.KEY).get();
        }
        // If the tagValue is "no" set optional boolean to false indicating the object is non-car
        // accessible
        if (NO.equalsIgnoreCase(tagValue))
        {
            return Optional.of(false);
        }
        // If the tagValue is "yes" set optional boolean to true indicating the object is car
        // accessible
        if (YES.equalsIgnoreCase(tagValue))
        {
            return Optional.of(true);
        }
        return Optional.empty();
    }
}
