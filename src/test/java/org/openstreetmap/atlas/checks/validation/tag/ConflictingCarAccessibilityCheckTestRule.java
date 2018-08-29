package org.openstreetmap.atlas.checks.validation.tag;

import org.openstreetmap.atlas.geography.atlas.Atlas;
import org.openstreetmap.atlas.utilities.testing.CoreTestRule;
import org.openstreetmap.atlas.utilities.testing.TestAtlas;

/**
 * {@link ConflictingCarAccessibilityCheck} test data.
 *
 * @author sayana
 */
public class ConflictingCarAccessibilityCheckTestRule extends CoreTestRule
{
    private static final String TEST_1 = "47.2136626201459,-122.443275382856";
    private static final String TEST_10 = "47.2136413561665,-122.436028431137";
    private static final String TEST_11 = "47.2141623212065,-122.443729295599";
    private static final String TEST_12 = "47.2132054427106,-122.44382320858";
    private static final String TEST_13 = "47.2132267068647,-122.435339735941";
    private static final String TEST_2 = "47.2138327316739,-122.44258668766";
    private static final String TEST_3 = "47.2136626201459,-122.441897992465";
    private static final String TEST_4 = "47.2138114677627,-122.440990166979";
    private static final String TEST_5 = "47.2136200921786,-122.44001973284";
    private static final String TEST_6 = "47.2135137721113,-122.439127559518";
    private static final String TEST_7 = "47.2136200921786,-122.438157125378";
    private static final String TEST_8 = "47.2136413561665,-122.437468430183";
    private static final String TEST_9 = "47.2137689399148,-122.436717126333";
    @TestAtlas(
            // nodes
            nodes = { @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_1)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_3)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_5)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_4)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_6)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_7)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_8)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_13)) },
            // edges
            edges = {
                    @TestAtlas.Edge(coordinates = { @TestAtlas.Loc(value = TEST_1),
                            @TestAtlas.Loc(value = TEST_3) }, tags = { "highway=ROAD", "access=yes",
                                    "vehicle=yes" }),
                    @TestAtlas.Edge(id = "1001000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_3), @TestAtlas.Loc(value = TEST_4),
                            @TestAtlas.Loc(value = TEST_5) }, tags = { "highway=ROAD", "access=yes",
                                    "motorcar=yes" }),
                    @TestAtlas.Edge(id = "1002000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_5), @TestAtlas.Loc(value = TEST_6),
                            @TestAtlas.Loc(value = TEST_7) }, tags = { "highway=ROAD", "access=yes",
                                    "motor_vehicle=yes" }),
                    @TestAtlas.Edge(id = "1008000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_3),
                            @TestAtlas.Loc(value = TEST_8) }, tags = { "highway=ROAD", "access=yes",
                                    "motor_vehicle=yes", "vehicle=no" }),
                    @TestAtlas.Edge(id = "1009000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_5),
                            @TestAtlas.Loc(value = TEST_8) }, tags = { "highway=ROAD", "access=yes",
                                    "vehicle=yes", "motorcar=yes", "motor_vehicle=no" }),
                    @TestAtlas.Edge(id = "1001100001", coordinates = {
                            @TestAtlas.Loc(value = TEST_5),
                            @TestAtlas.Loc(value = TEST_8) }, tags = { "highway=ROAD", "access=yes",
                                    "vehicle=no", "motorcar=yes", "motor_vehicle=no" }), })
    private Atlas carAccessCarNavigableAtlas;
    @TestAtlas(
            // nodes
            nodes = { @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_1)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_3)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_5)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_4)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_6)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_7)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_8)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_13)) },
            // edges
            edges = {
                    @TestAtlas.Edge(coordinates = { @TestAtlas.Loc(value = TEST_1),
                            @TestAtlas.Loc(value = TEST_3) }, tags = { "highway=STEPS",
                                    "access=yes", "vehicle=yes" }),
                    @TestAtlas.Edge(id = "1001000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_3), @TestAtlas.Loc(value = TEST_4),
                            @TestAtlas.Loc(value = TEST_5) }, tags = { "highway=STEPS",
                                    "access=yes", "motorcar=yes" }),
                    @TestAtlas.Edge(id = "1002000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_5), @TestAtlas.Loc(value = TEST_6),
                            @TestAtlas.Loc(value = TEST_7) }, tags = { "highway=STEPS",
                                    "access=yes", "motor_vehicle=yes" }),
                    @TestAtlas.Edge(id = "1008000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_3),
                            @TestAtlas.Loc(value = TEST_8) }, tags = { "highway=FOOTWAY",
                                    "access=yes", "motor_vehicle=yes", "vehicle=no" }),
                    @TestAtlas.Edge(id = "1009000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_5),
                            @TestAtlas.Loc(value = TEST_8) }, tags = { "highway=MOTORWAY",
                                    "access=yes", "vehicle=yes", "motorcar=yes",
                                    "motor_vehicle=no" }),
                    @TestAtlas.Edge(id = "1007000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_13),
                            @TestAtlas.Loc(value = TEST_1) }, tags = { "highway=PRIMARY",
                                    "access=yes", "motorcar=BUS", "motor_vehicle=yes" }), })
    private Atlas carAccessMetricHighwayAtlas;
    @TestAtlas(
            // nodes
            nodes = { @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_1)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_3)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_5)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_4)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_6)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_10)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_11)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_2)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_13)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_9)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_12)) },

            // edges
            edges = { @TestAtlas.Edge(id = "1000000001", coordinates = {
                    @TestAtlas.Loc(value = TEST_1), @TestAtlas.Loc(value = TEST_2) }, tags = {
                            "highway=MOTORWAY", "access=yes", "vehicle=no" }),
                    @TestAtlas.Edge(id = "1001000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_3),
                            @TestAtlas.Loc(value = TEST_4) }, tags = { "highway=ROAD", "access=yes",
                                    "motorcar=no" }),
                    @TestAtlas.Edge(id = "1002000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_5),
                            @TestAtlas.Loc(value = TEST_6) }, tags = { "highway=ROAD", "access=yes",
                                    "motor_vehicle=no" }),
                    @TestAtlas.Edge(id = "1004000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_9),
                            @TestAtlas.Loc(value = TEST_10) }, tags = { "highway=ROAD",
                                    "access=yes", "motorcar=no", "vehicle=yes" }),
                    @TestAtlas.Edge(id = "1006000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_12),
                            @TestAtlas.Loc(value = TEST_6) }, tags = { "highway=ROAD", "access=yes",
                                    "motor_vehicle=no", "vehicle=yes" }),
                    @TestAtlas.Edge(id = "1007000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_12),
                            @TestAtlas.Loc(value = TEST_9) }, tags = { "highway=ROAD", "access=yes",
                                    "motor_vehicle=yes", "motorcar=no" }), })
    private Atlas nonCarAccessCarNavigableAtlas;
    @TestAtlas(
            // nodes
            nodes = { @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_1)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_3)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_5)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_4)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_6)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_10)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_11)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_2)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_13)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_9)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_12)) },

            // edges
            edges = { @TestAtlas.Edge(id = "1000000001", coordinates = {
                    @TestAtlas.Loc(value = TEST_1), @TestAtlas.Loc(value = TEST_2) }, tags = {
                            "highway=FOOTWAY", "access=yes", "vehicle=no" }),
                    @TestAtlas.Edge(id = "1001000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_3),
                            @TestAtlas.Loc(value = TEST_4) }, tags = { "highway=FOOTWAY",
                                    "access=yes", "motorcar=no" }),
                    @TestAtlas.Edge(id = "1002000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_5),
                            @TestAtlas.Loc(value = TEST_6) }, tags = { "highway=FOOTWAY",
                                    "access=yes", "motor_vehicle=no" }),
                    @TestAtlas.Edge(id = "1004000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_9),
                            @TestAtlas.Loc(value = TEST_10) }, tags = { "highway=CYCLEWAY",
                                    "access=yes", "motorcar=no", "vehicle=yes" }),
                    @TestAtlas.Edge(id = "1006000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_12),
                            @TestAtlas.Loc(value = TEST_6) }, tags = { "highway=PATH", "access=yes",
                                    "motor_vehicle=no", "vehicle=yes" }),
                    @TestAtlas.Edge(id = "1007000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_12),
                            @TestAtlas.Loc(value = TEST_9) }, tags = { "highway=PEDESTRIAN",
                                    "access=yes", "motor_vehicle=yes", "motorcar=no" }), })
    private Atlas nonCarAccessMetricHighwayAtlas;

    public Atlas carAccessCarNavigableAtlas()
    {
        return this.carAccessCarNavigableAtlas;
    }

    public Atlas carAccessMetricHighwayAtlas()
    {
        return this.carAccessMetricHighwayAtlas;
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
