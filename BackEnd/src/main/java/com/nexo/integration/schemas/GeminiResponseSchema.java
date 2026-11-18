package com.nexo.integration.schemas;
import com.google.genai.types.Schema;
import com.google.genai.types.Type;

import java.util.Map;

public class GeminiResponseSchema {

    public static final Schema RESPONSE_SCHEMA = Schema.builder()
            .type(Type.Known.OBJECT)
            .properties(
                    Map.of(
                            "studyTrail",
                            GeminiTrailSchema.TRAIL_SCHEMA,

                            "studyTest",
                            GeminiTestSchema.TEST_SCHEMA
                    )
            )
            .required("studyTrail", "studyTest")
            .build();
}
