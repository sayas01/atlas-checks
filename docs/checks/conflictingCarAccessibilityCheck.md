# Conflicting Car Accessibility Check 

This check flags roads that have conflicting highway and transportation tags. A conflict in these tags occurs when either of the two conditions is met:
 * A car navigable highway tag value combined with a restrictive car 
access tag value 
 * A non-car navigable highway tag value combined with an open car access tag value
#### Live Examples
1. The way [id:369590090](https://www.openstreetmap.org/way/369590090) has conflicting values between `HighwayTag` (`highway`=`RESIDENTIAL`) and `VehicleTag`(`vehicle`=`NO`). This has `bicyle`=`DESIGNATED` and so the `Highway` tag should have been `highway=CYCLEWAY`.
#### Code Review
In [Atlas](https://github.com/osmlab/atlas), OSM elements are represented as Edges, Points, Lines, Nodes & Relations; in our case, we’re are looking at [Edges](https://github.com/osmlab/atlas/blob/dev/src/main/java/org/openstreetmap/atlas/geography/atlas/items/Edge.java).
Our first goal is to validate the incoming Atlas object. Valid features for this check will satisfy the following conditions:
* Is an Edge
* Has a `highway` tag
* `AccessTag` for the object is not "no"
* Has either `MotorVehicleTag` or `MotorcarTag` or `VehicleTag` and none of these are conditional tags
* Is a master edge
* Is not a way sectioned duplicate
```java
    @Override
  @Override
    public boolean validCheckForObject(final AtlasObject object)
    {
        return object instanceof Edge
                && !AccessTag.isNo(object)
                && ((object.getTag(MotorVehicleTag.KEY).isPresent()
                        && !object.getTag(MotorVehicleTag.KEY + CONDITIONAL).isPresent())
                        || (object.getTag(MotorcarTag.KEY).isPresent()
                                && !object.getTag(MotorcarTag.KEY + CONDITIONAL).isPresent())
                        || (object.getTag(VehicleTag.KEY).isPresent())
                                && !object.getTag(VehicleTag.KEY + CONDITIONAL).isPresent())
                && ((Edge) object).isMasterEdge()
                && Validators.isOfType(object, HighwayTag.class, HighwayTag.values())
                && !this.isFlagged(object.getOsmIdentifier());
    }
```
The valid objects are then checked for car accessibility. If the object meets any of the two conditions mentioned above for a conflict to  occur, it will be flagged.
```java
    @Override
    protected Optional<CheckFlag> flag(final AtlasObject object)
    {
        final Optional<Boolean> carAccessible = checkIfCarAccessible((Edge) object);
        if (!carAccessible.isPresent())
        {
            return Optional.empty();
        }
        if (carAccessible.get() && HighwayTag.isMetricHighway(object)
                && !HighwayTag.isCarNavigableHighway(object))
        {
            this.markAsFlagged(object.getOsmIdentifier());
            return Optional.of(this.createFlag(object,
                    this.getLocalizedInstruction(1, object.getOsmIdentifier())));
        }
        else if (!carAccessible.get() && HighwayTag.isCarNavigableHighway(object))
        {
            this.markAsFlagged(object.getOsmIdentifier());
            return Optional.of(this.createFlag(object,
                    this.getLocalizedInstruction(0, object.getOsmIdentifier())));
        }
        return Optional.empty();
    }
```
The following method validates if an `AtlasObject` is car accessible or not. Car accessibility is determined by the three transportation tags -
`MotorcarTag`,`MotorVehicleTag` and `VehicleTag`. If any one of the above is present, the tag's value will determine the car accessibility. The check for
the tag is based on the hierarchy - `MotorcarTag` -> `MotorVehicleTag` -> `VehicleTag`.
```java
    private Optional<Boolean> checkIfCarAccessible(final Edge object)
    {
        String tagValue = null;
        if (object.getTag(MotorcarTag.KEY).isPresent())
        {
            tagValue = object.getTag(MotorcarTag.KEY).get();
        }
        else if (object.getTag(MotorVehicleTag.KEY).isPresent())
        {
            tagValue = object.getTag(MotorVehicleTag.KEY).get();
        }
        else if (object.getTag(VehicleTag.KEY).isPresent())
        {
            tagValue = object.getTag(VehicleTag.KEY).get();
        }
        if (NO.equalsIgnoreCase(tagValue))
        {
            return Optional.of(false);
        }
        if (YES.equalsIgnoreCase(tagValue))
        {
            return Optional.of(true);
        }
        return Optional.empty();
    }
```
To learn more about the code, please look at the comments in the source code for the check.  
[ConflictingCarAccessibilityCheck](../../src/main/java/org/openstreetmap/atlas/checks/validation/tag/ConflictingCarAccessibilityCheck.java)
