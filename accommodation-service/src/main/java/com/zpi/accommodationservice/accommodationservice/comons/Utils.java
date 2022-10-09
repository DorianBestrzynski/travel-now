package com.zpi.accommodationservice.accommodationservice.comons;

public interface Utils {
    String SERVICE_REGEX = "(airbnb\\.|booking\\.com)";
    Integer DEFAULT_VOTES = 0;
    String AIRBNB_CSS_QUERY = "body > script[id=data-deferred-state]";
    String BOOKING_CSS_QUERY = "head > script[type=application/ld+json]";
    String AIR_BNB_JSON_EXTRACTION_REGEX = "(?<=>)\\{.*(?=</script>)";
    String WHITESPACE = "\\s+";
    String EMPTY_STRING = "";
    String COMMA = "";
    String SECTION = "section";
    String NULL_STRING = "null";
    String SHARE_SAVE = "shareSave";
    String SECTION_DEPENDENCIES = "sectionDependencies";
    Character NEW_LINE = '\n';
    Character COLON = ':';
    String OR_WORD = "or";

}
