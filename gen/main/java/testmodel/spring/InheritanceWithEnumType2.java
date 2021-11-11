package testmodel.spring;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import testmodel.spring.InheritanceWithEnumType;
import testmodel.spring.InheritanceWithEnumType1;
import testmodel.spring.InheritanceWithEnumType2;
import testmodel.spring.InheritanceWithStringType2AllOf;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * InheritanceWithEnumType2
 */
@java.lang.SuppressWarnings("all")

@JsonIgnoreProperties(
  value = "type", // ignore manually set type, it will be automatically generated by Jackson during serialization
  allowSetters = true // allows the type to be set during deserialization
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = InheritanceWithEnumType1.class, name = "Inheritance1"),
  @JsonSubTypes.Type(value = InheritanceWithEnumType2.class, name = "Inheritance2")
})

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class InheritanceWithEnumType2 extends InheritanceWithEnumType {

  @JsonProperty("b")
  private String b;

  public InheritanceWithEnumType2 b(String b) {
    this.b = b;
    return this;
  }

  /**
   * Get b
   * @return b
  */
  
  @Schema(name = "b", required = false)
  public String getB() {
    return b;
  }

  public void setB(String b) {
    this.b = b;
  }

  public InheritanceWithEnumType2 type(String type) {
    super.setType(type);
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InheritanceWithEnumType2 inheritanceWithEnumType2 = (InheritanceWithEnumType2) o;
    return Objects.equals(this.b, inheritanceWithEnumType2.b) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(b, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InheritanceWithEnumType2 {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    b: ").append(toIndentedString(b)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

