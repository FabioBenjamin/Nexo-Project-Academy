package com.nexo.integration.schemas;
import com.google.genai.types.Schema;
import com.google.genai.types.Type;

import java.util.Map;

public class GeminiTestSchema {

    public static final Schema ALTERNATIVE_SCHEMA = Schema.builder()
            .type(Type.Known.OBJECT)
            .properties(
                    Map.of(
                            "description",
                            Schema.builder()
                                    .type(Type.Known.STRING)
                                    .build(),

                            "isCorrect",
                            Schema.builder()
                                    .type(Type.Known.BOOLEAN)
                                    .build(),

                            "explanation",
                            Schema.builder()
                                    .type(Type.Known.STRING)
                                    .build()

                    )
            )
            .required("description", "isCorrect", "explanation")
            .build();

    public static final Schema ALTERNATIVES_SCHEMA = Schema.builder()
            .type(Type.Known.ARRAY)
            .items(ALTERNATIVE_SCHEMA)
            .minItems(5L)
            .maxItems(5L)
            .build();

    public static final Schema QUESTION_SCHEMA = Schema.builder()
            .type(Type.Known.OBJECT)
            .properties(
                    Map.of(
                            "statement",
                            Schema.builder()
                                    .type(Type.Known.STRING)
                                    .build(),

                            "alternatives",
                            ALTERNATIVES_SCHEMA
                    )
            )
            .required("statement", "alternatives")
            .build();

    public static final Schema QUESTIONS_SCHEMA = Schema.builder()
            .type(Type.Known.ARRAY)
            .items(QUESTION_SCHEMA)
            .minItems(10L)
            .maxItems(10L)
            .build();

    public static final Schema TEST_SCHEMA = Schema.builder()
            .type(Type.Known.OBJECT)
            .properties(
                    Map.of(
                            "questions",
                            QUESTIONS_SCHEMA
                    )
            )
            .required("questions")
            .build();
}
