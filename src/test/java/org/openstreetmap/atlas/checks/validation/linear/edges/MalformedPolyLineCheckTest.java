package org.openstreetmap.atlas.checks.validation.linear.edges;

import org.junit.Assert;
import org.junit.Test;
import org.openstreetmap.atlas.checks.configuration.ConfigurationResolver;
import org.openstreetmap.atlas.checks.flag.CheckFlag;
import org.openstreetmap.atlas.geography.Location;
import org.openstreetmap.atlas.geography.PolyLine;
import org.openstreetmap.atlas.geography.Rectangle;
import org.openstreetmap.atlas.geography.Segment;
import org.openstreetmap.atlas.geography.atlas.Atlas;
import org.openstreetmap.atlas.geography.atlas.packed.PackedAtlasBuilder;
import org.openstreetmap.atlas.utilities.collections.Iterables;
import org.openstreetmap.atlas.utilities.configuration.Configuration;
import org.openstreetmap.atlas.utilities.random.RandomTagsSupplier;

/**
 * @author matthieun
 */
public class MalformedPolyLineCheckTest
{
    private final Configuration configuration = ConfigurationResolver.emptyConfiguration();

    @Test
    public void testLength()
    {
        final PolyLine linePolyLine = new Segment(Location.TEST_2, Location.TEST_5);
        final PolyLine edgePolyLine = new Segment(Location.TEST_2, Location.EIFFEL_TOWER);

        final PackedAtlasBuilder builder = new PackedAtlasBuilder();

        builder.addNode(0, Location.TEST_2, RandomTagsSupplier.randomTags(5));
        builder.addNode(1, Location.EIFFEL_TOWER, RandomTagsSupplier.randomTags(5));

        builder.addLine(0, linePolyLine, RandomTagsSupplier.randomTags(5));

        builder.addEdge(0, edgePolyLine, RandomTagsSupplier.randomTags(5));

        final Atlas atlas = builder.get();

        final Iterable<CheckFlag> flags = new MalformedPolyLineCheck(this.configuration)
                .flags(atlas);
        final long size = Iterables.size(flags);
        Assert.assertEquals(1, size);

        Assert.assertEquals("0", flags.iterator().next().getIdentifier());
    }

    @Test
    public void testNumberPoints()
    {
        final Rectangle bounds = Rectangle.TEST_RECTANGLE;

        final PolyLine linePolyLine = PolyLine.random(600, bounds);
        final PolyLine edgePolyLine = new Segment(Location.TEST_2, Location.TEST_5);

        final PackedAtlasBuilder builder = new PackedAtlasBuilder();

        builder.addNode(0, Location.TEST_2, RandomTagsSupplier.randomTags(5));
        builder.addNode(1, Location.TEST_5, RandomTagsSupplier.randomTags(5));

        builder.addLine(0, linePolyLine, RandomTagsSupplier.randomTags(5));

        builder.addEdge(0, edgePolyLine, RandomTagsSupplier.randomTags(5));

        final Atlas atlas = builder.get();

        final Iterable<CheckFlag> flags = new MalformedPolyLineCheck(this.configuration)
                .flags(atlas);
        final long size = Iterables.size(flags);
        Assert.assertEquals(1, size);

        Assert.assertEquals("0", flags.iterator().next().getIdentifier());
    }
}
