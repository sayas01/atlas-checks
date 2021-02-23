# InvalidCharacterNameTagCheck

#### Description

The purpose of this check is to identify certain atlas objects with name or localized name tags that contain
characters that are deemed as invalid. A configurable taggable filter is used to idently valid atlas objects for this check and the following are considered as invalid characters in the name tags of those atlas objects:
1. Numbers [0-9]
2. Special characters +,#,$,%,^,&,*,@,~
3. Double quotes "" 
4. Smart quotes “”

#### Live Examples

1. This [lagoon](https://www.openstreetmap.org/relation/4063628) has double quotes in its name tag.
2. This [stream](https://www.openstreetmap.org/way/723274199) has a number in its name tag.
3. This [pond](https://www.openstreetmap.org/way/618591377) has double quotes in its name tag.
4. This [lake](https://www.openstreetmap.org/way/481285407) has number in its name tag.


#### Code Review

The check looks at Lines, Areas and Relations. A Taggable filter set in the configuration for the check is used to filter out valid Lines, Areas and Relations. Those atlas objects that pass through the filter are then checked for invalid characters in their name or localized name tags. 
If invalid characters are present, then the atlas object is flagged with appropriate instructions.

To learn more about the code, please look at the comments in the source code for the check.
[InvalidCharacterNameTagCheck.java](../../src/main/java/org/openstreetmap/atlas/checks/validation/tag/InvalidCharacterNameTagCheck.java)