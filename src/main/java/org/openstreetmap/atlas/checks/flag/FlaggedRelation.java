package org.openstreetmap.atlas.checks.flag;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.atlas.geography.Location;
import org.openstreetmap.atlas.geography.PolyLine;
import org.openstreetmap.atlas.geography.atlas.items.Area;
import org.openstreetmap.atlas.geography.atlas.items.AtlasItem;
import org.openstreetmap.atlas.geography.atlas.items.AtlasObject;
import org.openstreetmap.atlas.geography.atlas.items.LocationItem;
import org.openstreetmap.atlas.geography.atlas.items.Relation;
import org.openstreetmap.atlas.geography.atlas.items.RelationMember;
import org.openstreetmap.atlas.geography.atlas.items.RelationMemberList;
import org.openstreetmap.atlas.utilities.collections.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A flag for a {@link Relation}
 *
 * @author sayas01
 */
public class FlaggedRelation extends FlaggedObject
{
    private static final Logger logger = LoggerFactory.getLogger(FlaggedPoint.class);
    private final Relation relation;
    private final Map<String, String> properties;

    public FlaggedRelation(final Relation relation)
    {
        this.relation = relation;
        this.properties = initProperties(relation);
    }

    /**
     * Get locations of all flattened members
     *
     * @return flagged geometry
     */
    @Override
    public Iterable<Location> getGeometry()
    {
        final List<Location> listOfLocation = new ArrayList<>();

        final Set<AtlasObject> flattenedMembers = this.flatten();
        if (flattenedMembers.size() == 1)
        {
            final AtlasObject object = flattenedMembers.iterator().next();
            if (object instanceof LocationItem)
            {
                return ((LocationItem) object).getLocation();
            }
        }
        for (final AtlasObject member : flattenedMembers)
        {
            if (member instanceof LocationItem)
            {
                listOfLocation.add(((LocationItem) member).getLocation());
            }
            else
            {
                if (member instanceof Area)
                {
                    // An Area's geometry doesn't include the end (same as start) location. To close
                    // the
                    // area boundary, we need to add the end location manually.
                    final List<Location> geometry = Iterables
                            .asList(((AtlasItem) member).getRawGeometry());
                    geometry.add(geometry.get(0));
                    listOfLocation.addAll(new PolyLine(geometry));
                }
                else
                {
                    listOfLocation.addAll(new PolyLine(((AtlasItem) member).getRawGeometry()));
                }
            }
        }
        final PolyLine polyLine = new PolyLine(listOfLocation);
        return polyLine;
    }

    /**
     * "Flattens" the relation by returning the set of non-Relation members. Adds any non-Relation
     * members to the set, then loops on any Relation members to add their non-Relation members as
     * well. Keeps track of Relations whose identifiers have already been operated on, so that
     * recursively defined relations don't cause problems.
     *
     * @return a Set of AtlasObjects all related to this Relation, with no Relations.
     */
    public Set<AtlasObject> flatten()
    {
        final Set<AtlasObject> relationMembers = new HashSet<>();
        final Deque<AtlasObject> toProcess = new LinkedList<>();
        final Set<Long> relationsSeen = new HashSet<>();
        AtlasObject polledMember;

        toProcess.add(this.relation);
        while (!toProcess.isEmpty())
        {
            polledMember = toProcess.poll();
            if (polledMember instanceof Relation)
            {
                if (relationsSeen.contains(polledMember.getIdentifier()))
                {
                    continue;
                }
                ((Relation) polledMember).members()
                        .forEach(member -> toProcess.add(member.getEntity()));
                relationsSeen.add(polledMember.getIdentifier());
            }
            else
            {
                relationMembers.add(polledMember);
            }
        }
        return relationMembers;
    }

    /**
     * Get flattened RelationMemberList
     * 
     * @return {@link RelationMemberList}
     */
    public RelationMemberList getflattenedRelationMembers()
    {

        final List<RelationMember> listOfMembers = new ArrayList<>();
        final Deque<RelationMember> toProcess = new LinkedList<>();
        final Set<Long> relationsSeen = new HashSet<>();
        RelationMember polledMember;
        this.relation.members().forEach(member -> toProcess.add(member));
        while (!toProcess.isEmpty())
        {
            polledMember = toProcess.poll();
            if (polledMember.getEntity() instanceof Relation)
            {
                if (relationsSeen.contains(polledMember.getEntity().getIdentifier()))
                {
                    continue;
                }
                ((Relation) polledMember.getEntity()).members()
                        .forEach(member -> toProcess.add(member));
                relationsSeen.add(polledMember.getEntity().getIdentifier());
            }
            else
            {
                listOfMembers.add(polledMember);
            }
        }
        return new RelationMemberList(listOfMembers);
    }

    public RelationMemberList members()
    {
        return this.relation.members();
    }

    /**
     * @return flag key-value property map
     */
    @Override
    public Map<String, String> getProperties()
    {
        return this.properties;
    }

    /**
     * Populate properties of relation
     *
     * @param relation
     * @return
     */
    private Map<String, String> initProperties(final Relation relation)
    {
        final Map<String, String> tags = relation.getTags();
        tags.put(ITEM_IDENTIFIER_TAG, relation.getIdentifier() + "");
        tags.put(OSM_IDENTIFIER_TAG, relation.getOsmIdentifier() + "");
        tags.put(ITEM_TYPE_TAG, "Relation");
        return tags;
    }
}
