package org.openstreetmap.atlas.checks.event;

import org.openstreetmap.atlas.checks.flag.CheckFlag;
import org.openstreetmap.atlas.geography.atlas.Atlas;
import org.openstreetmap.atlas.tags.RelationTypeTag;
import org.openstreetmap.atlas.utilities.collections.Iterables;
import org.openstreetmap.atlas.utilities.testing.CoreTestRule;
import org.openstreetmap.atlas.utilities.testing.TestAtlas;
import org.openstreetmap.atlas.utilities.testing.TestAtlas.Area;
import org.openstreetmap.atlas.utilities.testing.TestAtlas.Edge;
import org.openstreetmap.atlas.utilities.testing.TestAtlas.Loc;
import org.openstreetmap.atlas.utilities.testing.TestAtlas.Node;
import org.openstreetmap.atlas.utilities.testing.TestAtlas.Relation;
import org.openstreetmap.atlas.utilities.testing.TestAtlas.Relation.Member;

/**
 * {@link CheckFlagGeoJsonProcessorTest} test data
 *
 * @author brian_l_davis
 */
public class CheckFlagGeoJsonProcessorTestRule extends CoreTestRule
{
    private static final String TEST_1 = "37.335310,-122.009566";
    private static final String TEST_2 = "37.3314171,-122.0304871";
    private static final String TEST_3 = "37.325440,-122.033948";
    private static final String TEST_4 = "37.332451,-122.028932";
    private static final String TEST_5 = "37.317585,-122.052138";
    private static final String TEST_6 = "37.390535,-122.031007";

    @TestAtlas(

            // GeoJson Points
            nodes = {

                    @Node(id = "1", coordinates = @Loc(value = TEST_1)),
                    @Node(id = "2", coordinates = @Loc(value = TEST_2)),
                    @Node(id = "3", coordinates = @Loc(value = TEST_3)),
                    @Node(id = "4", coordinates = @Loc(value = TEST_4)),
                    @Node(id = "5", coordinates = @Loc(value = TEST_5)),
                    @Node(id = "6", coordinates = @Loc(value = TEST_6)), },

            // GeoJson LineString
            edges = {

                    @Edge(id = "12", coordinates = { @Loc(value = TEST_1), @Loc(value = TEST_2),
                            @Loc(value = TEST_3) }, tags = { "highway=primary" }),
                    @Edge(id = "23", coordinates = { @Loc(value = TEST_4), @Loc(value = TEST_5),
                            @Loc(value = TEST_6) }, tags = { "highway=primary" }), },

            // GeoJson Polygon
            areas = {

                    @Area(coordinates = { @Loc(value = TEST_5), @Loc(value = TEST_2),
                            @Loc(value = TEST_4), @Loc(value = TEST_1),
                            @Loc(value = TEST_6) }, tags = { "building=yes" }) },
            // GeoJson Relation
            relations = { @Relation(id = "123", members = {
                    @Member(id = "12", type = "edge", role = RelationTypeTag.RESTRICTION_ROLE_FROM),
                    @Member(id = "2", type = "node", role = RelationTypeTag.RESTRICTION_ROLE_VIA),
                    @Member(id = "23", type = "edge", role = RelationTypeTag.RESTRICTION_ROLE_TO) }, tags = {
                            "restriction=no_u_turn" }) })
    private Atlas atlas;

    public CheckFlagEvent getCheckFlagEvent()
    {
        final CheckFlag flag = new CheckFlag("Test check flag");
        flag.addObject(Iterables.head(atlas.nodes()), "Flagged Node");
        flag.addObject(Iterables.head(atlas.edges()), "Flagged Edge");
        flag.addObject(Iterables.head(atlas.areas()), "Flagged Area");
        flag.addObject(Iterables.head(atlas.areas()), "Flagged Relation");

        final CheckFlagEvent event = new CheckFlagEvent("sample-name", flag);
        event.getCheckFlag().addInstruction("First instruction");
        event.getCheckFlag().addInstruction("Second instruction");
        return event;
    }
}
