package org.openapitools.codegen.languages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.CodegenConfigurator;

public class IssueCodegenTest extends AbstractCodegenTest {

	@DisplayName("model without micronaut annotations")
	@Test
	void modelWithoutMicronaut() {
		generate(configurator("src/test/resources/openapi/issue-303.yaml", "issue._303").setGenerateAliasAsModel(true));
	}

	@DisplayName("model with additional properties does not include properties in hashcode/equals")
	@Test
	void modelWithAdditionapProperties() {
		generate(configurator("src/test/resources/openapi/issue-315.yaml", "issue._315").setGenerateAliasAsModel(true));
	}

	@DisplayName("model with suffix")
	@Test
	void modelWithSuffix() {
		generate(configurator("src/test/resources/openapi/issue-318.yaml", "issue._318").setModelNameSuffix("Dto"));
	}

	@DisplayName("model with additional properties and multiple properties generate invalid hashcode/equals")
	@Test
	void modelWithAdditionapPropertiesAndMultipleProperties() {
		generate(configurator("src/test/resources/openapi/issue-336.yaml", "issue._336").setGenerateAliasAsModel(true));
	}

	@DisplayName("model with arrays items with validation")
	@Test
	void modelWithArrayItemsWithValidation() {
		var configurator = configurator("src/test/resources/openapi/issue-331.yaml", "issue._331");
		generate(configurator);
		generate(configurator.addAdditionalProperty("clientId", "test"));
	}

	@DisplayName("model with simple inheritance has problems with deserializaion")
	@Test
	void modelDeserializaion() {
		generate(configurator("src/test/resources/openapi/issue-356.yaml", "issue._356"));
	}

	@DisplayName("enum with removeEnumValuePrefix=true")
	@Test
	void enumWithRemoveEnumValuePrefixTrue() {
		generate(configurator("src/test/resources/openapi/issue-363.yaml", "issue._363._true")
				.addAdditionalProperty(CodegenConstants.REMOVE_ENUM_VALUE_PREFIX, "true"));
	}

	@DisplayName("enum with removeEnumValuePrefix=false")
	@Test
	void enumWithRemoveEnumValuePrefixFalse() {
		generate(configurator("src/test/resources/openapi/issue-363.yaml", "issue._363._false")
				.addAdditionalProperty(CodegenConstants.REMOVE_ENUM_VALUE_PREFIX, "false"));
	}

	@DisplayName("api parameter with snake_case")
	@Test
	void apiParamWithSnakeCase() {
		generate(configurator("src/test/resources/openapi/issue-366.yaml", "issue._366"));
	}

	@DisplayName("api with optional and validation annotations")
	@Test
	void apiWithParametersWithOptionalAndValidation() {
		generate(configurator("src/test/resources/openapi/issue-372.yaml", "issue._372"));
	}

	@DisplayName("model with inner and external enum values")
	@Test
	void modelWithInnerAndExternalEnumValues() {
		generate(configurator("src/test/resources/openapi/issue-407.yaml", "issue._407"));
	}

	@DisplayName("model with prefix")
	@Test
	void modelWithPrefix() {
		generate(configurator("src/test/resources/openapi/issue-414.yaml", "issue._414")
				.addAdditionalProperty(MicronautCodegen.SERDEABLE, false)
				.addAdditionalProperty(CodegenConstants.API_NAME_PREFIX, "ApiPrefix")
				.addAdditionalProperty(CodegenConstants.API_NAME_SUFFIX, "ApiSuffix")
				.addAdditionalProperty(CodegenConstants.MODEL_NAME_PREFIX, "ModelPrefix")
				.addAdditionalProperty(CodegenConstants.MODEL_NAME_SUFFIX, "ModelSuffix"));
	}

	@DisplayName("model parent has inner eum, should not created for childs")
	@Test
	void modelWithInnerEnum() {
		generate(configurator("src/test/resources/openapi/issue-432.yaml", "issue._432"));
	}

	static void generate(CodegenConfigurator configurator) {
		var gen = new DefaultGenerator();
		gen.setGenerateMetadata(false);
		gen.setGeneratorPropertyDefault(CodegenConstants.SUPPORTING_FILES, "false");
		gen.setGeneratorPropertyDefault(CodegenConstants.APIS, "true");
		gen.setGeneratorPropertyDefault(CodegenConstants.MODELS, "true");
		gen.opts(configurator.toClientOptInput()).generate();
	}
}
