package org.openstreetmap.atlas.checks.validation.tag;

import org.openstreetmap.atlas.geography.atlas.Atlas;
import org.openstreetmap.atlas.utilities.testing.CoreTestRule;
import org.openstreetmap.atlas.utilities.testing.TestAtlas;
import org.openstreetmap.atlas.utilities.testing.TestAtlas.Line;
import org.openstreetmap.atlas.utilities.testing.TestAtlas.Loc;
import org.openstreetmap.atlas.utilities.testing.TestAtlas.Node;

/**
 * Test data for {@link InvalidCharacterNameTagCheckTest}
 *
 * @author sayas01
 */
public class InvalidCharacterNameTagCheckTestRule extends CoreTestRule
{
    private static final String TEST_1 = "20.538246,10.546134";
    private static final String TEST_2 = "20.535768,10.543755";
    private static final String TEST_3 = "20.535773, 10.548353";

    @TestAtlas(nodes = { @Node(id = "1000000", coordinates = @Loc(value = TEST_1)),
            @Node(id = "2000000", coordinates = @Loc(value = TEST_2)) }, lines = {
                    @Line(id = "1001000001", coordinates = { @Loc(value = TEST_1),
                            @Loc(value = TEST_2) }, tags = { "name=Rio Guamá 2",
                                    "natural=spring" }) })
    private Atlas numbersInNameTagAtlas;

    @TestAtlas(nodes = { @Node(id = "1000000", coordinates = @Loc(value = TEST_1)),
            @Node(id = "2000000", coordinates = @Loc(value = TEST_2)) }, lines = {
                    @Line(id = "1001000001", coordinates = { @Loc(value = TEST_1),
                            @Loc(value = TEST_2) }, tags = { "name:es=Rio Guamá 2",
                                    "waterway=river" }) })
    private Atlas numbersInLocalizedNameTagAtlas;

    @TestAtlas(nodes = { @Node(id = "1000000", coordinates = @Loc(value = TEST_1)),
            @Node(id = "2000000", coordinates = @Loc(value = TEST_2)) }, lines = {
                    @Line(id = "1001000001", coordinates = { @Loc(value = TEST_1),
                            @Loc(value = TEST_2) }, tags = { "name=Rio # Guamá ",
                                    "natural=spring" }) })
    private Atlas specialCharactersInNameTagAtlas;

    @TestAtlas(nodes = { @Node(id = "1000000", coordinates = @Loc(value = TEST_1)),
            @Node(id = "2000000", coordinates = @Loc(value = TEST_2)) }, lines = {
                    @Line(id = "1001000001", coordinates = { @Loc(value = TEST_1),
                            @Loc(value = TEST_2) }, tags = { "name:es=Rio #Guamá",
                                    "waterway=river" }) })
    private Atlas specialCharactersInLocalizedNameTagAtlas;

    @TestAtlas(nodes = { @Node(id = "1000000", coordinates = @Loc(value = TEST_1)),
            @Node(id = "2000000", coordinates = @Loc(value = TEST_2)) }, lines = {
                    @Line(id = "1001000001", coordinates = { @Loc(value = TEST_1),
                            @Loc(value = TEST_2) }, tags = { "name=Rio \"Guamá\"",
                                    "natural=spring" }) })
    private Atlas doubleQuotesInNameTagAtlas;

    @TestAtlas(nodes = { @Node(id = "1000000", coordinates = @Loc(value = TEST_1)),
            @Node(id = "2000000", coordinates = @Loc(value = TEST_2)) }, lines = {
                    @Line(id = "1001000001", coordinates = { @Loc(value = TEST_1),
                            @Loc(value = TEST_2) }, tags = { "name:en=Rio \"Guamá\"",
                                    "waterway=river" }) })
    private Atlas doubleQuotesInLocalizedNameTagAtlas;

    @TestAtlas(nodes = { @Node(id = "1000000", coordinates = @Loc(value = TEST_1)),
            @Node(id = "2000000", coordinates = @Loc(value = TEST_2)) }, lines = {
                    @Line(id = "1001000001", coordinates = { @Loc(value = TEST_1),
                            @Loc(value = TEST_2) }, tags = { "name=Rio “Guamá”",
                                    "natural=spring" }) })
    private Atlas smartQuotesInNameTagAtlas;

    @TestAtlas(nodes = { @Node(id = "1000000", coordinates = @Loc(value = TEST_1)),
            @Node(id = "2000000", coordinates = @Loc(value = TEST_2)) }, lines = {
                    @Line(id = "1001000001", coordinates = { @Loc(value = TEST_1),
                            @Loc(value = TEST_2) }, tags = { "name=Rio “Guamá",
                                    "name:es=Rio “Guamá” ", "natural=spring" }) })
    private Atlas smartQuotesInLocalizedNameTagAtlas;

    public Atlas getDoubleQuotesInLocalizedNameTagAtlas()
    {
        return this.doubleQuotesInLocalizedNameTagAtlas;
    }

    public Atlas getDoubleQuotesInNameTagAtlas()
    {
        return this.doubleQuotesInNameTagAtlas;
    }

    public Atlas getNumbersInLocalizedNameTagAtlas()
    {
        return this.numbersInLocalizedNameTagAtlas;
    }

    public Atlas getNumbersInNameTagAtlas()
    {
        return this.numbersInNameTagAtlas;
    }

    public Atlas getSmartQuotesInLocalizedNameTagAtlas()
    {
        return this.smartQuotesInLocalizedNameTagAtlas;
    }

    public Atlas getSmartQuotesInNameTagAtlas()
    {
        return this.smartQuotesInNameTagAtlas;
    }

    public Atlas getSpecialCharactersInLocalizedNameTagAtlas()
    {
        return this.specialCharactersInLocalizedNameTagAtlas;
    }

    public Atlas getSpecialCharactersInNameTagAtlas()
    {
        return this.specialCharactersInNameTagAtlas;
    }
}
