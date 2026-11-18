package com.nexo.integration.schemas;


import com.google.genai.types.Schema;
import com.google.genai.types.Type;

import java.util.Map;

public class GeminiTrailSchema {

    // Static says value belongs to the class and not an instance
    public static final Schema SUBTOPIC_SCHEMA = Schema.builder()
            .type(Type.Known.OBJECT)
            .properties(
                    Map.of(
                            "title",
                            Schema.builder()
                                    .type(Type.Known.STRING)
                                    .build(),

                            "description",
                            Schema.builder()
                                    .type(Type.Known.STRING)
                                    .build(),

                            "targetDate",
                            Schema.builder()
                                    .type(Type.Known.STRING)
                                    .build()
                    )
            )
            .required("title", "description", "targetDate")
            .build();

    public static final Schema SUBTOPICS_SCHEMA = Schema.builder()
            .type(Type.Known.ARRAY)
            .items(SUBTOPIC_SCHEMA)
            .build();

    public static final Schema TOPIC_SCHEMA = Schema.builder()
            .type(Type.Known.OBJECT)  // tell that is a JSON obj
            .properties(
                    Map.of(
                            "title",
                            Schema.builder()
                                    .type(Type.Known.STRING)
                                    .build(),

                            "description",
                            Schema.builder()
                                    .type(Type.Known.STRING)
                                    .build(),

                            "subTopics",
                            SUBTOPICS_SCHEMA,

                            "targetDate",
                            Schema.builder()
                                    .type(Type.Known.STRING)
                                    .build()
                    )
            )
            .required("title", "description", "targetDate")
            .build();

    public static final Schema TOPICS_SCHEMA = Schema.builder()
            .type(Type.Known.ARRAY)
            .items(TOPIC_SCHEMA) // array elements
            .build();

    public static final Schema TRAIL_SCHEMA = Schema.builder()
            .type(Type.Known.OBJECT)
            .properties(
                    Map.of(
                            "title",
                            Schema.builder()
                                    .type(Type.Known.STRING)
                                    .build(),

                            "description",
                            Schema.builder()
                                    .type(Type.Known.STRING)
                                    .build(),

                            "topics",
                            TOPICS_SCHEMA
                    )
            )
            .required("title", "description", "topics")
            .build();
}
