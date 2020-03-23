package com.cloudogu.scm.mytasks;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class SelfLinkSerializer extends JsonSerializer<String> {
  @Override
  public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeFieldName("self");

    jsonGenerator.writeStartObject();
    jsonGenerator.writeStringField("href", value);
    jsonGenerator.writeEndObject();

    jsonGenerator.writeEndObject();
  }
}
