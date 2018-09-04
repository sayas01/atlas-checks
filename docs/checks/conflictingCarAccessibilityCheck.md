# Conflicting Car Accessibility Check 

This check flags roads that have conflicting highway, transportation and access tags. A conflict in these tags occurs when any of the below conditions is met:
 * A car-navigable highway tag value combined with a restrictive car 
access tag value with no designated vehicle tags 
 * A non-car navigable highway tag value combined with an open car access tag value
 * A car-navigable highway tag value combined with `Access`=`YES` tag and with designated vehicle tags like `Motorcycle`,`Bus` etc.
#### Live Examples
1. The way [id:27605010](https://www.openstreetmap.org/way/27605010) has conflicting values between highway tag (highway=PATH) and motorcar tag values (motorcar=YES), which allows a car to drive. Normally, highway=PATH represents no access for cars and this tag combination is an example for non-car navigable highway with car access.
2. The way [id:409750479](https://www.openstreetmap.org/way/409750479) has conflicting values between highway tag (highway=SERVICE) and motorcar tag values (motorcar=NO), which prohibits cars to drive. The only transportation that is allowed here is the motorcycle which makes it a designated highway and so the access tag value should have been equal to NO instead of YES.
3. The way [id:369590090](https://www.openstreetmap.org/way/369590090) has conflicting values between highway tag (highway=RESIDENTIAL) and vehicle tag values (vehicle=NO), which prohibits cars to drive. It has bicycle=DESIGNATED tag to allow bicycles to use this road segment, which is conflicting with the highway tag value.
    
#### Code Review
In [Atlas](https://github.com/osmlab/atlas), OSM elements are represented as Edges, Points, Lines, Nodes & Relations; in our case, we’re are looking at [Edges](https://github.com/osmlab/atlas/blob/dev/src/main/java/org/openstreetmap/atlas/geography/atlas/items/Edge.java).
Our first goal is to validate the incoming Atlas object. Valid features for this check will satisfy the following conditions:
* Is an Edge
* Has a `Highway` tag
* `AccessTag` for the object is not "no"
* Has either `MotorVehicleTag` or `MotorcarTag` or `VehicleTag`
* Does not contain any conditional tags such as `motor_vehicle:conditional`, `vehicle:conditional` or `motorcar:conditional` tags
* Is a master edge
* Is not an OSM way that has already been flagged
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
The valid objects are then checked for car accessibility and designated vehicle use only tags. If the object meets any of the two conditions mentioned above for a conflict to  occur and does not have a designed use tag, it will be flagged. This
method also flags objects with specific vehicle use only tag (such tags can be given in the config file; if not specified, default set of tags in the source code would be considered) but have car navigable highway tag.
```java
    @Override
    protected Optional<CheckFlag> flag(final AtlasObject object)
    {
        boolean isOverridingTag = false;
        final Optional<Boolean> carAccessible = checkIfCarAccessible((Edge) object);
        if (!carAccessible.isPresent())
        {
            return Optional.empty();
        }
        for (final String tagKey : object.getOsmTags().keySet())
        {
            if (tagsFilter.contains(tagKey) && object.getTag(tagKey).get().equalsIgnoreCase(YES))
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
        else if (!isOverridingTag && carAccessible.get() && HighwayTag.isMetricHighway(object)
                && !HighwayTag.isCarNavigableHighway(object))
        {
            this.markAsFlagged(object.getOsmIdentifier());
            return Optional.of(this.createFlag(object,
                    this.getLocalizedInstruction(1, object.getOsmIdentifier())));
        }
        else if (!isOverridingTag && !carAccessible.get()
                && HighwayTag.isCarNavigableHighway(object))
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
