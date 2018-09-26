package org.openstreetmap.atlas.checks.validation.linear.edges;

import org.openstreetmap.atlas.geography.atlas.Atlas;
import org.openstreetmap.atlas.utilities.testing.CoreTestRule;
import org.openstreetmap.atlas.utilities.testing.TestAtlas;

/**
 * Test data for {@link MalformedPolyLineCheckTest}
 * 
 * @author sayas01
 */
public class MalformedPolyLineCheckTestRule extends CoreTestRule
{
    private static final String TEST_1 = "47.2136626201459,-122.443275382856";
    private static final String TEST_3 = "47.2136626201459,-122.441897992465";
    private static final String TEST_4 = "47.2138114677627,-122.440990166979";
    private static final String TEST_5 = "29.2601483, 48.1656914";
    private static final String TEST_6 = "29.2688082, 48.0907369";
    private static final String TEST_7 = "24.9865524, 55.0190518";

    @TestAtlas(
            // nodes
            nodes = { @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_1)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_3)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_5)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_7)) },
            // lines
            lines = {
                    @TestAtlas.Line(id = "1000000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_1) }, tags = { "highway=motorway" }),
                    @TestAtlas.Line(id = "1001000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_3),
                            @TestAtlas.Loc(value = TEST_4) }, tags = { "highway=motorway" }) })
    private Atlas singlePointPolyLineAtlas;
    @TestAtlas(
            // nodes
            nodes = { @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_1)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_3)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_5)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_7)) },
            // lines
            lines = {
                    @TestAtlas.Line(id = "1001000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_3), @TestAtlas.Loc(value = TEST_4),
                            @TestAtlas.Loc(value = TEST_5) }, tags = { "highway=motorway" }),
                    @TestAtlas.Line(id = "1002000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_5), @TestAtlas.Loc(value = TEST_6),
                            @TestAtlas.Loc(value = TEST_7) }, tags = { "highway=motorway" }) })
    private Atlas maxLengthPolyLineAtlas;
    @TestAtlas(
            // nodes
            nodes = { @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_1)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_3)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_5)),
                    @TestAtlas.Node(coordinates = @TestAtlas.Loc(value = TEST_7)) },
            // lines
            lines = {
                    @TestAtlas.Line(id = "1001000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_3), @TestAtlas.Loc(value = TEST_4),
                            @TestAtlas.Loc(value = TEST_5) }, tags = { "natural=coastline" }),
                    @TestAtlas.Line(id = "1002000001", coordinates = {
                            @TestAtlas.Loc(value = TEST_5), @TestAtlas.Loc(value = TEST_6),
                            @TestAtlas.Loc(value = TEST_7) }, tags = { "waterway=river" }) })
    private Atlas complexPolyLineAtlas;

    @TestAtlas(loadFromTextResource = "MalformedPolyLine.txt.gz")
    private Atlas malformedPolyLineAtlas;

    public Atlas getMalformedPolyLineAtlas()
    {
        return this.malformedPolyLineAtlas;
    }

    public Atlas getSinglePointAtlas()
    {
        return this.singlePointPolyLineAtlas;
    }

    public Atlas getMaxLengthAtlas()
    {
        return this.maxLengthPolyLineAtlas;
    }

    public Atlas getComplexPolyLineAtlas()
    {
        return this.complexPolyLineAtlas;
    }
}
