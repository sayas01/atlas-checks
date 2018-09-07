package org.openstreetmap.atlas.checks.validation.tag;

import org.openstreetmap.atlas.geography.atlas.Atlas;
import org.openstreetmap.atlas.utilities.testing.CoreTestRule;
import org.openstreetmap.atlas.utilities.testing.TestAtlas;
import org.openstreetmap.atlas.utilities.testing.TestAtlas.Edge;
import org.openstreetmap.atlas.utilities.testing.TestAtlas.Loc;
import org.openstreetmap.atlas.utilities.testing.TestAtlas.Node;

/**
 * {@link ConflictingCarAccessibilityCheck} test data.
 *
 * @author sayas01
 */
public class ConflictingCarAccessibilityCheckTestRule extends CoreTestRule
{
    private static final String TEST_1 = "47.2136626201459,-122.443275382856";
    private static final String TEST_10 = "47.2136413561665,-122.436028431137";
    private static final String TEST_12 = "47.2132054427106,-122.44382320858";
    private static final String TEST_2 = "47.2138327316739,-122.44258668766";
    private static final String TEST_3 = "47.2136626201459,-122.441897992465";
    private static final String TEST_4 = "47.2138114677627,-122.440990166979";
    private static final String TEST_5 = "47.2136200921786,-122.44001973284";
    private static final String TEST_6 = "47.2135137721113,-122.439127559518";
    private static final String TEST_7 = "47.2136200921786,-122.438157125378";
    private static final String TEST_8 = "47.2136413561665,-122.437468430183";
    private static final String TEST_9 = "47.2137689399148,-122.436717126333";
    // Atlas to test car navigable edges with car access
    @TestAtlas(
            // nodes
            nodes = { @Node(coordinates = @Loc(value = TEST_1)),
                    @Node(coordinates = @Loc(value = TEST_3)),
                    @Node(coordinates = @Loc(value = TEST_5)),
                    @Node(coordinates = @Loc(value = TEST_4)),
                    @Node(coordinates = @Loc(value = TEST_6)),
                    @Node(coordinates = @Loc(value = TEST_7)),
                    @Node(coordinates = @Loc(value = TEST_8)), },
            // edges
            edges = { @Edge(id = "1006000001", coordinates = { @Loc(value = TEST_1),
                    @Loc(value = TEST_3) }, tags = { "highway=ROAD", "access=yes", "vehicle=yes" }),
                    @Edge(id = "1001000001", coordinates = { @Loc(value = TEST_3),
                            @Loc(value = TEST_4), @Loc(value = TEST_5) }, tags = { "highway=ROAD",
                                    "access=yes", "motorcar=yes" }),
                    @Edge(id = "1002000001", coordinates = { @Loc(value = TEST_5),
                            @Loc(value = TEST_6), @Loc(value = TEST_7) }, tags = { "highway=ROAD",
                                    "access=yes", "motor_vehicle=yes" }),
                    @Edge(id = "1008000001", coordinates = { @Loc(value = TEST_3),
                            @Loc(value = TEST_8) }, tags = { "highway=ROAD", "access=yes",
                                    "motor_vehicle=yes", "vehicle=no" }),
                    @Edge(id = "1009000001", coordinates = { @Loc(value = TEST_5),
                            @Loc(value = TEST_8) }, tags = { "highway=ROAD", "access=yes",
                                    "vehicle=yes", "motorcar=yes", "motor_vehicle=no" }),
                    @Edge(id = "1001100001", coordinates = { @Loc(value = TEST_5),
                            @Loc(value = TEST_8) }, tags = { "highway=ROAD", "access=yes",
                                    "vehicle=no", "motorcar=yes", "motor_vehicle=no" }), })
    private Atlas carAccessCarNavigableAtlas;
    // Atlas to test non-car navigable edges with car access
    @TestAtlas(
            // nodes
            nodes = { @Node(coordinates = @Loc(value = TEST_1)),
                    @Node(coordinates = @Loc(value = TEST_3)),
                    @Node(coordinates = @Loc(value = TEST_5)),
                    @Node(coordinates = @Loc(value = TEST_4)),
                    @Node(coordinates = @Loc(value = TEST_6)),
                    @Node(coordinates = @Loc(value = TEST_7)),
                    @Node(coordinates = @Loc(value = TEST_8)), },
            // edges
            edges = {
                    @Edge(id = "1007000001", coordinates = { @Loc(value = TEST_1),
                            @Loc(value = TEST_3) }, tags = { "highway=STEPS", "access=yes",
                                    "vehicle=yes" }),
                    @Edge(id = "1001000001", coordinates = { @Loc(value = TEST_3),
                            @Loc(value = TEST_4), @Loc(value = TEST_5) }, tags = { "highway=STEPS",
                                    "access=yes", "motorcar=yes" }),
                    @Edge(id = "1002000001", coordinates = { @Loc(value = TEST_5),
                            @Loc(value = TEST_6), @Loc(value = TEST_7) }, tags = { "highway=STEPS",
                                    "access=yes", "motor_vehicle=yes" }),
                    @Edge(id = "1008000001", coordinates = { @Loc(value = TEST_3),
                            @Loc(value = TEST_8) }, tags = { "highway=FOOTWAY", "access=yes",
                                    "motor_vehicle=yes", "vehicle=no" }),
                    @Edge(id = "1009000001", coordinates = { @Loc(value = TEST_5),
                            @Loc(value = TEST_8) }, tags = { "highway=SERVICE", "access=yes",
                                    "vehicle=yes", "motorcar=yes", "bus=yes",
                                    "motor_vehicle=no" }), })
    private Atlas carAccessMetricHighwayAtlas;
    // Atlas to test designated use edges that are car navigable
    @TestAtlas(
            // nodes
            nodes = { @Node(coordinates = @Loc(value = TEST_1)),
                    @Node(coordinates = @Loc(value = TEST_3)),
                    @Node(coordinates = @Loc(value = TEST_5)),
                    @Node(coordinates = @Loc(value = TEST_4)),
                    @Node(coordinates = @Loc(value = TEST_6)),
                    @Node(coordinates = @Loc(value = TEST_2)), },

            // edges
            edges = {
                    @Edge(id = "1000000001", coordinates = { @Loc(value = TEST_1),
                            @Loc(value = TEST_2) }, tags = { "highway=MOTORWAY", "access=yes",
                                    "vehicle=no", "minibus=yes" }),
                    @Edge(id = "1001000001", coordinates = { @Loc(value = TEST_3),
                            @Loc(value = TEST_4) }, tags = { "highway=ROAD", "access=yes",
                                    "motorcar=no", "minibus=yes" }),
                    @Edge(id = "1002000001", coordinates = { @Loc(value = TEST_5),
                            @Loc(value = TEST_6) }, tags = { "highway=ROAD", "access=yes",
                                    "motor_vehicle=no", "minibus=yes" }), })
    private Atlas designatedUseCarNavigableAtlas;
    // Atlas to test car navigable edges with non-car access
    @TestAtlas(
            // nodes
            nodes = { @Node(coordinates = @Loc(value = TEST_1)),
                    @Node(coordinates = @Loc(value = TEST_3)),
                    @Node(coordinates = @Loc(value = TEST_5)),
                    @Node(coordinates = @Loc(value = TEST_4)),
                    @Node(coordinates = @Loc(value = TEST_6)),
                    @Node(coordinates = @Loc(value = TEST_10)),
                    @Node(coordinates = @Loc(value = TEST_2)),
                    @Node(coordinates = @Loc(value = TEST_9)),
                    @Node(coordinates = @Loc(value = TEST_12)) },

            // edges
            edges = {
                    @Edge(id = "1000000001", coordinates = { @Loc(value = TEST_1),
                            @Loc(value = TEST_2) }, tags = { "highway=MOTORWAY", "access=yes",
                                    "vehicle=no" }),
                    @Edge(id = "1001000001", coordinates = { @Loc(value = TEST_3),
                            @Loc(value = TEST_4) }, tags = { "highway=ROAD", "access=yes",
                                    "motorcar=no" }),
                    @Edge(id = "1002000001", coordinates = { @Loc(value = TEST_5),
                            @Loc(value = TEST_6) }, tags = { "highway=ROAD", "access=yes",
                                    "motor_vehicle=no" }),
                    @Edge(id = "1004000001", coordinates = { @Loc(value = TEST_9),
                            @Loc(value = TEST_10) }, tags = { "highway=ROAD", "access=yes",
                                    "motorcar=no", "vehicle=yes" }),
                    @Edge(id = "1006000001", coordinates = { @Loc(value = TEST_12),
                            @Loc(value = TEST_6) }, tags = { "highway=ROAD", "access=yes",
                                    "motor_vehicle=no", "vehicle=yes" }),
                    @Edge(id = "1007000001", coordinates = { @Loc(value = TEST_12),
                            @Loc(value = TEST_9) }, tags = { "highway=ROAD", "access=yes",
                                    "motor_vehicle=yes", "motorcar=no" }), })
    private Atlas nonCarAccessCarNavigableAtlas;
    // Atlas to test non-car navigable edges with non-car access
    @TestAtlas(
            // nodes
            nodes = { @Node(coordinates = @Loc(value = TEST_1)),
                    @Node(coordinates = @Loc(value = TEST_3)),
                    @Node(coordinates = @Loc(value = TEST_5)),
                    @Node(coordinates = @Loc(value = TEST_4)),
                    @Node(coordinates = @Loc(value = TEST_6)),
                    @Node(coordinates = @Loc(value = TEST_10)),
                    @Node(coordinates = @Loc(value = TEST_2)),
                    @Node(coordinates = @Loc(value = TEST_9)),
                    @Node(coordinates = @Loc(value = TEST_12)) },

            // edges
            edges = {
                    @Edge(id = "1000000001", coordinates = { @Loc(value = TEST_1),
                            @Loc(value = TEST_2) }, tags = { "highway=FOOTWAY", "access=yes",
                                    "vehicle=no" }),
                    @Edge(id = "1001000001", coordinates = { @Loc(value = TEST_3),
                            @Loc(value = TEST_4) }, tags = { "highway=FOOTWAY", "access=yes",
                                    "motorcar=no" }),
                    @Edge(id = "1002000001", coordinates = { @Loc(value = TEST_5),
                            @Loc(value = TEST_6) }, tags = { "highway=FOOTWAY", "access=yes",
                                    "motor_vehicle=no" }),
                    @Edge(id = "1004000001", coordinates = { @Loc(value = TEST_9),
                            @Loc(value = TEST_10) }, tags = { "highway=CYCLEWAY", "access=yes",
                                    "motorcar=no", "vehicle=yes" }),
                    @Edge(id = "1006000001", coordinates = { @Loc(value = TEST_12),
                            @Loc(value = TEST_6) }, tags = { "highway=PATH", "access=yes",
                                    "motor_vehicle=no", "vehicle=yes" }),
                    @Edge(id = "1007000001", coordinates = { @Loc(value = TEST_12),
                            @Loc(value = TEST_9) }, tags = { "highway=PEDESTRIAN", "access=yes",
                                    "motor_vehicle=yes", "motorcar=no" }), })
    private Atlas nonCarAccessMetricHighwayAtlas;

    public Atlas carAccessCarNavigableAtlas()
    {
        return this.carAccessCarNavigableAtlas;
    }

    public Atlas carAccessMetricHighwayAtlas()
    {
        return this.carAccessMetricHighwayAtlas;
    }

    public Atlas designatedUseCarNavigableAtlas()
    {
        return this.designatedUseCarNavigableAtlas;
    }

    public Atlas nonCarAccessCarNavigableAtlas()
    {
        return this.nonCarAccessCarNavigableAtlas;
    }

    public Atlas nonCarAccessMetricHighwayAtlas()
    {
        return this.nonCarAccessMetricHighwayAtlas;
    }
}
