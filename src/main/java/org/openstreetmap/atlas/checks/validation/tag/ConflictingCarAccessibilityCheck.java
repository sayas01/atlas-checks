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
import org.openstreetmap.atlas.tags.filters.TaggableFilter;
import org.openstreetmap.atlas.utilities.configuration.Configuration;

/**
 * This check flags {@link Edge}s with conflicting tag combinations resulting from combining
 * car-navigable/non-car-navigable highways with different modes of transportation tags. This check
 * flags the following tag combinations: 1) Car-navigable way with restricted car access and with no
 * designated vehicle use tags like {@link org.openstreetmap.atlas.tags.MotorcycleTag} 2)
 * Car-navigable way with restricted car access and with designated vehicle use tags like
 * {@link org.openstreetmap.atlas.tags.MotorcycleTag} 3) Non-car-navigable way with open car access
 * Car accessibility is determined by {@link #checkIfCarAccessible(Edge)}
 *
 * @author sayas01
 */
public class ConflictingCarAccessibilityCheck extends BaseCheck
{
    private static final String CAR_ACCESSIBLE_DESIGNATED_HIGHWAY_INSTRUCTION = "This OSM way {0,number,#} is designated for specific vehicles only, consider adding access=NO to prevent general use.";
    private static final String CAR_ACCESSIBLE_NON_CAR_NAVIGABLE_INSTRUCTION = "This OSM way  {0,number,#} has a non-car navigable highway tag value combined with an open car access tag value, please verify and make proper corrections if needed.";
    private static final String CAR_NAVIGABLE_RESTRICTED_ACCESS_HIGHWAY_INSTRUCTION = "This OSM way {0,number,#} has a car navigable highway tag value combined with a restrictive car access tag value, please verify and make proper corrections if needed.";
    private static final String CONDITIONAL = ":conditional";
    private static final List<String> FALLBACK_INSTRUCTIONS = Arrays.asList(
            CAR_NAVIGABLE_RESTRICTED_ACCESS_HIGHWAY_INSTRUCTION,
            CAR_ACCESSIBLE_NON_CAR_NAVIGABLE_INSTRUCTION,
            CAR_ACCESSIBLE_DESIGNATED_HIGHWAY_INSTRUCTION);
    private static final List<String> MODES_OF_TRANSPORTATION = Arrays
            .asList(MotorVehicleTag.KEY, VehicleTag.KEY, MotorcarTag.KEY);
    private static final String NO = "no";
    private static final String TAGS_OVERRIDING_ACCESSIBILITY_DEFAULT =
            "bus->yes|minibus->yes|motorcycle->yes|taxi->yes|tourist_bus->yes|share_taxi->yes|psv->yes|good->yes|hsv->yes|"
                    + "agricultural->yes|snowmobile->yes|hov->yes|public_transportation->yes|emergency->yes|disabled->yes|hazmat->yes";
    private static final String YES = "yes";
    private static final long serialVersionUID = 8896036998080132728L;
    private final TaggableFilter designatedVehicleTagFilter;

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
        this.designatedVehicleTagFilter = (TaggableFilter) configurationValue(configuration,
                "accessibility.overriding.tags", TAGS_OVERRIDING_ACCESSIBILITY_DEFAULT,
                value -> new TaggableFilter(value.toString()));
    }

    /**
     * This function validates if given {@link AtlasObject} is valid for the check. A valid
     * {@link AtlasObject} for this check must have the below properties: 1) Is an Edge 2) Has a
     * highway tag 3) AccessTag for the object is not "no" 4) Has either `MotorVehicleTag` or
     * `MotorcarTag` or `VehicleTag` and none of these are conditional tags 5) Is a master edge 6)
     * An edge with the same OSM id has not already been flagged.
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
                // Make sure that the object has either MotorVehicleTag or MotorcarTag or VehicleTag tags
                // and does not contain any conditional restrictions on these tags
                && isNotConditionalTag(object)
                // Make sure that only master edges are considered
                && ((Edge) object).isMasterEdge()
                // Make sure that the object has a Highway tag
                && Validators.isOfType(object, HighwayTag.class, HighwayTag.values())
                // Make sure that the object has not been previously flagged
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
        // Check if any one of the designated vehicle tag is present
        final boolean isOverridingTag = this.designatedVehicleTagFilter.test(object);
        final Optional<Boolean> carAccessible = checkIfCarAccessible((Edge) object);
        // If carAccessible is empty, do not consider the object for flagging
        if (!carAccessible.isPresent())
        {
            return Optional.empty();
        }
        // Checks if the object has designated highway tag combined with non car access and car
        // navigable highway
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
        else if (!isOverridingTag && !carAccessible.get()
                && HighwayTag.isCarNavigableHighway(object))
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
     * This function evaluates if the given {@link AtlasObject} is car accessible or not based on
     * the three transporation tags : {@link MotorcarTag}, {@link MotorVehicleTag} and
     * {@link VehicleTag}.
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

    /**
     * This is a helper function that evaluates if the given {@link AtlasObject} has any of
     * MotorVehicleTag or MotorcarTag or VehicleTag tags
     * and does not contain any conditional restrictions on these tags
     *
     * @param object the {@link AtlasObject} that needs to be checked for conditional tag on any of MotorVehicleTag or MotorcarTag or
     *               VehicleTag tags.
     * @return {@code true} if any of the transportation tags is present and does not have conditional tag associated with it
     */
    private boolean isNotConditionalTag(final AtlasObject object)
    {
        return MODES_OF_TRANSPORTATION
                .stream().anyMatch(tagKey -> object.getTag(tagKey).isPresent()
                        && !object.getTag(tagKey + CONDITIONAL).isPresent());
    }

}
