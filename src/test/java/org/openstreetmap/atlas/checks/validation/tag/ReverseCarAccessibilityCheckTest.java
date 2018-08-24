package org.openstreetmap.atlas.checks.validation.tag;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.openstreetmap.atlas.checks.configuration.ConfigurationResolver;
import org.openstreetmap.atlas.checks.validation.verifier.ConsumerBasedExpectedCheckVerifier;

/**
 * Tests for {@link ReverseCarAccessibilityCheck}.
 *
 * @author sayana
 */
public class ReverseCarAccessibilityCheckTest
{
    @Rule
    public ReverseCarAccessibilityCheckTestRule setup = new ReverseCarAccessibilityCheckTestRule();

    @Rule
    public ConsumerBasedExpectedCheckVerifier verifier = new ConsumerBasedExpectedCheckVerifier();

    @Test
    public void testCarAccessCarNavigable()
    {
        this.verifier.actual(this.setup.carAccessCarNavigableAtlas(),
                new ReverseCarAccessibilityCheck(ConfigurationResolver.emptyConfiguration()));
        this.verifier.globallyVerify(flags -> Assert.assertEquals(0, flags.size()));
    }

    @Test
    public void testCarAccessMetricHighway()
    {
        this.verifier.actual(this.setup.carAccessMetricHighwayAtlas(),
                new ReverseCarAccessibilityCheck(ConfigurationResolver.emptyConfiguration()));
        this.verifier.globallyVerify(flags -> Assert.assertEquals(6, flags.size()));
    }

    @Test
    public void testCarNavigableHighwayInstruction()
    {
        this.verifier.actual(this.setup.nonCarAccessCarNavigableAtlas(),
                new ReverseCarAccessibilityCheck(ConfigurationResolver.emptyConfiguration()));
        this.verifier.verify(
                flag -> Assert.assertFalse(flag.getInstructions().contains("non car-navigable")));
    }

    @Test
    public void testMetricHighwayInstruction()
    {
        this.verifier.actual(this.setup.carAccessMetricHighwayAtlas(),
                new ReverseCarAccessibilityCheck(ConfigurationResolver.emptyConfiguration()));
        this.verifier.verify(
                flag -> Assert.assertTrue(flag.getInstructions().contains("non car-navigable")));
    }

    @Test
    public void testNonCarAccessCarNavigable()
    {
        this.verifier.actual(this.setup.nonCarAccessCarNavigableAtlas(),
                new ReverseCarAccessibilityCheck(ConfigurationResolver.emptyConfiguration()));
        this.verifier.globallyVerify(flags -> Assert.assertEquals(6, flags.size()));
    }

    @Test
    public void testNonCarAccessMetricHighway()
    {
        this.verifier.actual(this.setup.nonCarAccessMetricHighwayAtlas(),
                new ReverseCarAccessibilityCheck(ConfigurationResolver.emptyConfiguration()));
        this.verifier.globallyVerify(flags -> Assert.assertEquals(0, flags.size()));
    }
}