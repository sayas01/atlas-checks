package org.openstreetmap.atlas.checks.validation.intersections;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.openstreetmap.atlas.checks.configuration.ConfigurationResolver;
import org.openstreetmap.atlas.checks.validation.verifier.ConsumerBasedExpectedCheckVerifier;

/**
 * @author mkalender
 */
public class LineCrossingWaterBodyCheckTest
{
    private static LineCrossingWaterBodyCheck check = new LineCrossingWaterBodyCheck(
            ConfigurationResolver.emptyConfiguration());

    @Rule
    public LineCrossingWaterBodyCheckTestRule setup = new LineCrossingWaterBodyCheckTestRule();

    @Rule
    public ConsumerBasedExpectedCheckVerifier verifier = new ConsumerBasedExpectedCheckVerifier();

    @Test
    public void testInvalidCrossingItemsAtlas()
    {
        this.verifier.actual(this.setup.invalidCrossingItemsAtlas(), check);
        // this.verifier.verifyNotEmpty();
        this.verifier.globallyVerify(flags -> Assert.assertEquals(flags.size(), 1));
        this.verifier.verify(flag -> Assert.assertEquals(flag.getFlaggedObjects().size(), 3));
    }

    @Test
    public void testInvalidIntersectionItemsAtlas()
    {
        this.verifier.actual(this.setup.invalidIntersectionItemsAtlas(), check);
        this.verifier.verifyNotEmpty();
        this.verifier.globallyVerify(flags -> Assert.assertEquals(flags.size(), 1));
        this.verifier.verify(flag -> Assert.assertEquals(flag.getFlaggedObjects().size(), 2));
    }

    @Test
    public void testNoCrossingItemsAtlas()
    {
        this.verifier.actual(this.setup.noCrossingItemsAtlas(), check);
        this.verifier.verifyEmpty();
    }

    @Test
    public void testValidCrossingItemsAtlas()
    {
        this.verifier.actual(this.setup.validCrossingItemsAtlas(), check);
        this.verifier.verifyEmpty();
    }

    @Test
    public void testValidIntersectionItemsAtlas()
    {
        this.verifier.actual(this.setup.validIntersectionItemsAtlas(), check);
        this.verifier.verifyEmpty();
    }

    @Test
    public void testInvalidLineCrossingAtlas()
    {
        this.verifier.actual(this.setup.getInvalidLineCrossingAtlas(), check);
        this.verifier.globallyVerify(flags -> Assert.assertEquals(flags.size(), 1));
        this.verifier.verify(flag -> Assert.assertEquals(flag.getFlaggedObjects().size(), 5));
    }

    @Test
    public void testValidLineCrossingAtlas()
    {
        this.verifier.actual(this.setup.getValidLineCrossingAtlas(), check);
        this.verifier.globallyVerify(flags -> Assert.assertEquals(flags.size(), 1));
        this.verifier.verify(flag -> Assert.assertEquals(flag.getFlaggedObjects().size(), 5));
    }
}
